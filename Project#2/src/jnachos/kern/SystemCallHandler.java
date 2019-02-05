/**
 * Copyright (c) 1992-1993 The Regents of the University of California.
 * All rights reserved.  See copyright.h for copyright notice and limitation 
 * of liability and disclaimer of warranty provisions.
 *  
 *  Created by Patrick McSweeney on 12/5/08.
 */

/*
 * Modified by Yuchang Chen on 20/9/2017
 * Add the Exit, Fork, Join, Exec system calls in the handler.
 */
package jnachos.kern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jnachos.filesystem.OpenFile;
import jnachos.machine.*;

/** The class handles System calls made from user programs. */
public class SystemCallHandler {
	
	/** The System call index for halting. */
	public static final int SC_Halt = 0;

	/** The System call index for exiting a program. */
	public static final int SC_Exit = 1;

	/** The System call index for executing program. */
	public static final int SC_Exec = 2;

	/** The System call index for joining with a process. */
	public static final int SC_Join = 3;

	/** The System call index for creating a file. */
	public static final int SC_Create = 4;

	/** The System call index for opening a file. */
	public static final int SC_Open = 5;

	/** The System call index for reading a file. */
	public static final int SC_Read = 6;

	/** The System call index for writting a file. */
	public static final int SC_Write = 7;

	/** The System call index for closing a file. */
	public static final int SC_Close = 8;

	/** The System call index for forking a forking a new process. */
	public static final int SC_Fork = 9;

	/** The System call index for yielding a program. */
	public static final int SC_Yield = 10;

