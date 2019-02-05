package jnachos.kern.sync;

import java.io.*;

import jnachos.kern.*;


public class Peroxide {
	/** Semaphore H */
	static Semaphore H = new Semaphore("SemH", 0);

	/**	Semaphore O */
	static Semaphore O = new Semaphore("SemO", 0);

	/**	Semaphore wait */
	static Semaphore wait = new Semaphore("wait", 0);
	
	/**	Semaphore wait1 */
	static Semaphore wait1 = new Semaphore("wait1", 0);

	/**	Semaphore mutexH */
	static Semaphore mutexH = new Semaphore("MUTEX", 1);

	/**	Semaphore mutexOutput */
	static Semaphore mutexOutput = new Semaphore("MUTEX1", 1);
	
	/**	Semaphore mutexO */
	static Semaphore mutexO = new Semaphore("MUTEX1", 1);
	
	/**	The count of the H */
	static long count = 0;
	
	/**	The count of the O */
	static long count1 = 0;

	/**	H and O count in the print */
	static int Hcount, Ocount, nH, nO;
	
	static int previousId=0;

	/**	class HAtom */
	class HAtom implements VoidFunctionPtr {
		int mID;

		/**
		 *
		 */
		public HAtom(int id) {
			mID = id;
		}

		/**
		 * oAtom will call oReady. When this atom is used, do continuous
		 * "Yielding" - preserving resource
		 */
		public void call(Object pDummy) {		
			mutexH.P();
			if (count % 2 == 0) // first H atom
			{
				count++; // increment counter for the first H
				mutexH.V(); // Critical section ended
				H.P(); // Waiting for the second H atom
//				wait1.V();
//				O.P();
			} else // second H atom
			{
				count++; // increment count for next first H			
				mutexH.V(); // Critical section ended
				
				H.V(); // wake up the first H atom
				O.V(); // wake up O atom	
				O.V(); // wake up another O atom
				
			}

			wait.P(); // wait for water message done			
			System.out.println("H atom #" + mID + " used in making peroxide.");
			
		}
	}

	/**	class OAtom */
	class OAtom implements VoidFunctionPtr {
		int mID;

		/**
		 * oAtom will call oReady. When this atom is used, do continuous
		 * "Yielding" - preserving resource
		 */
		public OAtom(int id) {
			mID = id;
		}

		/**
		 * oAtom will call oReady. When this atom is used, do continuous
		 * "Yielding" - preserving resource
		 */
		public void call(Object pDummy) {
			O.P();
			mutexO.P();
			if (count1 % 2 == 0) // first H atom
			{
				count1++; // increment counter for the first H
				mutexO.V();
//				previousId = mID;				
				wait1.P(); // Waiting for the second O atom	

			} else // second H atom
			{
				count1++; // increment count for next first H
				mutexO.V();							
		
				wait.V(); // wake up H atoms and they will return to
				wait.V(); // resource pool
				wait1.V();//wake the first O atom
								
				mutexOutput.P();
				makePeroxide();
				Hcount = Hcount - 2;
				Ocount = Ocount - 2 ;
				System.out.println("Numbers Left: H Atoms: " + Hcount + ", O Atoms: " + Ocount);
				System.out.println("Numbers Used: H Atoms: " + (nH - Hcount) + ", O Atoms: " + (nO - Ocount));
											
				mutexOutput.V();	
					
			}
			System.out.println("O atom #" + mID + " used in making peroxide.");
		}
	}

	/**
	 * oAtom will call oReady. When this atom is used, do continuous "Yielding"
	 * - preserving resource
	 */
	public static void makePeroxide() {
		System.out.println("** Peroxide made! Splash!! **");
	}

	/**
	 * oAtom will call oReady. When this atom is used, do continuous "Yielding"
	 * - preserving resource
	 */
	public Peroxide() {
		runPeroxide();
	}

	/**
	 *
	 */
	public void runPeroxide() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Number of H atoms ? ");
			nH = (new Integer(reader.readLine())).intValue();
			System.out.println("Number of O atoms ? ");
			nO = (new Integer(reader.readLine())).intValue();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Hcount = nH;
		Ocount = nO;

		for (int i = 0; i < nH; i++) {
			HAtom atom = new HAtom(i);
			(new NachosProcess(new String("hAtom" + i))).fork(atom, null);
		}

		for (int j = 0; j < nO; j++) {
			OAtom atom = new OAtom(j);
			(new NachosProcess(new String("oAtom" + j))).fork(atom, null);
		}
	}
}
