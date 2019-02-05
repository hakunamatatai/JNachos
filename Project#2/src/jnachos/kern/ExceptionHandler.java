/**
 * Copyright (c) 1992-1993 The Regents of the University of California.
 * All rights reserved.  See copyright.h for copyright notice and limitation 
 * of liability and disclaimer of warranty provisions.
 *
 *  Created by Patrick McSweeney on 12/13/08.
 *
 */
package jnachos.kern;

import java.util.Arrays;

import jnachos.machine.*;

/**
 * The ExceptionHanlder class handles all exceptions raised by the simulated
 * machine. This class is abstract and should not be instantiated.
 */
public abstract class ExceptionHandler {

	/**
	 * This class does all of the work for handling exceptions raised by the
	 * simulated machine. This is the only funciton in this class.
	 *
	 * @param pException
	 *            The type of exception that was raised.
	 * @see ExceptionType.java
	 */
	public static void handleException(ExceptionType pException) {
		switch (pException) {
		// If this type was a system call
		case SyscallException:

			// Get what type of system call was made
			int type = Machine.readRegister(2);

			// Invoke the System call handler
			SystemCallHandler.handleSystemCall(type);
			break;

		case PageFaultException:
			int virtualPgNum = Machine.readRegister(39)/Machine.PageSize;
			int checkPageNum = JNachos.getCurrentProcess().getSpace().mFreeMap.find();
			if (checkPageNum != -1) {		
//				int virtualPgNum = Machine.readRegister(Machine.BadVAddrReg)/Machine.PageSize;
				//put in queue
//				QueueManage.printQueue(JNachos.queue);
				entry pventry=new entry();
				pventry.setProcess(JNachos.getCurrentProcess());
				pventry.setVirtualPageNumbaer(virtualPgNum);
				JNachos.queue.offer(pventry);
//				QueueManage.printQueue(JNachos.queue);
			    //MMU.mPageTable[virtualPgNum] = 
				
				TranslationEntry[] pageTable = JNachos.getCurrentProcess().getSpace().getPageTable();
				
				pageTable[virtualPgNum].physicalPage=checkPageNum;
				pageTable[virtualPgNum].valid=true;			
				//JNachos.getCurrentProcess().getSpace().setPageTable(pageTable);
				
				if ( pageTable[virtualPgNum].dirty == true ) {
					byte[] memory_1 = new byte[Machine.PageSize];
					System.arraycopy(Machine.mMainMemory, pageTable[virtualPgNum].physicalPage * Machine.PageSize, memory_1, 0, 
							Machine.PageSize);
					//JNachos.SwapSpace.readAt(memory_1, memory_1.length, pageTable[virtualPgNum].swapSpacePlace * Machine.PageSize);
					JNachos.mSwapSpace.readAt(memory_1, Machine.PageSize, pageTable[virtualPgNum].swapSpacePlace * Machine.PageSize);
					pageTable[virtualPgNum].dirty = false;
				}
				
				//System.out.println(" virtual page "+virtualPgNum);			
				//System.out.println("physical page "+ pageTable[virtualPgNum].physicalPage);
						
				// Create a temporary buffer to copy the code
				byte[] bytes = new byte[Machine.PageSize];			
				JNachos.mSwapSpace.readAt(bytes, bytes.length, pageTable[virtualPgNum].swapSpacePlace * Machine.PageSize);
				
				Arrays.fill(Machine.mMainMemory, pageTable [virtualPgNum].physicalPage * Machine.PageSize,
						(pageTable[virtualPgNum].physicalPage + 1) * Machine.PageSize, (byte) 0);
				System.arraycopy(bytes, 0, Machine.mMainMemory, pageTable[virtualPgNum].physicalPage * Machine.PageSize,
						Machine.PageSize);			
								
			}
			else {
				
				//System.out.println("The size of the queue: "+JNachos.queue.size());
				entry VirtualOut = JNachos.queue.poll();
				TranslationEntry[] OutPageTable = VirtualOut.getProcess().getSpace().getPageTable();	
				TranslationEntry[] CurrentPageTable = JNachos.getCurrentProcess().getSpace().getPageTable();
	
				
				int outVirtualNum = VirtualOut.getVirtualPageNum();
				int physicalPageNum = OutPageTable[outVirtualNum].physicalPage;
				//OutPageTable[outVirtualNum].physicalPage = -1;
				//OutPageTable[outVirtualNum].valid=false;
				VirtualOut.getProcess().getSpace().setPageTable(OutPageTable);
				
				if ( OutPageTable[outVirtualNum].dirty == true ) {
					byte[] memory = new byte[Machine.PageSize];
					System.arraycopy(Machine.mMainMemory, physicalPageNum * Machine.PageSize, memory, 0, Machine.PageSize);
					JNachos.mSwapSpace.writeAt(memory, Machine.PageSize, OutPageTable[outVirtualNum].swapSpacePlace * Machine.PageSize);
					OutPageTable[virtualPgNum].dirty = false;
				}
				
				OutPageTable[outVirtualNum].physicalPage = -1;
				OutPageTable[outVirtualNum].valid=false;
	
				//MMU.mPageTable
				
				CurrentPageTable[virtualPgNum].physicalPage=physicalPageNum;
				CurrentPageTable[virtualPgNum].valid=true;
				JNachos.getCurrentProcess().getSpace().setPageTable(CurrentPageTable);
				

//				System.out.println("Virtual Page #"+virtualPgNum+" replace Page #"+outVir);
//				System.out.println("Replace into physical page #"+ CurrentPageTable[virtualPgNum].physicalPage);

	
				byte[] bytes = new byte[Machine.PageSize];
				JNachos.mSwapSpace.readAt(bytes, bytes.length, CurrentPageTable[virtualPgNum].swapSpacePlace * Machine.PageSize);
				
				Arrays.fill(Machine.mMainMemory, CurrentPageTable[virtualPgNum].physicalPage * Machine.PageSize,
						(CurrentPageTable[virtualPgNum].physicalPage + 1) * Machine.PageSize, (byte) 0);
				System.arraycopy(bytes, 0, Machine.mMainMemory, CurrentPageTable[virtualPgNum].physicalPage * Machine.PageSize,
						Machine.PageSize);
	
				
				entry pventry=new entry();
				pventry.setProcess(JNachos.getCurrentProcess());
				pventry.setVirtualPageNumbaer(virtualPgNum);
				JNachos.queue.offer(pventry);
				
				JNachos.pagefaultNum++;
				System.out.println("Number of Page Fault: "+JNachos.pagefaultNum);
			    Statistics.numPageFaults++;
			}
			
			
			break;	
		// All other exceptions shut down for now
		default:
			System.exit(0);
		}
	}
}