	/**
	 * Entry point into the Nachos kernel. Called when a user program is
	 * executing, and either does a syscall, or generates an addressing or
	 * arithmetic exception.
	 * 
	 * For system calls, the following is the calling convention:
	 * 
	 * system call code -- r2 arg1 -- r4 arg2 -- r5 arg3 -- r6 arg4 -- r7
	 * 
	 * The result of the system call, if any, must be put back into r2.
	 * 
	 * And don't forget to increment the pc before returning. (Or else you'll
	 * loop making the same system call forever!
	 * 
	 * @pWhich is the kind of exception. The list of possible exceptions are in
	 *         Machine.java
	 **/
	public static void handleSystemCall(int pWhichSysCall) {
		

		Debug.print('a', "!!!!" + Machine.read1 + "," + Machine.read2 + "," + Machine.read4 + "," + Machine.write1 + ","
				+ Machine.write2 + "," + Machine.write4);
		
		//System.out.println("----------------------------------------------------------------------------------");
		System.out.println("\n");
		System.out.println("SysCall:" + pWhichSysCall);

		switch (pWhichSysCall) {
		// If halt is received shut down
		case SC_Halt:
			Debug.print('a', "Shutdown, initiated by user program.");
			Interrupt.halt();
			break;

		case SC_Exit:
			Interrupt.setLevel(false);	
			// Read in any arguments from the 4th register
			
			System.out.println("Current process:"+JNachos.getCurrentProcess().getName()+" called SC_Exit.");
			int arg = Machine.readRegister(4);

			System.out
					.println("Current Process " + JNachos.getCurrentProcess().getName() + " exiting with code " + arg);
			
			//Take the waiting process from the IdMap
			Object waitingProcess=ProcessId.IdMap.get(JNachos.getCurrentProcess().getId());
			
			//If there is a process waiting	===>Join(4)
			if(waitingProcess != null) {
				
				//PC=PC+4
				Machine.writeRegister(Machine.PCReg,Machine.readRegister(Machine.NextPCReg));
				
				//save the exiting code in the user register as the return value.	===>Join(5)
				JNachos.getCurrentProcess().saveReg(2, arg);
				
				//set the waiting process ready to run		===>Join(6)
				Scheduler.readyToRun((NachosProcess)ProcessId.IdMap.get(JNachos.getCurrentProcess().getId()));
				System.out.println("The return from the Exit: " +JNachos.getCurrentProcess().readReg(2));
				
				// Finish the invoking process
				ProcessId.IdMap.remove(JNachos.getCurrentProcess());
				JNachos.getCurrentProcess().finish();
			}
			// If there is no waiting process
			else {
				
				//PC=PC+4
				Machine.writeRegister(Machine.PCReg,Machine.readRegister(Machine.NextPCReg));
				
				// Finish the invoking process
				ProcessId.IdMap.remove(JNachos.getCurrentProcess());				
				JNachos.getCurrentProcess().finish();
				
			}
			
			Interrupt.setLevel(true);
			break;

		case SC_Fork:
			//close the interrupt
			Interrupt.setLevel(false);	
			
			System.out.println("Current process:"+JNachos.getCurrentProcess().getName()+" called SC_Fork.");
	
			//return 0 to the parent
			Machine.writeRegister(2, 0);
			
			//create a new process ===> Fork(1)
			NachosProcess child=new NachosProcess("The child of "+JNachos.getCurrentProcess().getName());
			
			//Copy the memory space of the parent to a new section of RAM for the child, 
			//in the process setting up the child's memory map.		===>Fork(2)
			AddrSpace ChildAddrSpace=new AddrSpace(JNachos.getCurrentProcess().getSpace());
			child.setSpace(ChildAddrSpace);
			
			//pc=pc+4
			int pc_counter=Machine.readRegister(Machine.NextPCReg);
			Machine.writeRegister(Machine.PCReg,pc_counter);
			
			//save the state of the child process		===>Fork(3)
			child.saveUserState();
			
			//return the process id of the child process
			Machine.writeRegister(2, child.getId());
			
			//call the fork function	===>Fork(4)
			child.fork(new ForkProcess(), child);
			System.out.println("The Reg is:"+Machine.readRegister(4));
			
			//enable the interrupt
			Interrupt.setLevel(true);
			
			break;
			
		case SC_Join:
			//close the interrupt
			Interrupt.setLevel(false);
			System.out.println("Current process:"+JNachos.getCurrentProcess().getName()+" called SC_Join.");
			
			//get the first complete process(child process)
			int SpecificId=Machine.readRegister(4);
			System.out.println("The SpecificId is:"+ SpecificId);		
			
			//ensure the specific process exists		===>Join(1)
			if(SpecificId==0 || ProcessId.IdMap.containsKey(SpecificId)!=true || SpecificId==JNachos.getCurrentProcess().getId()) {
				
				//pc=pc+4
				pc_counter=Machine.readRegister(Machine.NextPCReg);
				Machine.writeRegister(Machine.PCReg,pc_counter);
				break;
			}
			else
			{
				//save the pointer of the invoking process		===>Join(2)
				ProcessId.IdMap.put(SpecificId,JNachos.getCurrentProcess());
			
				int JoinReturn=JNachos.getCurrentProcess().getId();
				Machine.writeRegister(2, JoinReturn);
				
				//pc=pc+4
				pc_counter=Machine.readRegister(Machine.NextPCReg);
				Machine.writeRegister(Machine.PCReg,pc_counter);
				
				//put the invoking process to sleep		===>Join(3)
				JNachos.getCurrentProcess().sleep();
			
			}
			
			Interrupt.setLevel(true);			
			break;
			
		case SC_Exec:
			Interrupt.setLevel(false);
			
			System.out.println("Current process:"+JNachos.getCurrentProcess().getName()+" called SC_Exec.");
			
			//pc=pc+4
			pc_counter=Machine.readRegister(Machine.NextPCReg);
			Machine.writeRegister(Machine.PCReg,pc_counter);
			
			//get the address from the register
			int FileAddr=Machine.readRegister(4);
			
			//get the filename		===>Exec(1)
			int getMem=1;
//			List<Integer> FileList=new ArrayList<>();
			String execFile=new String();
			while ( (char)getMem!='\0' ) {
				getMem=Machine.readMem(FileAddr, 1);
				
				if((char)getMem!='\0')
					execFile=execFile+(char)getMem;
				
				FileAddr++;
//				FileList.add(getMem);
			}
			
			//Open the file		===>Exec(2)
			OpenFile executable = JNachos.mFileSystem.open(execFile);

			// If the file does not exist
			if (executable == null) {
				System.out.println("Unable to open file: " + execFile);	
				break;
			}

			// Load the file into the memory space
			AddrSpace space = new AddrSpace(executable);
			JNachos.getCurrentProcess().setSpace(space);

			// set the initial register values		===>Exec(3)
			space.initRegisters();

			// load page table register
			space.restoreState();

			System.out.println("Run the user program: "+execFile);
			
			// jump to the user progam
			// machine->Run never returns;
			Machine.run();
						
			Interrupt.setLevel(true);
			
			break;
		default:
			Interrupt.halt();
			break;
		}
	}
}
