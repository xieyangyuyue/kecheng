#include <unistd.h>
#include <stdio.h>

int main() {
    printf("Before execl\n");
    execl("/bin/ls", "ls", "-l", (char *)NULL);
    perror("execl failed"); // If execl fails, this line will be executed.
    return 0;
}
