package de.ecconia.scrapmechanicmapper;

import de.ecconia.scrapmechanicmapper.objects.Line;
import de.ecconia.scrapmechanicmapper.objects.Waypoint;
import java.awt.Color;
import java.io.IOException;

public class Core implements AddressToPosition.PositionReceiver
{
	private final Storage storage;
	
	private boolean initial = true;
	private int lastX = -1000;
	private int lastZ = -1000;
	
	private int fixX;
	private int fixZ;
	
	private final GPSWindow window;
	
	private Color currentColor; //NULL -> Disabled on boot
	private AddressToPosition updater;
	
	public Core()
	{
		storage = new Storage("save.txt");
		try
		{
			storage.load();
		}
		catch(Exception e)
		{
			System.err.println("Could not load file, aborting for safety.");
			e.printStackTrace();
			System.exit(1);
		}
		
		window = new GPSWindow(this);
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			System.out.println("Saving...");
			try
			{
				storage.save();
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
				
				storage.addLine(new Line(fixX, fixZ, curX, curZ, currentColor));
				fixX = curX;
				fixZ = curZ;
			}
		}
	}
	
	public Storage getStorage()
	{
		return storage;
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
		storage.addWaypoint(new Waypoint(name, x, z));
	}
	
	public void updateAddresses(long addrA, long addrB)
	{
		if(updater != null)
		{
			updater.interrupt();
			try
			{
				updater.join(); //TODO: Timeout, properly handled.
			}
			catch(InterruptedException e)
			{
				e.printStackTrace(); //For real?? Common make these RuntimeEx.
			}
		}
		
		updater = new AddressToPosition(addrA, addrB, this);
		updater.start();
	}
}
