ReadMe –Synchronization   

1. Write Peroxide.java   
In this lab, Create a new file called Peroxide.java. The atomic for-mula for Hydrogen Peroxide is H2O2. Write a synchronization program to generate Hydrogen Peroxide molecules. Based on JNachosLab2Solution.   


2. Class HAtom:   
First, mutex semaphore calls P() to allocate one resource for mutex.   
Then for first hAtom, the count for the first H has initiate value as 0. So count % 2 = 0. The count will change from 0 to 1. Then mutex semaphore calls V() to release one resource. This is used for protecting only add one to count in one hAtom. The first H will wait for the seconde hAtom. Wait calls P().   
Then for the second hAtom, the count is 1, so count % 2 != 0. Then count add one. Then mutex semaphore calls V() to release one resource. Then the first hAtom and two OAtom which has already waited will be waked up. Wait calls P(). However, there are still two wait resource, so the three hAtoms will wait for the wait.V() releasing resource in OAtom.   


3. Class OAtom:   
For OAtom, the first H will wait for the seconde hAtom. mutex2 semaphore calls P() to allocate one resource for mutex2. Then for first OAtom, the count for the first O has initiate value as 0. So count % 2 = 0. The count will change from 0 to 1. Then mutex2 semaphore calls V() to release one resource. Wait1 calls P().The first O will wait for the seconde OAtom. 
Then for the second hAtom, the count is 1, so count % 2 != 0. Then count add one. Then mutex2 semaphore calls V() to release one resource.    
Then two wait.V() will be called to wake up OAtoms and one wait1.V() will be called to wake up hAtoms. Then Hcount minus two and Ocount minus two to generate one peroxide. The mutex1.P() and mute1.V() called here is used for protecting generating one peroxide one single process. Then it will continue to create another peroxide.   
