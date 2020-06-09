package de.ecconia.scrapmechanicmapper.accessor;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

//Reference: https://stackoverflow.com/questions/18849609/how-to-manipulate-memory-from-java-with-jna-on-windows
public class ProcessWrapper
{
	public static int PROCESS_VM_READ = 0x0010;
	public static int PROCESS_VM_WRITE = 0x0020;
	public static int PROCESS_VM_OPERATION = 0x0008;
	public static int PROCESS_QUERY_INFORMATION = 0x0400;
	
	private final Pointer process;
	
	public ProcessWrapper(String name)
	{
		int pid = getProcessId(name);
		if(pid == 0)
		{
			System.err.println("Could not find process.");
			System.exit(1);
		}
		
		process = openProcess(PROCESS_VM_READ | PROCESS_VM_WRITE | PROCESS_VM_OPERATION | PROCESS_QUERY_INFORMATION, pid);
	}
	
	public String getProcessImageFileName()
	{
		byte[] buffer = new byte[1024];
		int read = Psapi.INSTANCE.GetProcessImageFileNameA(process, buffer, buffer.length);
		return new String(buffer, 0, read);
	}
	
	public Psapi.LPMODULEINFO getProcessModuleInfo(String executable)
	{
		Pointer[] pointers = new Pointer[1024];
		IntByReference ref = new IntByReference(0);
		boolean test = Psapi.INSTANCE.EnumProcessModules(process, pointers, 1024, ref);
		if(!test)
		{
			throw new RuntimeException("API says no.");
		}
		
		for(int i = 0; i < (ref.getValue() / 8); i++)
		{
			byte[] temp = new byte[1024];
			int ret = Psapi.INSTANCE.GetModuleFileNameExA(process, pointers[i], temp, 1024);
			if(ret == 0)
			{
				throw new RuntimeException("API says no.");
			}
			String path = new String(temp, 0, ret);
			if(path.endsWith(executable)) //TODO: Replace with variable.
			{
				Pointer pointer = pointers[i];
				
				Psapi.LPMODULEINFO info = new Psapi.LPMODULEINFO();
				test = Psapi.INSTANCE.GetModuleInformation(process, pointer, info, info.size());
				if(!test)
				{
					throw new RuntimeException("API says no.");
				}
				
				return info;
			}
		}
		
		throw new RuntimeException("Could not find " + executable + "'s base address");
	}
	
	public static int getProcessId(String window)
	{
		IntByReference pid = new IntByReference(0);
		User32.INSTANCE.GetWindowThreadProcessId(User32.INSTANCE.FindWindowA(null, window), pid);
		
		return pid.getValue();
	}
	
	public static Pointer openProcess(int permissions, int pid)
	{
		return Kernel32.INSTANCE.OpenProcess(permissions, true, pid);
	}
	
	public Memory readMemory(long address, int bytesToRead)
	{
		IntByReference read = new IntByReference(0);
		Memory output = new Memory(bytesToRead);
		
		Kernel32.INSTANCE.ReadProcessMemory(process, address, output, bytesToRead, read);
		return output;
	}
	
	public float readFloat(long address)
	{
		Memory scoreMem = readMemory(address, 4);
		return scoreMem.getFloat(0);
	}
	
	public int readInt(long address)
	{
		Memory scoreMem = readMemory(address, 4);
		return scoreMem.getInt(0);
	}
}
