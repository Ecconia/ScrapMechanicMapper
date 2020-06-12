package de.ecconia.scrapmechanicmapper;

import de.ecconia.scrapmechanicmapper.accessor.ProcessWrapper;
import javax.swing.JOptionPane;

public class AddressToPosition extends Thread
{
	private final long addressA;
	private final long addressB;
	private final PositionReceiver receiver;
	
	private final ProcessWrapper process;
	
	public AddressToPosition(long addressA, long addressB, PositionReceiver receiver)
	{
		this.addressA = addressA;
		this.addressB = addressB;
		this.receiver = receiver;
		
		process = new ProcessWrapper("Scrap Mechanic");
		
		setDaemon(true);
	}
	
	@Override
	public void run()
	{
		try
		{
			System.out.println("Started update thread.");
			while(!isInterrupted())
			{
				float a = process.readFloat(addressA);
				float b = process.readFloat(addressB);
				receiver.updatePosition(a, b);
				
				try
				{
					Thread.sleep(500);
				}
				catch(InterruptedException e)
				{
					break;
				}
			}
		}
		catch(Error e)
		{
			if(e.getMessage().equals("Invalid memory access"))
			{
				System.out.println("Cannot access it.");
				JOptionPane.showMessageDialog(null, "Cannot read from that address. Start with console and report to developer.");
			}
			else
			{
				throw e;
			}
		}
		
		System.out.println("Stopped updated thread.");
	}
	
	public interface PositionReceiver
	{
		void updatePosition(float a, float b);
	}
}
