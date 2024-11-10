#include <semaphore.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <stdbool.h>
#include <unistd.h> // 为了使用 sleep 函数

#define MAX 256         // 每个产品的最大长度
#define BUFFER_SIZE 10  // 缓冲区大小
char *buffer[BUFFER_SIZE]; // 使用数组来存储多个产品
int in = 0;       // 生产者插入窗口
int out = 0;      // 消费者取出窗口

sem_t empty;      // 定义同步信号量empty
sem_t full;       // 定义同步信号量full
sem_t mutex;      // 定义互斥信号量mutex

void *producer() {
    while (true) {  // 使生产者持续生产
        sem_wait(&empty);  // empty的P操作
        sem_wait(&mutex);  // mutex的P操作

        buffer[in] = (char *)malloc(MAX); // 为缓冲区分配内存空间
        if (buffer[in] == NULL) {
            perror("malloc failed");
            exit(EXIT_FAILURE);
        }

        printf("Input something to buffer: ");
        fgets(buffer[in], MAX, stdin);  // 输入产品至缓冲区
        in = (in + 1) % BUFFER_SIZE; // 更新插入窗口
        
        sem_post(&mutex);  // mutex的V操作
        sem_post(&full);   // full的V操作

        sleep(1);  // 暂停一秒，模拟生产时间
    }
}

void *consumer() {
    while (true) {  // 使消费者持续消费
        sem_wait(&full);  // full的P操作
        sem_wait(&mutex);  // mutex的P操作
        
        printf("Read product from buffer: %s", buffer[out]); // 从缓冲区中取出产品
        free(buffer[out]); // 释放缓冲区内存
        buffer[out] = NULL; // 清空缓冲区
        out = (out + 1) % BUFFER_SIZE; // 更新取出窗口
        
        sem_post(&mutex);  // mutex的V操作
        sem_post(&empty);  // empty的V操作

        sleep(2); // 暂停两秒，模拟消费时间
    }
}

int main() {
    pthread_t id_producer;
    pthread_t id_consumer;
    int ret;

    sem_init(&empty, 0, BUFFER_SIZE); // 设置empty到初值为BUFFER_SIZE
    sem_init(&full, 0, 0);             // 设置full到初值为0
    sem_init(&mutex, 0, 1);            // 设置mutex到初值为1

    ret = pthread_create(&id_producer, NULL, producer, NULL); // 创建生产者线程
    if (ret != 0) {
        perror("Failed to create producer thread.");
        exit(EXIT_FAILURE);
    }

    ret = pthread_create(&id_consumer, NULL, consumer, NULL); // 创建消费者线程
    if (ret != 0) {
        perror("Failed to create consumer thread.");
        exit(EXIT_FAILURE);
    }

    pthread_join(id_producer, NULL); // 等待生产者线程结束
    pthread_join(id_consumer, NULL); // 等待消费者线程结束

    // 销毁信号量
    sem_destroy(&empty);
    sem_destroy(&full);
    sem_destroy(&mutex);

    printf("The End...\n");
    return 0;
}

