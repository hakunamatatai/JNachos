# JNachosOperatingSystem
JNachos Operating System is an instructional Operating System written in Java that runs as a UNIX process, and it simulates the functionality of the Linux Operating System. It provides a skeletal Operating System that supports threads, user-level processes, virtual memory, swap space and so on. 

## Project
### Project 1
Implemented execution of user programs written in C source code on Java platform.
Implemented multi-programming on a single processor.
Implemented the system calls "fork", "join", "exec", and "exit".

### Project 2
Designed virtual memory.Created a Swap Space to supply pages to main memory. The virtual memory is kept in the swap space.
Handled page fault and designed page replacement based on FIFO.

### Project 3
Implemented synchronization through Semaphore in creating H2O and H2O2.
Implemented a synchronization program to generate Hydrogen Peroxide molecules.

### Project 4
Designed Nucleus Message Passing in Nachos System for communication between processes
1. SC_SendMessage: An asynchronized system call. 
Send message copies a message into the first available buffer within the pool and delivers it in the queue of a named receiver.
2. SC_SendAnswer: A asynchronized system call. 
Send answer copies an answer into a buffer in which a message has been received and delivers it in the queue of the original sender.
3. SC_WaitMessage: An synchronized system call. 
Wait message delays the requesting process until a message arrives in its queue.
4. SC_WaitAnswer: A synchronized system call. 
Wait answer delays the requesting process until an answer arrives in a given buffer.

