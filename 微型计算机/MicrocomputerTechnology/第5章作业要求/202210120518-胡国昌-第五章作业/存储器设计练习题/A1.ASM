CYCLES     EQU 2000H        ; 循环次数定义（8192次，覆盖2000H*2字节=16KB）
EVENADDRESS EQU 0000H       ; 偶地址偏移（A0=0，对应偶存储体）
ODDADDRESS  EQU 0001H       ; 奇地址偏移（A0=1，对应奇存储体）

CODE SEGMENT
	           ASSUME CS:CODE
	START:     
	; 段寄存器初始化
	           MOV    AX, 0F800H     	; 设置段基址（物理地址F8000H）
	           MOV    DS, AX         	; DS指向共享存储段
	           MOV    DS:[0], AX     	; 初始化共享段首字（测试用）

	; 寄存器参数配置
	           MOV    SI, EVENADDRESS	; 偶地址指针（必须保持A0=0）
	           MOV    DI, ODDADDRESS 	; 奇地址指针（必须保持A0=1）
	           MOV    CX, CYCLES     	; 循环计数器（2000H次）
	           MOV    AL, 11H        	; 写入模式字节（二进制00010001）

	;----------------------------------------------------------
	;   每次循环同时写入奇偶存储体，地址步进2字节保持A0位交替
	;----------------------------------------------------------
	WRITE_LOOP:
	           MOV    [SI], AL       	; 写入偶地址存储体（DS:SI）
	           MOV    [DI], AL       	; 写入奇地址存储体（DS:DI）
               
	; 地址更新（每次+2保持A0位交替）
	           ADD    SI, 2          	; 偶地址步进（0000H→0002H...）
	           ADD    DI, 2          	; 奇地址步进（0001H→0003H...）
               
	; 循环控制
	           DEC    CX
	           JNZ    WRITE_LOOP     	; 剩余次数≠0时继续循环

            
	           JMP    $              	;硬件复位前保持挂起状态
CODE ENDS
END START