#include "syscall.h"
char message[]="Message1";
char answer[]="";
char receiver[]="test/userprog2";

int main(){
	int bufferId=SendMessage(receiver,message);
	int x=WaitAnswer(answer,bufferId);
	Exit(1001);
	return 0;
}
