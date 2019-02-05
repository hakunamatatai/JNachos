/**
 * Copyright (c) 1992-1993 The Regents of the University of California.
 * All rights reserved.  See copyright.h for copyright notice and limitation 
 * of liability and disclaimer of warranty provisions.
 *  
 *  Created by Patrick McSweeney on 12/5/08.
 */
package jnachos.kern;

import jnachos.machine.*;
import java.util.ArrayList;
import java.util.List;
import jnachos.filesystem.OpenFile;

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
		
		//int PCcount = 0;

		System.out.println("SysCall:" + pWhichSysCall);

		switch (pWhichSysCall) {
		// If halt is received shut down
		case SC_Halt:
			Debug.print('a', "Shutdown, initiated by user program.");
			Interrupt.halt();
			break;

		case SC_Exit:
			// Read in any arguments from the 4th register
			boolean preExit = Interrupt.setLevel(false);
			int arg = Machine.readRegister(4);
			System.out.println("Current process" + JNachos.getCurrentProcess().getName() + " is calling Exit." );
			
			//System.out.println("currentprocess.getID: " + JNachos.getCurrentProcess().getID());
			System.out.println("Current Process " + JNachos.getCurrentProcess().getName() + " exiting with code " + arg);
			//System.out
			//.println("Current Process " + JNachos.getCurrentProcess().getID() + " exiting with code " + arg);

			/*
			 * Every time a process calls the Exit System call, 
			 * check if any other process is waiting for it to finish.
			 */
			//if there is a process waiting
			//IDList
			//if (ProcessId.IDList.containsKey(JNachos.getCurrentProcess().getID())) {
			
			//WaitingList
			if (JNachos.getBlockTable().containsKey(JNachos.getCurrentProcess().getPID())) {
				
				//1
				///*
				int waitingID = (Integer)JNachos.getBlockTable().get(JNachos.getCurrentProcess().getPID());
				System.out.println("Process " + waitingID + " is waiting for " + JNachos.getCurrentProcess().getPID() + " to finish");
				
				//PCcount + 4 to enter the next instruction
				int PCcount = Machine.readRegister(Machine.NextPCReg);
				Machine.writeRegister(Machine.PCReg, PCcount);
				
				//ProcessId.IDList.get(waitingID).saveUserRegister(2, arg);
		        //Machine.writeRegister(2, arg);
				//ProcessId.IDList.get(waitingID).saveUserState();
				JNachos.getProcessTable().get(waitingID).saveUserRegister(2, arg);
				//Scheduler.readyToRun((NachosProcess) ProcessId.IDList.get(waitingID));
				//Scheduler.readyToRun(ProcessId.IDList.get(waitingID));
				Scheduler.readyToRun(JNachos.getProcessTable().get(waitingID));
				
				//ProcessId.Waitinglist.remove(JNachos.getCurrentProcess().getID());
				//ProcessId.IDList.remove(JNachos.getCurrentProcess().getID());
				JNachos.getBlockTable().remove(JNachos.getCurrentProcess().getPID());
				
				//*/
				
				//2
				//ProcessId.Waitinglist.remove(JNachos.getCurrentProcess().getID());
				//ProcessId.IDList.remove(JNachos.getCurrentProcess().getID());
				//set exit code of this process in register R2 of waiting process
				//Machine.writeRegister(2, arg);
				//Call readyToRun on the waiting process so that it will run again
				//Scheduler.readyToRun((NachosProcess)ProcessId.IDList.get(JNachos.getCurrentProcess().getID()));
				
							
				//finish the process
				JNachos.getCurrentProcess().finish();
			}
			//if not
			else {
				//PCcount + 4 to enter the next instruction
				int PCcount = Machine.readRegister(Machine.NextPCReg);
				Machine.writeRegister(Machine.PCReg, PCcount);
				
				//finish the process
				JNachos.getCurrentProcess().finish();
			}
			//JNachos.getCurrentProcess().finish();
			//ProcessId.IDList.remove(JNachos.getCurrentProcess().getID());
			JNachos.getProcessTable().remove(JNachos.getCurrentProcess().getPID());
			Interrupt.setLevel(preExit);
			break;
			
			

		//create and exact copy of the currently running process.
		case SC_Fork:
		//Disable Interrupts
			//Interrupt.setLevel(false);
			boolean pre = Interrupt.setLevel(false);
						
			//writeRegister no.2, value 0
			Machine.writeRegister(2, 0);
						
			//Create a new JNachoesProcess
			System.out.println("\n\nCurrent process" + JNachos.getCurrentProcess().getName() + " is calling fork." );
			System.out.println("Current Process PID: "+JNachos.getCurrentProcess().getPID());
			NachosProcess child = new NachosProcess(JNachos.getCurrentProcess().getName() + "  's child process's ");
						
			//copy the memory space of the parent to a new section of RAM for the child,
			//in the process setting up the child's memory map.
			//AddrSpace childAddrSpace = new AddrSpace(JNachos.getCurrentProcess().getSpace());
			child.setSpace(new AddrSpace(JNachos.getCurrentProcess().getSpace()));
						
			//PCcount + 4 to enter the next instruction before saving user state - mips
			int PCcount = Machine.readRegister(Machine.NextPCReg);
			Machine.writeRegister(Machine.PCReg, PCcount);
						
			//Copy the parents registers into the childs set of registers with the notable exception 
			//that the return value of the Fork system call should be different
			child.saveUserState();
			Machine.writeRegister(2, child.getPID());
						
			//Call the NachosProcess::fork member function make the child Ready.
			child.fork(new ForkProcess(), child);
			//System.out.println("Register number is: " + Machine.readRegister(4));
						
			//Restore Interrupt
			//Interrupt.setLevel(true);
			Interrupt.setLevel(pre);
			break;
			
						
						
		//Join system call will should make the invoking process wait until the process
		//with the specified PID has completed.
		case SC_Join:
			//Disable Interrupts
			//Interrupt.setLevel(false);
			boolean preJ = Interrupt.setLevel(false);
			System.out.println("\n\nCurrent process" + JNachos.getCurrentProcess().getName() + " is calling Join." );
							
			//create a specified ID for the current child process
			//output specified id
			//int currentSpecifiedID = Machine.readRegister(4);
			//System.out.println("The current process ID is: " + currentSpecifiedID);
							
			/*Judge the current condition: 
			 * if the process passes in an invalid PID, join should return
			 * if there is currently no PID in process, no child process, join should return
			 * if the process passes in its own PID, no child process, join should return
			*/
			PCcount = Machine.readRegister(Machine.NextPCReg);
			Machine.writeRegister(Machine.PCReg, PCcount);
			if(! JNachos.getProcessTable().containsKey(Machine.readRegister(4)) || Machine.readRegister(4) == 0 || 
			//if(ProcessId.IDList.containsKey(Machine.readRegister(4))!=true || Machine.readRegister(4) == 0 || 
					Machine.readRegister(4) == JNachos.getCurrentProcess().getPID()) {		
				
				//PCcount + 4 to enter the next instruction
				//PCcount = Machine.readRegister(Machine.NextPCReg);
				//Machine.writeRegister(Machine.PCReg, PCcount);
				System.out.println("Break!");
				break;
				}
				else {	
				//call join, set the invoking process to sleep 
				System.out.print("Current process " + JNachos.getCurrentProcess().getPID() + " will sleep for current process " 
						+ Machine.readRegister(4) + " to finish, and then continue \n");
				
				System.out.println("current id: " + JNachos.getCurrentProcess().getPID());
				//Store that current process will be waiting on Process with PID in R4
				//save a pointer to the invoking process some whrer that can be accessed later
				NachosProcess currentprocess = JNachos.getCurrentProcess();
				//IDList
				//ProcessId.IDList.put(Machine.readRegister(4), currentprocess);	
				
				//WaitingList
				JNachos.getBlockTable().put(Machine.readRegister(4), JNachos.getCurrentProcess().getPID());
				//ProcessId.Waitinglist.put(Machine.readRegister(4),currentprocess.getID());
				//ProcessId.Waitinglist.put(currentSpecifiedID, currentprocess);			
					
				JNachos.getCurrentProcess().sleep();
				//invokingprocess.sleep();
				//Restore Interrupt
				}
			//Interrupt.setLevel(true);
			Interrupt.setLevel(preJ);
			break;
				
			
		case SC_Exec:
			//Disable Interrupts
			//Interrupt.setLevel(false);
			boolean preE = Interrupt.setLevel(false);
			System.out.println("\n\nCurrent process" + JNachos.getCurrentProcess().getName() + " is calling Exec.\n" );
			System.out.println("The Process " + JNachos.getCurrentProcess().getPID() + " is running");
			
			//PC count + 4
			PCcount = Machine.readRegister(Machine.NextPCReg);
			Machine.writeRegister(Machine.PCReg, PCcount);
			
			//Get the string parameter from the process address space: only a pointer passed in
			int processAddr = Machine.readRegister(4);
			int getPointer = 1;
			List<Integer> FileName = new ArrayList<>();
			String execFileName = new String();
			
			while( (char)getPointer != '\0') {
				getPointer = Machine.readMem(processAddr, 1);
				if ((char) getPointer != '\0') {
					execFileName += (char)getPointer;
					processAddr++;
					FileName.add(getPointer);
				}
			}
			
			
			//Open the file and overwrite the contents of this process address space with its content
			OpenFile exec = JNachos.mFileSystem.open(execFileName);
			//file does not exist
			if(exec == null) {
				System.out.println("The" + execFileName + "does not exist");
				break;
			}
			
			System.out.println("Run ---> " + execFileName);
			JNachos.startProcess(execFileName);
			
			
			//create new memory space for the execfile.
			//AddrSpace newspace = new AddrSpace(exec);
			//JNachos.getCurrentProcess().setSpace(newspace);
			
			//Reset the process registers to their initial default state
			//newspace.initRegisters();
			//newspace.restoreState();
			
			//jump to the user program
			//machine-Run never returns;
			//Machine.run();
			
			//Restore Interrupt
			//Interrupt.setLevel(true);
			Interrupt.setLevel(preE);
			break;
			
		default:
			Interrupt.halt();
			break;
		}
	}
}
