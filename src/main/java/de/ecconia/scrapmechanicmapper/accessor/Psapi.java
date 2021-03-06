package de.ecconia.scrapmechanicmapper.accessor;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import static com.sun.jna.win32.W32APIOptions.DEFAULT_OPTIONS;
import java.util.Arrays;
import java.util.List;

//https://github.com/warmuuh/AndroidCtx/blob/master/HotContext/src/luz/winapi/jna/Psapi.java
public interface Psapi extends StdCallLibrary
{
	Psapi INSTANCE = (Psapi) Native.loadLibrary("Psapi", Psapi.class, DEFAULT_OPTIONS);
	
	/*
	 * http://msdn.microsoft.com/en-us/library/ms682629(VS.85).aspx
	 */
	boolean EnumProcesses(int[] pProcessIds, int cb, IntByReference pBytesReturned);
	
	/*
	 * http://msdn.microsoft.com/en-us/library/ms682631(VS.85).aspx
	 */
	boolean EnumProcessModules(Pointer hProcess, Pointer[] lphModule, int cb, IntByReference lpcbNeededs);
	
	/*
	 * http://msdn.microsoft.com/en-us/library/ms683198(VS.85).aspx
	 */
	int GetModuleFileNameExA(Pointer hProcess, Pointer hModule, byte[] lpImageFileName, int nSize);
	
	/*
	 * http://msdn.microsoft.com/en-us/library/ms684229(VS.85).aspx
	 */
	static class LPMODULEINFO extends Structure
	{
		public Pointer lpBaseOfDll;
		public int SizeOfImage;
		public Pointer EntryPoint;
		
		@Override
		protected List<String> getFieldOrder()
		{
			return Arrays.asList(
					"lpBaseOfDll",
					"SizeOfImage",
					"EntryPoint"
			);
		}
	}
	
	/*
	 * http://msdn.microsoft.com/en-us/library/ms683201(VS.85).aspx
	 */
	boolean GetModuleInformation(Pointer hProcess, Pointer hModule, LPMODULEINFO lpmodinfo, int cb);
	
	
	/*
	 * http://msdn.microsoft.com/en-us/library/ms683217(VS.85).aspx
	 */
	int GetProcessImageFileNameA(Pointer hProcess, byte[] lpImageFileName, int nSize);
	
	/*
	 * http://msdn.microsoft.com/en-us/library/ms684877(VS.85).aspx
	 */
	static class PPROCESS_MEMORY_COUNTERS extends Structure
	{
		public int cb;
		public int PageFaultCount;
		public int PeakWorkingSetSize;
		public int WorkingSetSize;
		public int QuotaPeakPagedPoolUsage;
		public int QuotaPagedPoolUsage;
		public int QuotaPeakNonPagedPoolUsage;
		public int QuotaNonPagedPoolUsage;
		public int PagefileUsage;
		public int PeakPagefileUsage;
		
		@Override
		protected List<String> getFieldOrder()
		{
			return Arrays.asList("");
		}
	}
	
	/*
	 * http://msdn.microsoft.com/en-us/library/ms683219(VS.85).aspx
	 */
	boolean GetProcessMemoryInfo(Pointer Process, PPROCESS_MEMORY_COUNTERS ppsmemCounters, int cb);
}
