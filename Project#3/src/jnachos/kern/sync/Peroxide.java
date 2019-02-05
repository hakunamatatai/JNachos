/**
 *  <MakeWater Problem>
 *  Author: Yuchang Chen
 *  
 *  Created by Patrick McSweeney on 12/17/08.
 */
package jnachos.kern.sync;

import java.io.*;
import jnachos.kern.*;

/**
 *
 */
public class Peroxide {

	/** Semaphore H */
	static Semaphore H = new Semaphore("SemH", 0);

	/**	*/
	static Semaphore O = new Semaphore("SemO", 0);

	/**	*/
	static Semaphore wait = new Semaphore("wait", 0);
	
	static Semaphore wait1 = new Semaphore("wait", 0);

	/**	*/
	static Semaphore mutex = new Semaphore("MUTEX", 1);

	/**	*/
	static Semaphore mutex1 = new Semaphore("MUTEX1", 1);
	
	static Semaphore mutex2 = new Semaphore("MUTEX2", 1);

	/**	*/
	static long count = 0;
	static long count1 = 0;

	/**	*/
	static int Hcount, Ocount, nH, nO;
	//Hcount, Ocount are input

	/**	*/
	class HAtom implements VoidFunctionPtr {
		int mID;

		public HAtom(int id) {
			mID = id;
		}

		/**
		 * oAtom will call oReady. When this atom is used, do continuous
		 * "Yielding" - preserving resource
		 */
		public void call(Object pDummy) {
			mutex.P();
			if (count % 2 == 0) // first H atom , if count is even, if even, return 1
			{
				//use V to wake up
				count++; // increment counter for the first H
				mutex.V(); // Critical section ended
				H.P(); // Waiting for the second H atom
			} else // second H atom
			{
				//use V to wake up
				count++; // increment count for next first H
				
				mutex.V(); // Critical section ended，then could execute			
				//if h odd, 
				H.V(); // wake up the first H atom
				O.V(); // wake up O atom
				O.V();
			}
			wait.P(); // wait for water message done
			System.out.println("H atom #" + mID + " used in making peroxide.");
		}
	}

	/**	*/
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
			mutex2.P();
			if (count1 % 2 == 0) // first H atom 
			{
				count1++;
				mutex2.V();
				//O.P(); // waiting for two H atoms.
				wait1.P();	
				//System.out.println("O atom #" + mID + " used in making peroxide.");
			}
			else {
				count1++;	
				mutex2.V();
									
				wait.V(); // wake up H atoms and they will return to
				wait.V(); // resource pool
				wait1.V();
				
				//System.out.println("O atom #" + first_wait_Oid + " used in making peroxide.");
				
				mutex1.P();
				makeWater();
				//end O atom
				Hcount = Hcount - 2;
				Ocount = Ocount - 2;
				System.out.println("Numbers Left: H Atoms: " + Hcount + ", O Atoms: " + Ocount);
				System.out.println("Numbers Used: H Atoms: " + (nH - Hcount) + ", O Atoms: " + (nO - Ocount));
				mutex1.V();
				
				//System.out.println("O atom #" + mID + " used in making peroxide.");
				//System.out.println("O atom #" + first_wait_Oid + " used in making peroxide.");
			}			
			System.out.println("O atom #" + mID + " used in making peroxide.");
			//O.P();
		}
	}

	/**
	 * oAtom will call oReady. When this atom is used, do continuous "Yielding"
	 * - preserving resource
	 */
	public static void makeWater() {
		System.out.println("** Water made! Splash!! **");
	}

	/**
	 * oAtom will call oReady. When this atom is used, do continuous "Yielding"
	 * - preserving resource
	 */
	public Peroxide() {
		runWater();
	}

	/**
	 *
	 */
	public void runWater() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Number of H atoms ? ");
			nH = (new Integer(reader.readLine())).intValue();
			System.out.println("Number of O atoms ? ");
			nO = (new Integer(reader.readLine())).intValue();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Hcount = nH;  //nH: input
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
