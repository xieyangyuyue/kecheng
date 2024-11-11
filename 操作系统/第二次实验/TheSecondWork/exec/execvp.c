#include <unistd.h>
#include <stdio.h>

int main() {
    char *args[] = {"ls", "-l", NULL};
    printf("Before execvp\n");
    execvp("ls", args); // Will look for `ls` in $PATH
    perror("execvp failed"); // If execvp fails, this line will be executed.
    return 0;
}
