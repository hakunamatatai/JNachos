package jnachos.kern;

import java.util.Queue;

import jnachos.machine.entry;

public class QueueManage {
	
	public static void printQueue(Queue<entry> queue) {
		for(entry pventry: queue) {
			
			System.out.println("PID:"+ pventry.getProcess().getId());
			System.out.println("VirtualPage: "+ pventry.getVirtualPageNum());
		}
	}
	
}
