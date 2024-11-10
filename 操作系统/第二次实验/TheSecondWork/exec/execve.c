#include <unistd.h>
#include <stdio.h>

int main() {
    char *args[] = {"ls", "-l", NULL};
    char *env[] = {"MY_ENV=some_value", NULL};
    printf("Before execve\n");
    execve("/bin/ls", args, env); // Directly provide the executable path and environment variables.
    perror("execve failed"); // If execve fails, this line will be executed.
    return 0;
}
