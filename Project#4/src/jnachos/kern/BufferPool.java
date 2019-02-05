package jnachos.kern;
import java.util.HashMap;

enum MessageType {
	MESSAGE, ANSWER
}



//Buffer Pool class
public class BufferPool {
	public static final int BufPool_Size = 8;
	private int freeBuf;
	private HashMap<Integer, Buffer> BufPool_Element = new HashMap<Integer, Buffer>();
	
	public BufferPool() {
		freeBuf = BufPool_Size;
		//initialize all buffers with an ID and put them in BufPool_Element;
		for(int i = 0; i < BufPool_Size; i++) {
			Buffer buffer = new Buffer(i);
			BufPool_Element.put(i, buffer);
		}
	}
	
	//get a free Buffer
	public synchronized Buffer getFreeBuf() {
		while(freeBuf == 0) {
			try {
				wait();
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		
		//iterate the buf_pool to find the first available buffer
		for (int i = 0; i < BufPool_Size; i++) {
			Buffer buffer = BufPool_Element.get(i);
			if (buffer.GetFree() == true) {
				buffer.SetFree(false);
				//reverse
				freeBuf--;
				return buffer;
			}
		}
		return null;
	}
	
	//get a buffer by its id
	public synchronized Buffer getBufById(int ID) {
		return BufPool_Element.get(ID);
	}
	
	//return a buffer to buffer pool
	public synchronized void returnBufToPool(Buffer buffer) {
		System.out.println("Return buffer" + buffer.GetBufferId() + "to buffer pool");
		
		//reset parameters
		buffer.SetSender("");
		buffer.SetReceiver("");
		buffer.SetFree(true);
		buffer.SetMessage("");
		freeBuf++;
		notify();
	}
}











