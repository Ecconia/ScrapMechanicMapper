package de.ecconia.scrapmechanicmapper;

import de.ecconia.scrapmechanicmapper.objects.Line;
import de.ecconia.scrapmechanicmapper.objects.Waypoint;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class GPSWindow extends JFrame
{
	private final Core core;
	private final DrawPane pane;
	private final Controls controls;
	private final Settings settingsPane;
	
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
		settingsPane = new Settings();
		getContentPane().add(settingsPane, BorderLayout.NORTH);
		
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
	
	private class Settings extends JComponent
	{
		public Settings()
		{
			setLayout(new FlowLayout());
			
			add(new JLabel("Addresses:"));
			JTextField a = new JTextField();
			a.setToolTipText("Address X.");
			JTextField b = new JTextField();
			b.setToolTipText("Address Z.");
			a.setPreferredSize(new Dimension(100, 27));
			a.getDocument().addDocumentListener(new DocumentListener()
			{
				@Override
				public void insertUpdate(DocumentEvent e)
				{
					update(e.getDocument());
				}
				
				@Override
				public void removeUpdate(DocumentEvent e)
				{
					update(e.getDocument());
				}
				
				@Override
				public void changedUpdate(DocumentEvent e)
				{
					update(e.getDocument());
				}
				
				private void update(Document doc)
				{
					try
					{
						String text = doc.getText(0, doc.getLength());
						if(text.matches("[0-9a-fA-F]+"))
						{
							a.setBackground(Color.white);
						}
						else
						{
							a.setBackground(new Color(250, 200, 200));
						}
						
					}
					catch(BadLocationException e)
					{
						e.printStackTrace();
					}
				}
			});
			b.getDocument().addDocumentListener(new DocumentListener()
			{
				@Override
				public void insertUpdate(DocumentEvent e)
				{
					update(e.getDocument());
				}
				
				@Override
				public void removeUpdate(DocumentEvent e)
				{
					update(e.getDocument());
				}
				
				@Override
				public void changedUpdate(DocumentEvent e)
				{
					update(e.getDocument());
				}
				
				private void update(Document doc)
				{
					try
					{
						String text = doc.getText(0, doc.getLength());
						if(text.trim().matches("[0-9a-fA-F]+"))
						{
							b.setBackground(Color.white);
						}
						else
						{
							b.setBackground(new Color(250, 200, 200));
						}
					}
					catch(BadLocationException e)
					{
						e.printStackTrace();
					}
				}
			});
			add(a);
			
			JButton button = new JButton("Swap");
			button.setToolTipText("Swap both fields.");
			button.setFocusable(false);
			button.addActionListener((ActionEvent e) -> {
				String ta = a.getText().trim();
				String tb = b.getText().trim();
				a.setText(tb);
				b.setText(ta);
			});
			add(button);
			
			b.setPreferredSize(new Dimension(100, 27));
			add(b);
			
			JButton done = new JButton("Done");
			done.setToolTipText("Apply the new addresses.");
			done.setFocusable(false);
			done.addActionListener((ActionEvent e) -> {
				String at = a.getText().trim();
				a.setText(at);
				String bt = b.getText().trim();
				b.setText(bt);
				
				if(at.matches("[0-9a-fA-F]+") && bt.matches("[0-9a-fA-F]+"))
				{
					try
					{
						long addrA = Long.parseLong(at, 16);
						long addrB = Long.parseLong(bt, 16);
						core.updateAddresses(addrA, addrB);
					}
					catch(NumberFormatException numberFormatException)
					{
						JOptionPane.showMessageDialog(null, "Well either of these two could not be parsed: " + numberFormatException.getMessage());
					}
				}
				
				GPSWindow.this.getContentPane().remove(settingsPane);
				GPSWindow.this.getContentPane().add(controls, BorderLayout.NORTH);
				GPSWindow.this.getContentPane().revalidate();
				GPSWindow.this.getContentPane().repaint();
			});
			add(done);
		}
	}
	
	private class Controls extends JComponent
	{
		private final JButton center;
		private JButton activeOne;
		
		public Controls()
		{
			setLayout(new FlowLayout());
			
			JButton settings = new JButton("Addr");
			settings.setToolTipText("Set the position memory addresses here.");
			settings.setFocusable(false);
			settings.addActionListener((ActionEvent e) -> {
				GPSWindow.this.getContentPane().remove(controls);
				GPSWindow.this.getContentPane().add(settingsPane, BorderLayout.NORTH);
				GPSWindow.this.getContentPane().revalidate();
				GPSWindow.this.getContentPane().repaint();
			});
			add(settings);
			
			center = new JButton("Center");
			center.setToolTipText("Align map with player again.");
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
			JButton gray = new JButton("  ");
			gray.setToolTipText("Track with gray color.");
			gray.setBackground(Color.darkGray);
			gray.setForeground(Color.white);
			gray.setFocusable(false);
			gray.addActionListener((ActionEvent e) -> {
				rearm();
				gray.setEnabled(false);
				core.setColor(Color.darkGray);
				gray.setBackground(Color.darkGray.darker());
				activeOne = gray;
			});
			add(gray);
			//Grass grassland
			JButton green = new JButton("  ");
			green.setToolTipText("Track with green color.");
			green.setBackground(Color.green);
			green.setForeground(Color.white);
			green.setFocusable(false);
			green.addActionListener((ActionEvent e) -> {
				rearm();
				green.setEnabled(false);
				core.setColor(Color.green);
				green.setBackground(Color.green.darker());
				activeOne = green;
			});
			add(green);
			//Other blue
			JButton blue = new JButton("  ");
			blue.setToolTipText("Track with blue color.");
			blue.setBackground(Color.blue);
			blue.setForeground(Color.white);
			blue.setFocusable(false);
			blue.addActionListener((ActionEvent e) -> {
				rearm();
				blue.setEnabled(false);
				core.setColor(Color.blue);
				blue.setBackground(Color.blue.darker());
				activeOne = blue;
			});
			add(blue);
			//Other Red
			JButton red = new JButton("  ");
			red.setToolTipText("Track with red color.");
			red.setBackground(Color.red);
			red.setForeground(Color.white);
			red.setFocusable(false);
			red.addActionListener((ActionEvent e) -> {
				rearm();
				red.setEnabled(false);
				core.setColor(Color.red);
				red.setBackground(Color.red.darker());
				activeOne = red;
			});
			add(red);
			
			//OFF
			JButton off = new JButton("Off");
			off.setToolTipText("Don't track movement.");
			off.setFocusable(false);
			//Default:
			activeOne = off;
			off.setEnabled(false);
			off.addActionListener((ActionEvent e) -> {
				rearm();
				off.setEnabled(false);
				core.setColor(null);
				activeOne = off;
			});
			add(off);
			
			//Waypoint
			JButton waypoint = new JButton("+");
			waypoint.setToolTipText("Add waypoint.");
			waypoint.setFocusable(false);
			waypoint.addActionListener((ActionEvent e) -> {
				captureWaypoint = true;
			});
			add(waypoint);
		}
		
		private void rearm()
		{
			activeOne.setEnabled(true);
			if(!activeOne.getText().equals("Off"))
			{
				activeOne.setBackground(activeOne.getBackground().brighter());
				activeOne.repaint();
			}
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
			for(Line line : core.getStorage().getLines())
			{
				g.setColor(line.color);
				g.drawLine(line.x1 - offX, line.z1 - offZ, line.x2 - offX, line.z2 - offZ);
			}
			
			g.setColor(Color.red);
			for(Waypoint waypoint : core.getStorage().getWaypoints())
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
