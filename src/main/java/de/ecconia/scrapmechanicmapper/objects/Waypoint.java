package de.ecconia.scrapmechanicmapper.objects;

public class Waypoint
{
	public String label;
	public final int x, z;
	
	public Waypoint(String label, int x, int z)
	{
		this.label = label;
		this.x = x;
		this.z = z;
	}
}
