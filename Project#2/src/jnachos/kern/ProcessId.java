/*
 * File Name: ProcessId.java
 * Date: 9/20/2017
 * Description:	This class ProcessId is build for the JNachos to store the id and the waiting
 * 	Hash map of the process
 */

package jnachos.kern;

import java.util.HashMap;
import java.util.Map;

public class ProcessId {
	
	//The start process id
	static private int id=100;
	
	//The hushMap to store the waiting relation
	static public HashMap IdMap = new HashMap ();
	
	//to give the id to process
	static public int giveId() {
		id=id+1;
		return id;
	}
}
