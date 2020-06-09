package de.ecconia.scrapmechanicmapper.accessor;

import com.sun.jna.Pointer;
import java.util.ArrayList;
import java.util.List;

public class PositionScanner
{
	public static void main(String[] args)
	{
		ProcessWrapper wrapper = new ProcessWrapper("Scrap Mechanic");
		
		String daName = wrapper.getProcessImageFileName();
		System.out.println(daName);
		
		Psapi.LPMODULEINFO info = wrapper.getProcessModuleInfo("ScrapMechanic.exe");
		long offset = pointerToLong(info.lpBaseOfDll);
		long entry = pointerToLong(info.EntryPoint);
		System.out.println("Offset: " + info.lpBaseOfDll + " " + offset);
		System.out.println("Length: " + "0x" + Long.toHexString(info.SizeOfImage) + " " + info.SizeOfImage);
		System.out.println("SomeOf: " + info.EntryPoint + " " + entry);
		
		System.out.println("L1: " + Long.toHexString(offset + info.SizeOfImage));
		System.out.println("L2: " + Long.toHexString(entry + info.SizeOfImage));
		
		long value = Long.parseLong("696DE700", 16);
		System.out.println("Raw Address: " + value);
		System.out.println("Address: " + (value - offset));
		
		System.out.println(wrapper.readFloat(0x696DE700L));
		
		List<Long> monitorThings = new ArrayList<>();
		
		for(int i = 0; i < info.SizeOfImage; i += 4)
		{
			long currentAddress = offset + i;
			int daVal = wrapper.readInt(currentAddress);
//			System.out.println("Value: " + daVal);
			if(daVal <= 90 && daVal >= 10)
			{
				monitorThings.add(currentAddress);
				System.out.println("Found one: " + daVal + " @ " + Long.toHexString(currentAddress) + " -> " + (i));
			}
		}
		
		while(true)
		{
			for(Long addr : monitorThings)
			{
				System.out.print(wrapper.readInt(addr) + " ");
			}
			System.out.println();
		}

//		System.out.println("Done.");
	}
	
	private static long pointerToLong(Pointer pointer)
	{
		String str = pointer.toString();
		if(str.startsWith("native@0x"))
		{
			return Long.parseLong(str.substring("native@0x".length(), str.length()), 16);
		}
		else
		{
			throw new RuntimeException("Pointer doesn't start with 'native@0x'");
		}
	}
}
