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
import java.util.LinkedList;
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
	
    public static final int SC_SendMessage = 13;
	
	public static final int SC_SendAnswer = 14;
	
	public static final int SC_WaitMessage = 15;
	
	public static final int SC_WaitAnswer = 16;

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
				JNachos.getProcessTable().remove(JNachos.getCurrentProcess().getPID());
				//finish the process
				//Interrupt.halt();
				JNachos.getCurrentProcess().finish();				
				
			}
			//JNachos.getCurrentProcess().finish();
			//ProcessId.IDList.remove(JNachos.getCurrentProcess().getID());
			//JNachos.getProcessTable().remove(JNachos.getCurrentProcess().getPID());
			
			
			//check if the exiting process's queue has buffer left
			LinkedList<Buffer> exitBufferQ = (LinkedList<Buffer>) JNachos.getCurrentProcess().ProcessQueue;
			//NachosProcess WaitProcess = JNachos.getCurrentProcess();
			//wait the process until an answer arrives in its queue
			
			System.out.println("queue size: " + exitBufferQ.size());
			while( !exitBufferQ.isEmpty() ) {
				//get the buffer
				Buffer LeftBuffer = exitBufferQ.poll();
				
				//if the left buffer contains a message, send a dummy answer to the original sender
				if(LeftBuffer.getMessageType() == MessageType.MESSAGE) {
					LeftBuffer.SetReceiver(LeftBuffer.GetSender());
					LeftBuffer.SetSender("System Kernel");
					LeftBuffer.SetMessage("Dummy");
					LeftBuffer.setMessageType(MessageType.ANSWER);
					
					NachosProcess ReceiverProExit = JNachos.ProcessNameTable.get(LeftBuffer.GetReceiver());
					if(ReceiverProExit != null) {
						//add buffer to queue
						ReceiverProExit.ProcessQueue.add(LeftBuffer);
					} else {
						//return buffer to buffer pool
						JNachos.mBufferPool.returnBufToPool(LeftBuffer);
					}
				} else {
					//if the buffer contains an answer, return the buffer to buffer pool
					JNachos.mBufferPool.returnBufToPool(LeftBuffer);
				}
		    }
		    
			
			Interrupt.setLevel(preExit);
			break;
			
			
		case SC_SendMessage:
			//Disable Interrupts
			Interrupt.setLevel(false); 
            System.out.println("Current process" + JNachos.getCurrentProcess().getName() + " is calling SendMeassage.\n" );
            
          //PCcount + 4 to enter the next instruction
			int PCcount = Machine.readRegister(Machine.NextPCReg);
			Machine.writeRegister(Machine.PCReg, PCcount);
            
			
			//Get the first parameter:receiver's name from the process address space: a pointer passed in
			int receiverAddr = Machine.readRegister(4);
			int ReceiverSendMPointer = 1;
			String receiverProName = new String();
			while( (char)ReceiverSendMPointer != '\0') {
				ReceiverSendMPointer = Machine.readMem(receiverAddr, 1);
				if ((char) ReceiverSendMPointer != '\0') {
					receiverProName += (char)ReceiverSendMPointer;
					receiverAddr++;
				}
			//System.out.println("Receiver name of SendMessage is : " + receiverProName);
			}
			System.out.println("Receiver name of SendMessage is : " + receiverProName);
			
			
			//Get the second parameter:message from the process address space: a pointer passed in
			
			
			int MessageSendAddr = Machine.readRegister(5);
			ReceiverSendMPointer = 1;
			String MessageSendName = new String();
			while( (char)ReceiverSendMPointer != '\0') {
				ReceiverSendMPointer = Machine.readMem(MessageSendAddr, 1);
				if ((char) ReceiverSendMPointer != '\0') {
					MessageSendName += (char)ReceiverSendMPointer;
					MessageSendAddr++;
				}
			} 
			System.out.println("Message of SendMessage is : " + MessageSendName);
			
			//get a buffer from pool
			Buffer SendBuffer = JNachos.mBufferPool.getFreeBuf();
			//save sender's name in buffer
			SendBuffer.SetSender(JNachos.getCurrentProcess().getName());
			//save receiver's name
			SendBuffer.SetReceiver(receiverProName);
			//save message in buffer
			SendBuffer.SetMessage(MessageSendName);
			//set message type
			//SendBuffer
			
			
			System.out.println("The sender of the SendMessage is: "+SendBuffer.GetSender());
			//receive Process
			//
			
			NachosProcess receiveProcess = JNachos.ProcessNameTable.get(receiverProName);
			
			//System.out.println("process table: " + JNachos.ProcessNameTable.get(receiverProName));
			//System.out.println("process table: " + JNachos.ProcessNameTable.size());
			
			if(receiveProcess != null) {
				receiveProcess.ProcessQueue.add(SendBuffer);
				JNachos.ProcessNameTable.put(receiverProName, receiveProcess);
				
				System.out.println("The message "+ MessageSendName + " in the Buffer "+ SendBuffer.GetBufferId()
					+" has been sent from "+SendBuffer.GetSender() + " to "+receiverProName);
			}else {
				System.out.println("The receiver " + receiverProName + " does not exist. It returns dummy answer to process: "
						+ SendBuffer.GetReceiver());
				
				//if the receive process doesn't exist, system return a dummy answer to original sender
				NachosProcess ReceiverProSenM = JNachos.ProcessNameTable.get(SendBuffer.GetSender());
				SendBuffer.setMessageType(MessageType.ANSWER);
				SendBuffer.SetReceiver(SendBuffer.GetSender());
				SendBuffer.SetMessage("Dummy");
				SendBuffer.SetSender("System Kernel");
				
				ReceiverProSenM.ProcessQueue.add(SendBuffer);		
			}
			
			//return value is the buffer's id
			Machine.writeRegister(2, SendBuffer.GetBufferId());
			Interrupt.setLevel(true);
			break;
			
			
		case SC_WaitMessage:
			Interrupt.setLevel(false);
            System.out.println("Process" + JNachos.getCurrentProcess().getName() + " is calling WaitMeassage.\n" );
			
          //PCcount + 4 to enter the next instruction
			PCcount = Machine.readRegister(Machine.NextPCReg);
			Machine.writeRegister(Machine.PCReg, PCcount);
			
			//Get the first parameter: sender's name from the process address space: get a pointer of receiver's name passed in
			int SendWaitAddr = Machine.readRegister(4);
			int SendWaitPointer = 1;
			String SendWaitProName = new String();
			while( (char)SendWaitPointer != '\0') {
				SendWaitPointer = Machine.readMem(SendWaitAddr, 1);
				if ((char) SendWaitPointer != '\0') {
					SendWaitProName += (char)SendWaitPointer;
					SendWaitAddr++;
				}
			//System.out.println("The WaitMessage is waiting for : " + SendWaitProName + "'s message");
			}
			System.out.println("The WaitMessage is waiting for : " + SendWaitProName + "'s message");
			
			//for buffer
			boolean valid = false;
			while(!valid) {
				//NachosProcess WaitProcess = JNachos.getCurrentProcess();
				String receiveName = JNachos.getCurrentProcess().getName();
				NachosProcess WaitProcess = (NachosProcess)JNachos.ProcessNameTable.get(receiveName);
	
					while( WaitProcess.ProcessQueue.isEmpty() ) {
					WaitProcess.yield();	
			    }
				
			//Buffer bufferWaitM = WaitProcess.getmBufferQ().remove();
			Buffer bufferWaitM = WaitProcess.ProcessQueue.remove();
			
			//check if the sender name in buffer is the same as the sender name
			if(SendWaitProName.equals(bufferWaitM.GetSender())) {
				System.out.println("The buffer of WaitMessage is validated");
				
				int MessageAddr = Machine.readRegister(5);
				String MessageWaitM = bufferWaitM.GetMessage();
				System.out.println("Received message using buffer "+ bufferWaitM.GetBufferId() + "from" + bufferWaitM.GetSender());

				//copy the message
				for(int i = 0; i < MessageWaitM.length(); i++) {
					Machine.writeMem(MessageAddr, 1, MessageWaitM.charAt(i));
					MessageAddr++;
				}
				//add \0 to the end of a string
				Machine.writeMem(MessageAddr, 1, '\0');
				//return value is the buffer's id
				Machine.writeRegister(2, bufferWaitM.GetBufferId());
				
				//jump 
				valid = true;
			} else {
				//if not same 
				System.out.println("The WaitMessage " + SendWaitProName + " receives an invalid buffer from: "
						+ bufferWaitM.GetSender());
				
				//system return a dummy answer to original sender
				bufferWaitM.setMessageType(MessageType.ANSWER);
				bufferWaitM.SetReceiver(bufferWaitM.GetSender());
				bufferWaitM.SetMessage("Invalid Receiver");
				bufferWaitM.SetSender("System Kernel");
				
				//get the receive process from process name table
				NachosProcess ReceiverProWaitM = JNachos.ProcessNameTable.get(bufferWaitM.GetReceiver());
				if (ReceiverProWaitM != null) {
					ReceiverProWaitM.ProcessQueue.add(bufferWaitM);
					//if receiver process exists, deliver the buffer to its queue
				} else {
					//if receiver process does not exist, return buffer to buffer pool
					JNachos.mBufferPool.returnBufToPool(bufferWaitM);
				}
                 //
			}	
		}
			Interrupt.setLevel(true);
			break;
		
			
		case SC_SendAnswer:
			Interrupt.setLevel(false);
			System.out.println("Process" + JNachos.getCurrentProcess().getName() + "is calling SendAnswer.\n" );
			
			
			//PCcount + 4 to enter the next instruction
			PCcount = Machine.readRegister(Machine.NextPCReg);
			Machine.writeRegister(Machine.PCReg, PCcount);
			
			//read the buffer id from the second input parameter and get the buffer from the buffer pool
		    //Buffer bufferSendAns = JNachos.mBufferPool.getBufById(Machine.readRegister(5));
			
			//Get the first parameter:answer from the process address space: get a pointer of answer passed in
			int SendAnsAddr = Machine.readRegister(4);
			int SendAnsPointer = 1;
			String answer = new String();
			while( (char)SendAnsPointer != '\0') {
				SendAnsPointer = Machine.readMem(SendAnsAddr, 1);
				if ((char) SendAnsPointer != '\0') {
					answer += (char)SendAnsPointer;
					SendAnsAddr++;
				}
			//System.out.println("The answer of SendAnswer is : " + answer );
			}
			System.out.println("The answer of SendAnswer is : " + answer );
			
			 //set the message type to be answer
			//read the buffer id from the second input parameter and get the buffer from the buffer pool
			 int bufferId = Machine.readRegister(5);
			 //Buffer bufferSendAns = JNachos.mBufferPool.BufferMap.get(bufferId);
		
			 Buffer bufferSendAns = JNachos.mBufferPool.getBufById(bufferId);
			 
			 bufferSendAns.SetReceiver(bufferSendAns.GetSender()); 
			 bufferSendAns.SetSender(JNachos.getCurrentProcess().getName());
			 System.out.println("The sender of the SendAnswer is: "+ bufferSendAns.GetSender());
			 //set the original sender to be the receiver answer
			 
			 bufferSendAns.SetMessage(answer);
			   
			 //get receive process from table
			 NachosProcess receiverAnswerProcess = JNachos.ProcessNameTable.get(bufferSendAns.GetReceiver());
			 if(receiverAnswerProcess != null) {
				//if the receive process exists, deliver the buffer to its queue
			    receiverAnswerProcess.ProcessQueue.add(bufferSendAns);
			    Machine.writeRegister(2, 1);
			    System.out.println("The answer " + answer + " in the Buffer " + bufferSendAns.GetBufferId()
			     +" has been sent from " + bufferSendAns.GetSender()+" to " + bufferSendAns.GetReceiver());
			  }else {
				//it not exists, return the buffer
			    JNachos.mBufferPool.returnBufToPool(bufferSendAns);
			    Machine.writeRegister(2, 0);
			    System.out.println("The answer receiver " + bufferSendAns.GetReceiver() + " does not exist.");
			   }						
			Interrupt.setLevel(true);
			break;
		
		
		case SC_WaitAnswer:
			Interrupt.setLevel(false); 
            System.out.println("Process" + JNachos.getCurrentProcess().getName() + " is calling WaitAnswer.\n" );
            
          //PCcount + 4 to enter the next instruction
			PCcount = Machine.readRegister(Machine.NextPCReg);
			Machine.writeRegister(Machine.PCReg, PCcount);
            
          //for buffer
			boolean valid_WaitAns = false;
			while(!valid_WaitAns) {
				NachosProcess WaitProcess = JNachos.getCurrentProcess();
				//wait the process until an answer arrives in its queue
				while( WaitProcess.ProcessQueue.isEmpty() ) {
					WaitProcess.yield();	
			    }
				
			//get the answer
			//Buffer bufferWaitM = WaitProcess.getmBufferQ().remove();
			Buffer bufferWaitAns = WaitProcess.ProcessQueue.remove();
			
			//check if its id is the same as the id of second parameter
			int bufferIdfromPara = Machine.readRegister(5);
			if(bufferWaitAns.GetBufferId() == bufferIdfromPara ) {
				System.out.println("The buffer of WaitMessage is validated");
				
				//get memory address from the first input parameter
				int AnsAddrWaitA = Machine.readRegister(4);
				//message AnsWaitAns
				String AnsWaitAns = bufferWaitAns.GetMessage();
				System.out.println("The buffer "+ bufferWaitAns.GetBufferId() + " received message from " + bufferWaitAns.GetSender()
				           +" to " + bufferWaitAns.GetReceiver() );

				//copy the message to write memory
				for(int i = 0; i < AnsWaitAns.length(); i++) {
					Machine.writeMem(AnsAddrWaitA, 1, AnsWaitAns.charAt(i));
					AnsAddrWaitA++;
				}
				
				//add \0 to the end of a string
				Machine.writeMem(AnsAddrWaitA, 1, '\0');
				//check if the answr is equals to the dummy answer from system
				if(bufferWaitAns.GetSender().equals("System Kernel")) {
					//if equeals, return value will be 0
					Machine.writeRegister(2, 0);
					System.out.println("Received answer is a dummy answer from system");
				}
			    else 
				//if not, return value is 1
				Machine.writeRegister(2, 1);
				//return the buffer to the buffer pool
				JNachos.mBufferPool.returnBufToPool(bufferWaitAns);
				valid_WaitAns = true;
			} else {
				//if ID are not same, the buffer is invalid
				System.out.println("The buffer is invalid from " + bufferWaitAns.GetSender());
				//return the buffer to buffer pool
				JNachos.mBufferPool.returnBufToPool(bufferWaitAns);
			}
		}
		Interrupt.setLevel(true);
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
			PCcount = Machine.readRegister(Machine.NextPCReg);
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
