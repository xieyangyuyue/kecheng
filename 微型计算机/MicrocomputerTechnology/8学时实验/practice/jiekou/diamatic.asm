IOA		EQU 	0FFD0H		; 端口A地址
IOB		EQU 	0FFD2H		; 端口B地址
IOC		EQU 	0FFD4H		; 端口C地址
IOCON	EQU 	0FFD6H		; 控制寄存器地址

DATA SEGMENT
    TABLE DB 0C0H,0F9H,0A4H,0B0H,99H,92H,82H,0F8H,80H,90H    ; 共阳极数码管编码表
DATA ENDS

CODE SEGMENT
               ASSUME CS:CODE, DS:DATA
    START:     
               MOV    AX, DATA
               MOV    DS, AX
               LEA    SI, TABLE

    ; 配置8255A：A/B口输出，C口下半部输入
               MOV    AL, 10000001B            ; 控制字：A口输出，B口输出，C口下半输入(PC0-PC3)
               MOV    DX, IOCON
               OUT    DX, AL

    MAIN_LOOP: 
    ; 读取C口状态（检测PC0开关）
               MOV    DX, IOC
               IN     AL, DX
               TEST   AL, 00000001B            ; 测试PC0位是否为1（开关闭合）
               JNZ    NO_UPDATE                ; 若为0则跳转，保持当前显示

    ; 更新数码管编码索引
               INC    SI
               CMP    SI, OFFSET TABLE + 10    ; 检查是否超出表范围
               JB     NO_UPDATE                ; 若未超出则继续
               LEA    SI, TABLE                ; 超出则重置到表首

    NO_UPDATE: 
    ; 输出当前编码到数码管
               MOV    AL, [SI]                 ; 获取当前数码管编码
               MOV    DX, IOA
               OUT    DX, AL

               CALL   DELAY                    ; 延时控制循环速度
               JMP    MAIN_LOOP                ; 持续循环检测

    ; 延时子程序（约0.1秒）
DELAY PROC
               PUSH   CX
               MOV    CX, 0FFFFH
    DELAY_LOOP:
               PUSH   CX
               MOV    CX, 08H
    INNER_LOOP:
               LOOP   INNER_LOOP
               POP    CX
               LOOP   DELAY_LOOP
               POP    CX
               RET
DELAY ENDP
CODE ENDS
END 	START