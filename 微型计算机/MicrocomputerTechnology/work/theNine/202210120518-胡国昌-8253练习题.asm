OUT373  EQU   0FF80H   ; 8255输出端口地址
IN245   EQU   0FF90H   ; 8255输入端口地址
TCON0   EQU   0FFA0H   ; 8253定时器0数据端口
TCONTR  EQU   0FFA6H   ; 8253控制寄存器端口

CODE SEGMENT
            ASSUME CS:CODE

      START:
            MOV    BL, 01H            ; 初始化BL寄存器

            CALL   INIT               ; 初始化8253定时器

      CYCLE:
            MOV    DX, IN245
    
            TEST   AL, 00001000B      ; 检测第3位
            JZ     NEXT               ; 如果bit3=0跳转到NEXT
    
    
            ROL    BL, 1
            CALL   INIT

      NEXT: 
            MOV    AL, BL             ; 准备输出当前LED模式
            MOV    DX, OUT373         ; 设置8255输出端口地址
            OUT    DX, AL             ; 将BL值输出到8255控制LED
    
            JMP    CYCLE

      ; 定时器初始化子程序
INIT PROC
   
            MOV    AL, 30H
            MOV    DX, TCONTR         ; 8253控制寄存器地址
            OUT    DX, AL
    
      ; 设置定时器0的计数初值
            MOV    DX, TCON0          ; 定时器0数据端口
            MOV    AX, 1000
            OUT    DX, AL
            MOV    AL, AH
            OUT    DX, AL
            RET
INIT ENDP

CODE ENDS
END START             