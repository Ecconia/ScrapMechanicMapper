package de.ecconia.scrapmechanicmapper.accessor;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

import static com.sun.jna.win32.W32APIOptions.DEFAULT_OPTIONS;

public interface Kernel32 extends StdCallLibrary
{
	Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("kernel32", User32.class, DEFAULT_OPTIONS);
	
	// description from msdn
	//BOOL WINAPI WriteProcessMemory(
	//          __in   HANDLE hProcess,
	//          __in   LPVOID lpBaseAddress,
	//          __in   LPCVOID lpBuffer,
	//          __in   SIZE_T nSize,
	//          __out  SIZE_T *lpNumberOfBytesWritten
	//);
	boolean WriteProcessMemory(Pointer p, long address, Pointer buffer, int size, IntByReference written);
	
	//BOOL WINAPI ReadProcessMemory(
	//          __in   HANDLE hProcess,
	//          __in   LPCVOID lpBaseAddress,
	//          __out  LPVOID lpBuffer,
	//          __in   SIZE_T nSize,
	//          __out  SIZE_T *lpNumberOfBytesRead
	//);
	boolean ReadProcessMemory(Pointer hProcess, long inBaseAddress, Pointer outputBuffer, int nSize, IntByReference outNumberOfBytesRead);
	
	//HANDLE WINAPI OpenProcess(
	//          __in  DWORD dwDesiredAccess,
	//          __in  BOOL bInheritHandle,
	//          __in  DWORD dwProcessId
	//);
	Pointer OpenProcess(int desired, boolean inherit, int pid);
	
	/* derp */
	int GetLastError();
}