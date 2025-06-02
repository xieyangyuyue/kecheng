;=============================================
; 程序功能：在字节数组中查找最大值并存储到数组首字节
; 数组结构：BUF[0]预留存储位置，查找范围为BUF[1]~BUF[5]
; 最终结果：最大值存入BUF[0]
;=============================================

;---------- 数据段定义 ----------
DATAS SEGMENT
    BUF   DB ?, 12H, 34H, 26H, 60H, 22H    ; 定义字节数组：
    ; BUF[0] = ? (预留位置)
    ; BUF[1] = 12H, BUF[2] = 34H,... BUF[5] = 22H
    N     =  $ - BUF                       ; 计算数组总长度：
    ; $表示当前地址，BUF起始地址到此处共6字节
DATAS ENDS


;---------- 代码段定义 ----------
CODES SEGMENT
          ASSUME CS:CODES, DS:DATAS    ; 告诉汇编器段寄存器与段的对应关系

    START:
    ; 初始化数据段寄存器
          MOV    AX, DATAS             ; 将数据段地址存入AX
          MOV    DS, AX                ; 将AX值赋给DS，使DS指向数据段

    ; 准备遍历数组
          LEA    SI, BUF               ; SI = 数组首地址（指向BUF[0]）
          INC    SI                    ; SI++ → 指向BUF[1]（第一个有效元素）
          MOV    CX, N-1               ; CX = 数组总长度-1 → 6-1=5（原始循环次数）
          DEC    CX                    ; CX-- → 调整为4（实际需要比较后续4个元素）
          MOV    AL, [SI]              ; AL = BUF[1]的值（12H），作为初始最大值

    ; 循环查找最大值（共执行4次）
    LOOP1:
          INC    SI                    ; 指针SI移向下一个元素
          CMP    AL, [SI]              ; 比较当前最大值AL与[SI]指向的元素
          JA     NEXT                  ; 若AL > [SI]（无符号数比较），跳过更新
          MOV    AL, [SI]              ; 否则更新最大值为当前元素值
    NEXT: 
          LOOP   LOOP1                 ; CX--，若CX≠0则继续循环

    ; 存储结果并退出程序
          MOV    BUF, AL               ; 将最终最大值AL存入BUF[0]
          MOV    AH, 4CH               ; DOS功能号4CH：程序终止
          INT    21H                   ; 调用DOS中断，结束程序

CODES ENDS
END START          ; 程序结束，入口点为START