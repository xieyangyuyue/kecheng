;=============================================
; 程序功能：统计字节数组中正数、负数、零的个数
;=============================================

DATAS SEGMENT
    BUF   DB -1,0,5,-3,2,0,-7,8,0,4    ; 测试数据（10个字节）
    N     =  $ - BUF                   ; 计算数组长度（N=10）
    NUM   DB 3 DUP(?)                  ; 结果存储区：NUM[0]=正数，NUM[1]=负数，NUM[2]=0
DATAS ENDS

STACKS SEGMENT
           DB 100H DUP(?)    ; 预留256字节堆栈空间
STACKS ENDS

CODES SEGMENT
                ASSUME CS:CODES, DS:DATAS, SS:STACKS

    START:      
    ;----- 初始化数据段 -----
                MOV    AX, DATAS
                MOV    DS, AX

    ;----- 初始化指针和计数器 -----
                LEA    SI, BUF                          ; SI指向数组首地址
                MOV    CX, N                            ; CX=10（循环次数=数组长度）
                XOR    BX, BX                           ; BH=正数计数器，BL=负数计数器
                XOR    DX, DX                           ; DH=0计数器（DX高位）

    ;----- 主循环：统计三类数值 -----
    LOOP_START: 
                MOV    AL, [SI]                         ; 读取当前元素
                INC    SI                               ; 指针后移
                CMP    AL, 0                            ; 与0比较
                JE     IS_ZERO                          ; 等于0 → 跳转零处理
                JS     IS_NEGATIVE                      ; 符号位为1（负数）→ 跳转负数处理
    ;----- 正数处理 -----
                INC    BH                               ; 正数计数器+1
                JMP    NEXT_ITER
    IS_ZERO:    
                INC    DH                               ; 零计数器+1
                JMP    NEXT_ITER
    IS_NEGATIVE:
                INC    BL                               ; 负数计数器+1
    NEXT_ITER:  
                LOOP   LOOP_START                       ; CX--，循环直到CX=0

    ;----- 存储统计结果 -----
                MOV    NUM, BH                          ; 保存正数个数
                MOV    NUM+1, BL                        ; 保存负数个数
                MOV    NUM+2, DH                        ; 保存零个数

    ;----- 输出结果（带空格分隔） -----
                LEA    SI, NUM                          ; SI指向结果数组
                MOV    CX, 3                            ; 循环3次（正/负/零）
    OUTPUT_LOOP:
                MOV    DL, [SI]                         ; 读取统计值
                ADD    DL, '0'                          ; 转换为ASCII码
                MOV    AH, 02H                          ; DOS功能：字符输出
                INT    21H
                MOV    DL, ' '                          ; 输出空格分隔符
                INT    21H
                INC    SI                               ; 移向下一个统计值
                LOOP   OUTPUT_LOOP

    ;----- 程序终止 -----
                MOV    AH, 4CH
                INT    21H
CODES ENDS
END START