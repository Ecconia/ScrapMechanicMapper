package de.ecconia.scrapmechanicmapper;

import de.ecconia.scrapmechanicmapper.objects.Line;
import de.ecconia.scrapmechanicmapper.objects.Waypoint;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Storage
{
	private final File saveFile;
	
	private List<Line> lines = new ArrayList<>();
	private List<Waypoint> waypoints = new ArrayList<>();
	
	public Storage(String filename)
	{
		saveFile = new File(filename);
	}
	
	public void load() throws Exception
	{
		if(saveFile.exists())
		{
			BufferedReader reader = new BufferedReader(new FileReader(saveFile));
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
	
	public void save() throws IOException
	{
		FileWriter writer = new FileWriter(saveFile, false);
		
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
	
	//Data access:
	
	public List<Line> getLines()
	{
		return lines;
	}
	
	public void addLine(Line line)
	{
		lines.add(line);
	}
	
	public List<Waypoint> getWaypoints()
	{
		return waypoints;
	}
	
	public void addWaypoint(Waypoint waypoint)
	{
		waypoints.add(waypoint);
	}
	
	public void removeWaypoint(Waypoint waypoint)
	{
		waypoints.remove(waypoint);
	}
}
