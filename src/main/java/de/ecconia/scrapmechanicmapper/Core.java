package de.ecconia.scrapmechanicmapper;

import de.ecconia.scrapmechanicmapper.objects.Line;
import de.ecconia.scrapmechanicmapper.objects.Waypoint;
import java.awt.Color;
import java.io.IOException;

public class Core implements AddressToPosition.PositionReceiver
{
	private final Storage storage;
	
	private boolean firstUpdateAfterLinking = true;
	private int lastUpdatedPositionX = -1000;
	private int lastUpdatedPositionZ = -1000;
	private int lineStartX;
	private int lineStartZ;
	
	private final GPSWindow window;
	
	/**
	 * The current color used to draw a line. If NULL then no line will be drawn.
	 */
	private Color currentLineColor; //NULL -> Disabled on boot
	private AddressToPosition positionUpdater;
	
	//TODO: Make the whole project more GUI, less console oriented.
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
		int currentX = (int) a;
		int currentZ = (int) b;
		if(firstUpdateAfterLinking)
		{
			firstUpdateAfterLinking = false;
			//Reset the positions to wherever the player is.
			lastUpdatedPositionX = lineStartX = currentX;
			lastUpdatedPositionZ = lineStartZ = currentZ;
		}
		else
		{
			if(lastUpdatedPositionX != currentX || lastUpdatedPositionZ != currentZ)
			{
				handleNewPosition(currentX, currentZ);
				//The window uses these current positions, thus update before the window.
				lastUpdatedPositionX = currentX;
				lastUpdatedPositionZ = currentZ;
				window.update();
			}
		}
	}
	
	private void handleNewPosition(int currentX, int currentZ)
	{
		if(currentLineColor != null)
		{
			int distanceLineStartX = lineStartX - currentX;
			int distanceLienStartZ = lineStartZ - currentZ;
			double dist = Math.sqrt(distanceLineStartX * distanceLineStartX + distanceLienStartZ * distanceLienStartZ);
			if(dist > 20)
			{
				storage.addLine(new Line(lineStartX, lineStartZ, currentX, currentZ, currentLineColor));
				lineStartX = currentX;
				lineStartZ = currentZ;
			}
		}
	}
	
	public Storage getStorage()
	{
		return storage;
	}
	
	public int getLastUpdatedPositionX()
	{
		return lastUpdatedPositionX;
	}
	
	public int getLastUpdatedPositionZ()
	{
		return lastUpdatedPositionZ;
	}
	
	public void setColor(Color color)
	{
		if(currentLineColor == null)
		{
			//Reset the line starting position.
			lineStartX = lastUpdatedPositionX;
			lineStartZ = lastUpdatedPositionZ;
		}
		currentLineColor = color;
	}
	
	public void addWaypoint(String name, int x, int z)
	{
		storage.addWaypoint(new Waypoint(name, x, z));
	}
	
	public void updateAddresses(long addressA, long addressB)
	{
		if(positionUpdater != null)
		{
			positionUpdater.interrupt();
			try
			{
				positionUpdater.join(); //TODO: Timeout, properly handled.
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		firstUpdateAfterLinking = true;
		positionUpdater = new AddressToPosition(addressA, addressB, this);
		positionUpdater.start();
	}
}
