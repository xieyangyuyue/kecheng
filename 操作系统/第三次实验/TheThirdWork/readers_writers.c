#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <semaphore.h>
#include <unistd.h> // for sleep

#define READERS_COUNT 5
#define WRITERS_COUNT 2

// 读者-写者问题的数据结构
typedef struct {
    int readers;               // 当前活跃的读者数量
    sem_t mutex;              // 保护读者计数的互斥锁
    sem_t resource_lock;      // 保护对共享资源的信号量
} ReadersWriters;

ReadersWriters rw;

// 读者线程函数
void *reader(void *id) {
    int reader_id = *((int *)id);
    
    while (1) {
        // 模拟读者思考
        sleep(rand() % 3);

        printf("Reader %d is trying to read.\n", reader_id);
        
        // 进入临界区
        sem_wait(&rw.mutex);
        rw.readers++;
        if (rw.readers == 1) {
            sem_wait(&rw.resource_lock);  // 第一个读者阻止写者
        }
        sem_post(&rw.mutex);

        // 开始读取
        printf("Reader %d is reading.\n", reader_id);
        sleep(rand() % 3);  // 模拟读取时间

        // 结束读取
        sem_wait(&rw.mutex);
        rw.readers--;
        if (rw.readers == 0) {
            sem_post(&rw.resource_lock);  // 最后一个读者释放写者
        }
        sem_post(&rw.mutex);

        printf("Reader %d finished reading.\n", reader_id);
    }
}

// 写者线程函数
void *writer(void *id) {
    int writer_id = *((int *)id);
    
    while (1) {
        // 模拟写者思考
        sleep(rand() % 3);

        printf("Writer %d is trying to write.\n", writer_id);
        sem_wait(&rw.resource_lock);  // 等待访问
        // 开始写作
        printf("Writer %d is writing.\n", writer_id);
        sleep(rand() % 3);  // 模拟写作时间
        sem_post(&rw.resource_lock);  // 释放访问
        printf("Writer %d finished writing.\n", writer_id);
    }
}

int main() {
    pthread_t readers[READERS_COUNT], writers[WRITERS_COUNT];
    int reader_ids[READERS_COUNT], writer_ids[WRITERS_COUNT];

    // 初始化读者-写者管理器
    rw.readers = 0;
    sem_init(&rw.mutex, 0, 1);              // 初始化互斥锁
    sem_init(&rw.resource_lock, 0, 1);      // 初始化资源锁

    // 创建读者线程
    for (int i = 0; i < READERS_COUNT; i++) {
        reader_ids[i] = i + 1;
        pthread_create(&readers[i], NULL, reader, (void *)&reader_ids[i]);
    }

    // 创建写者线程
    for (int i = 0; i < WRITERS_COUNT; i++) {
        writer_ids[i] = i + 1;
        pthread_create(&writers[i], NULL, writer, (void *)&writer_ids[i]);
    }

    // 等待线程结束（无限等待）
    for (int i = 0; i < READERS_COUNT; i++) {
        pthread_join(readers[i], NULL);
    }
    for (int i = 0; i < WRITERS_COUNT; i++) {
        pthread_join(writers[i], NULL);
    }

    // 销毁信号量
    sem_destroy(&rw.mutex);
    sem_destroy(&rw.resource_lock);

    return 0;
}

