ASSUME CS:CODESG, DS:DATA

;--------------- 数据段定义 ---------------
DATA SEGMENT
       BIN   DW 358                           ; 待转换的二进制数（示例值358 → 十进制"00358"）
       ASDEC DB 5 DUP('0'), '$'               ; 存储十进制ASCII结果（预填'0'，以$结尾）
       PWTAB DW 10000, 1000, 100, 10, 1       ; 十进制权值表（字类型）
DATA ENDS

;--------------- 代码段定义 ---------------
CODESG SEGMENT
       START: 
       ; 初始化数据段
              MOV  AX, DATA
              MOV  DS, AX

       ; 设置指针和计数器
              LEA  SI, PWTAB           ; SI指向权值表
              LEA  DI, ASDEC           ; DI指向结果缓冲区
              MOV  CX, 5               ; 循环5次（5位十进制）

       ; 主转换逻辑
              MOV  AX, [BIN]           ; 加载待转换的数字到AX
       LOP:   
              MOV  DX, 0               ; 清零DX（用于DIV字操作）
              DIV  WORD PTR [SI]       ; AX = DX:AX / [SI], DX = 余数
              ADD  AL, '0'             ; 将商转换为ASCII字符
              MOV  [DI], AL            ; 存储ASCII字符到结果缓冲区
              INC  DI                  ; 移动目标指针
              ADD  SI, 2               ; 权值表为字类型，每次+2字节
              MOV  AX, DX              ; 余数作为下一次的被除数
              LOOP LOP

       ; 显示结果字符串
              LEA  DX, ASDEC
              MOV  AH, 09H
              INT  21H

       ; 程序终止
              MOV  AX, 4C00H
              INT  21H
CODESG ENDS
END START