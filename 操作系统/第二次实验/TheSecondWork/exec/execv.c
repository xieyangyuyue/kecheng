#include <unistd.h>
#include <stdio.h>

int main() {
    char *args[] = {"ls", "-l", NULL}; // The array of arguments
    printf("Before execv\n");
    execv("/bin/ls", args);
    perror("execv failed"); // If execv fails, this line will be executed.
    return 0;
}
