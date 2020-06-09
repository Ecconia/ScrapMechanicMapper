package de.ecconia.scrapmechanicmapper;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Core implements AddressToPosition.PositionReceiver
{
	private boolean initial = true;
	private int lastX = -1000;
	private int lastZ = -1000;
	
	private List<Line> lines = new ArrayList<>();
	private List<Waypoint> waypoints = new ArrayList<>();
	
	private int fixX;
	private int fixZ;
	
	private final GPSWindow window;
	
	private Color currentColor = Color.blue;
	
	public Core()
	{
		try
		{
			File f = new File("save.txt");
			if(f.exists())
			{
				BufferedReader reader = new BufferedReader(new FileReader(f));
				String line = reader.readLine();
				if(!line.equals("1"))
				{
					System.err.println("Could not load file, not version 1.");
					System.exit(1);
				}
				while(true)
				{
					line = reader.readLine();
					if(line == null)
					{
						System.err.println("Could not load file, unexpected end of file.");
						System.exit(1);
					}
					if(line.isEmpty())
					{
						break;
					}
					String[] parts = line.split(" ");
					lines.add(new Line(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), new Color(Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), Integer.parseInt(parts[6]))));
				}
				
				while(true)
				{
					line = reader.readLine();
					if(line == null || line.isEmpty())
					{
						break;
					}
					String[] parts = line.split(" ", 3);
					waypoints.add(new Waypoint(parts[2], Integer.parseInt(parts[0]), Integer.parseInt(parts[1])));
				}
			}
		}
		catch(Exception e)
		{
			System.err.println("Could not load file, aborting for safety.");
			e.printStackTrace();
			System.exit(1);
		}
		
//		long address = 0x5048B9E8L;
//		AddressToPosition updater = new AddressToPosition(address + 4, address, this);
		
		window = new GPSWindow(this);
		
//		updater.start();
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			System.out.println("Saving...");
			try
			{
				File f = new File("save.txt");
				FileWriter writer = new FileWriter(f, false);
				
				writer.write("1\n");
				for(Line line : lines)
				{
					writer.write(line.x1 + " " + line.z1 + " " + line.x2 + " " + line.z2 + " " + line.color.getRed() + " " + line.color.getGreen() + " " + line.color.getBlue() + "\n");
				}
				writer.write("\n");
				for(Waypoint waypoint : waypoints)
				{
					writer.write(waypoint.x + " " + waypoint.z + " " + waypoint.label + "\n");
				}
				writer.flush();
				writer.close();
			}
			catch(IOException e)
			{
				System.err.println("Could not save data, sucks for you.");
				e.printStackTrace();
			}
			
		}));
	}
	
	@Override
	public void updatePosition(float a, float b)
	{
		int x = (int) a;
		int z = (int) b;
		if(initial)
		{
			initial = false;
			lastX = x;
			lastZ = z;
			fixX = x;
			fixZ = z;
		}
		else
		{
			if(lastX != x || lastZ != z)
			{
				move(lastX, lastZ, x, z);
				lastX = x;
				lastZ = z;
				window.update();
			}
		}
	}
	
	private void move(int lastX, int lastZ, int curX, int curZ)
	{
		if(currentColor != null)
		{
			int dx = fixX - curX;
			int dz = fixZ - curZ;
			double dist = Math.sqrt(dx * dx + dz * dz);
			if(dist > 20)
			{
				lines.add(new Line(fixX, fixZ, curX, curZ, currentColor));
				fixX = curX;
				fixZ = curZ;
			}
		}
	}
	
	public List<Line> getLines()
	{
		return lines;
	}
	
	public int getLastX()
	{
		return lastX;
	}
	
	public int getLastZ()
	{
		return lastZ;
	}
	
	public void setColor(Color color)
	{
		if(currentColor == null)
		{
			fixX = lastX;
			fixZ = lastZ;
		}
		currentColor = color;
	}
	
	public void addWaypoint(String name, int x, int z)
	{
		waypoints.add(new Waypoint(name, x, z));
	}
	
	public List<Waypoint> getWaypoints()
	{
		return waypoints;
	}
	
	public static class Waypoint
	{
		public final String label;
		public final int x, z;
		
		public Waypoint(String label, int x, int z)
		{
			this.label = label;
			this.x = x;
			this.z = z;
		}
	}
	
	public static class Line
	{
		public final int x1, x2, z1, z2;
		public final Color color;
		
		public Line(int x1, int z1, int x2, int z2, Color color)
		{
			this.x1 = x1;
			this.x2 = x2;
			this.z1 = z1;
			this.z2 = z2;
			this.color = color;
		}
	}
}
