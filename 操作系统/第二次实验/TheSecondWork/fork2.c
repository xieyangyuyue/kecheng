#include <stdio.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>

int main() {
    pid_t pid; // 用于存储子进程的PID

    /* fork another process */
    pid = fork(); // 创建一个子进程
    if (pid < 0) { // 出现错误
        perror("Fork failed");
        return 1; // 返回非0表示出现错误
    }
    else if (pid == 0) { // 子进程
        // 使用execlp执行ls命令
        execlp("ls", "ls", NULL); // 运行ls命令
        // 如果execlp失败，输出错误信息
        perror("execlp failed");
        return 1; // 返回子进程出错
    }
    else { // 父进程
        /* parent will wait for the child to complete */
        wait(NULL); // 等待子进程完成
        printf("Child Complete\n"); // 子进程完成后输出信息
    }

    return 0; // 正常结束
}
