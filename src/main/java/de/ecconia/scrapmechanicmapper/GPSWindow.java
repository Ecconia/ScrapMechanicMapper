package de.ecconia.scrapmechanicmapper;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

public class GPSWindow extends JFrame
{
	private final Core core;
	private final DrawPane pane;
	private final Controls controls;
	
	private Point alternateCenter;
	private int offsetX, offsetZ;
	private int lastOffsetX, lastOffsetZ;
	private Point pointerDown;
	
	boolean captureWaypoint;
	
	public GPSWindow(Core core)
	{
		this.core = core;
		
		setTitle("Path tracer");
		setPreferredSize(new Dimension(500, 500));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		getContentPane().setLayout(new BorderLayout());
		
		controls = new Controls();
		getContentPane().add(controls, BorderLayout.NORTH);
		
		pane = new DrawPane();
		getContentPane().add(pane, BorderLayout.CENTER);
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void update()
	{
		pane.repaint();
	}
	
	private class Controls extends JComponent
	{
		private JButton center;
		private List<JButton> colors = new ArrayList<>();
		
		public Controls()
		{
			setLayout(new FlowLayout());
			
			center = new JButton("Center");
			center.setEnabled(false);
			center.addActionListener((ActionEvent e) -> {
				center.setEnabled(false);
				
				offsetX = 0;
				offsetZ = 0;
				alternateCenter = null;
				
				pane.repaint();
			});
			add(center);
			
			//Street Gray
			JButton street = new JButton("Gray");
			street.setBackground(Color.darkGray);
			street.setForeground(Color.white);
			street.setFocusable(false);
			colors.add(street);
			street.addActionListener((ActionEvent e) -> {
				for(JButton button : colors)
				{
					button.setEnabled(true);
				}
				street.setEnabled(false);
				core.setColor(Color.darkGray);
			});
			add(street);
			//Grass grassland
			JButton grass = new JButton("Green");
			grass.setBackground(Color.green);
			grass.setForeground(Color.white);
			grass.setFocusable(false);
			colors.add(grass);
			grass.addActionListener((ActionEvent e) -> {
				for(JButton button : colors)
				{
					button.setEnabled(true);
				}
				grass.setEnabled(false);
				core.setColor(Color.green);
			});
			add(grass);
			//Other blue
			JButton other = new JButton("Blue");
			other.setBackground(Color.blue);
			other.setForeground(Color.white);
			other.setFocusable(false);
			other.setEnabled(false);
			colors.add(other);
			other.addActionListener((ActionEvent e) -> {
				for(JButton button : colors)
				{
					button.setEnabled(true);
				}
				other.setEnabled(false);
				core.setColor(Color.blue);
			});
			add(other);
			//Other Red
			JButton red = new JButton("Red");
			red.setBackground(Color.red);
			red.setForeground(Color.white);
			red.setFocusable(false);
			colors.add(red);
			red.addActionListener((ActionEvent e) -> {
				for(JButton button : colors)
				{
					button.setEnabled(true);
				}
				red.setEnabled(false);
				core.setColor(Color.red);
			});
			add(red);
			
			//OFF
			JButton off = new JButton("Off");
			off.setFocusable(false);
			colors.add(off);
			off.addActionListener((ActionEvent e) -> {
				for(JButton button : colors)
				{
					button.setEnabled(true);
				}
				off.setEnabled(false);
				core.setColor(null);
			});
			add(off);
			
			//OFF
			JButton waypoint = new JButton("+");
			waypoint.setFocusable(false);
			waypoint.addActionListener((ActionEvent e) -> {
				captureWaypoint = true;
			});
			add(waypoint);
		}
	}
	
	private class DrawPane extends JComponent
	{
		public DrawPane()
		{
			addMouseListener(new MouseListener()
			{
				@Override
				public void mouseClicked(MouseEvent e)
				{
				}
				
				@Override
				public void mousePressed(MouseEvent e)
				{
					if(captureWaypoint)
					{
						captureWaypoint = false;
						String name = JOptionPane.showInputDialog("Name of waypoint?");
						if(name != null && !name.trim().isEmpty())
						{
							name = name.trim();
							core.addWaypoint(name, core.getLastX() + offsetX - getWidth() / 2 + e.getX(), core.getLastZ() + offsetZ - getHeight() / 2 + e.getY());
							pane.repaint();
						}
					}
					else
					{
						pointerDown = e.getPoint();
						lastOffsetX = lastOffsetZ = 0;
					}
				}
				
				@Override
				public void mouseReleased(MouseEvent e)
				{
				}
				
				@Override
				public void mouseEntered(MouseEvent e)
				{
				}
				
				@Override
				public void mouseExited(MouseEvent e)
				{
				}
			});
			addMouseMotionListener(new MouseMotionListener()
			{
				@Override
				public void mouseDragged(MouseEvent e)
				{
					int x = e.getX();
					int z = e.getY();
					
					offsetX -= lastOffsetX;
					offsetZ -= lastOffsetZ;
					
					lastOffsetX = pointerDown.x - x;
					lastOffsetZ = pointerDown.y - z;
					
					offsetX += lastOffsetX;
					offsetZ += lastOffsetZ;
					
					if(offsetX != 0 || offsetZ != 0)
					{
						if(alternateCenter == null)
						{
							controls.center.setEnabled(true);
						}
						
						alternateCenter = new Point(core.getLastX() + offsetX, core.getLastZ() + offsetZ);
						
						GPSWindow.this.repaint();
					}
				}
				
				@Override
				public void mouseMoved(MouseEvent e)
				{
				}
			});
		}
		
		@Override
		public void paint(Graphics g)
		{
			int w = getWidth();
			int h = getHeight();
			
			Point realCenter = new Point(core.getLastX(), core.getLastZ());
			Point center = alternateCenter == null ? realCenter : alternateCenter;
			
			g.setColor(Color.white);
			g.fillRect(0, 0, w, h);
			
			int offX = center.x - w / 2;
			int offZ = center.y - h / 2;
			for(Core.Line line : core.getLines())
			{
				g.setColor(line.color);
				g.drawLine(line.x1 - offX, line.z1 - offZ, line.x2 - offX, line.z2 - offZ);
			}
			
			g.setColor(Color.red);
			for(Core.Waypoint waypoint : core.getWaypoints())
			{
				int x = waypoint.x - offX;
				int z = waypoint.z - offZ;
				g.drawOval(x - 3, z - 3, 6, 6);
				
				g.drawString(waypoint.label, x + 6, z + 4);
			}
			
			g.setColor(Color.black);
			g.drawOval(w / 2 - (center.x - realCenter.x) - 3, h / 2 - (center.y - realCenter.y) - 3, 6, 6);
		}
	}
}
