package de.ecconia.scrapmechanicmapper;

import de.ecconia.scrapmechanicmapper.accessor.ProcessWrapper;

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
		System.out.println("Stopped updated thread.");
	}
	
	public interface PositionReceiver
	{
		void updatePosition(float a, float b);
	}
}
