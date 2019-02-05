package jnachos.machine;
import jnachos.kern.NachosProcess;

public class entry {

	public entry() {};
	private NachosProcess Process;
	private int VirtualPageNumber;
	//static private int ProcessId;
	
	public void setProcess(NachosProcess process) {
		Process = process;
	}
	
	public NachosProcess getProcess() {
		return Process;
	}
	
	public void setVirtualPageNumbaer(int Num) {
		VirtualPageNumber = Num;
	}
	
	public int getVirtualPageNum() {
		return VirtualPageNumber;
	}
	
//	public static void setProcessId(int id) {
//		ProcessId = id;
//	}
	
//	static public int getProcessId() {
//		return ProcessId;
//	}
	
}
