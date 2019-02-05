package jnachos.kern;

import jnachos.machine.Machine;

public class ForkProcess implements VoidFunctionPtr {

	public void call(Object pArg) {
		
		//Restore registers for this user process
		JNachos.getCurrentProcess().restoreUserState();
		
		//Restore address space for this process
		JNachos.getCurrentProcess().getSpace().restoreState();
		
		//jump to the user program
		//machine-Run never returns;
		Machine.run();
		
		//the address space exits
		//by doing the syscall "exit"
		assert(false);
	}
}
