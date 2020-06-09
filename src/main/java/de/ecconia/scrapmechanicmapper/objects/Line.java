package de.ecconia.scrapmechanicmapper.objects;

import java.awt.Color;

public class Line
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
