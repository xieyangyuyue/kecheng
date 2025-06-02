CODE SEGMENT
               ASSUME CS:CODE
    START:     
               MOV    AX, 0F800H    ; 段基址（F8000H）
               MOV    DS, AX        ; DS指向共享段（奇偶存储体共用）

               MOV    DS:[0],AX

               MOV    SI, 0000H     ; 偶地址偏移（A0=0）
               MOV    DI, 0001H     ; 奇地址偏移（A0=1）
               MOV    CX, 0800H     ; 循环次数（2K次，覆盖4KB）
               MOV    AL, 11H       ; 写入值

    WRITE_LOOP:
               MOV    [SI], AL      ; 写入偶存储体（A0=0）
               MOV    [DI], AL      ; 写入奇存储体（A0=1）
               ADD    SI, 2         ; 步进2字节（下一个偶地址）
               ADD    DI, 2         ; 步进2字节（下一个奇地址）
               LOOP   WRITE_LOOP

               JMP    $             ; 程序结束
CODE ENDS
 END START
