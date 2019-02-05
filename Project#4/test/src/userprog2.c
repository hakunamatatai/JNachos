#include "syscall.h"

char sender[]="test/userprog1";
char message[]="";
char answer[]="age1";

int main(){
	int bufferId=WaitMessage(sender,message);
	int y=SendAnswer(answer,bufferId);
	Exit(1002);
	return 0;
}
