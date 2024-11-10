#include <unistd.h>
#include <sys/types.h>
#include <errno.h>
#include <sys/wait.h>
#include <stdlib.h>
#include <stdio.h>

int main() {
    pid_t childpid;
    int retval;

    /* create new process */
    childpid = fork();
    if (childpid >= 0) {
        if (childpid == 0) { /* child process */
            printf("CHILD: I am the child process!\n");
            sleep(1);
            printf("CHILD: Enter an exit value (0~255): ");
            scanf("%d", &retval);
            printf("CHILD: Goodbye!\n");
            exit(retval); /* child exits */
        } else { /* parent process */
            printf("PARENT: I am the parent process!\n");
            printf("PARENT: Here's my PID: %d\n", getpid());
            printf("PARENT: The value of my child's PID is: %d\n", childpid);
            printf("PARENT: Goodbye!\n");
            exit(0); /* parent exits */
            // wait(&status); // This line is removed
        }
    } else { /* fork returns -1 on failure */
        perror("fork error!");
        exit(0);
    }
}
