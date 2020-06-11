package de.ecconia.scrapmechanicmapper.objects;

public class Waypoint
{
	
	private static long lastID = 0;
	
	public String label;
	public final int x, z;
	public final long id;
	
	public Waypoint(String label, int x, int z)
	{
		this.label = label;
		this.x = x;
		this.z = z;
		id = lastID;
		lastID ++;
	}
	
}
