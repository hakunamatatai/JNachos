package jnachos.kern;

public class Buffer {
	private String message;
	
	private int BufferId;
	private static int messageLength = 50;
	private String sender;
	private String receiver;
	
	private MessageType type;
	//Message or answer
	private String MesAns;
	private boolean free = true; 
	
	
	//buffer id
	public Buffer(int bufferid) {
		BufferId = bufferid;
	}
	public int GetBufferId() {
		return BufferId;
	}
	
	
	//message
	public void SetMessage(String Message) {
		if(Message.length() <= messageLength) {
			message=Message;
		}		
	}
	public String GetMessage() {
		System.out.println(message);
		return message;
	}	
	
	
	//sender
	public void SetSender(String Sender){
		sender=Sender;
		//message="This is the num "+MessageId+" message from "+sender+" to "+receiver;		
	}	
	public String GetSender() {
		return sender;
	}
	
	
	//receiver
	public void SetReceiver(String Receiver){
		receiver=Receiver;
		//message="This is the num "+MessageId+" message from "+sender+" to "+receiver;		
	}
	public String GetReceiver() {
		return receiver;
	}
	
	
	//MessageType
	public void setMessageType(MessageType Type) {
		type = Type;
	}
	public MessageType getMessageType() {
		return type;
	}
	
	
	
	//boolean
	public void SetFree(boolean Free) {
		free = Free;
	}
	public boolean GetFree() {
		return free;
	}
}

