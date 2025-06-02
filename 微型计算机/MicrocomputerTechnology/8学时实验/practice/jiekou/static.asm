; 8255A芯片端口地址定义
IOA		EQU 	0FFD0H		; 端口A地址
IOB		EQU 	0FFD2H		; 端口B地址
IOC		EQU 	0FFD4H		; 端口C地址
IOCON	EQU 	0FFD6H		; 控制寄存器地址

DATA SEGMENT
    TABLE DB 0C0H,0F9H,0A4H,0B0H,99H,92H,82H,0F8H,80H,90H    ; 七段数码管编码表（共阳极）
DATA ENDS

CODE SEGMENT
               ASSUME CS:CODE, DS:DATA
    START:     
               MOV    AX, DATA
               MOV    DS, AX
               LEA    SI, TABLE                ; 加载TABLE地址到SI

               MOV    AL, 80H                  ; 8255A初始化（同上）
               MOV    DX, IOCON
               OUT    DX, AL

    L1:        
               MOV    AL, [SI]                 ; 正确读取当前数字编码
               MOV    DX, IOA
               OUT    DX, AL                   ; 输出到端口A

               INC    SI                       ; 指向下一个数字
               CMP    SI, OFFSET TABLE + 10    ; 是否到表末尾？
               JB     NEXT
               LEA    SI, TABLE                ; 重置到表头
    NEXT:      
               CALL   DELAY                    ; 添加延时函数
               JMP    L1                       ; 循环显示

DELAY PROC                                     ; 延时子程序
               PUSH   CX
               MOV    CX, 0FFFFH
    DELAY_LOOP:
               NOP
               LOOP   DELAY_LOOP
               POP    CX
               RET
DELAY ENDP
CODE ENDS
END 	START