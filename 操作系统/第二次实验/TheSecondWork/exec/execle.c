#include <unistd.h>
#include <stdio.h>

int main() {
    printf("Before execle\n");
    char *envp[] = {"MY_ENV=some_value", NULL}; // Custom environment variables
    execle("/bin/ls", "ls", "-l", (char *)NULL, envp);
    perror("execle failed"); // If execle fails, this line will be executed.
    return 0;
}
