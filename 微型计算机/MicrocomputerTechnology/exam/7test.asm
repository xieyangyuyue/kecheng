DATA SEGMENT                            ; 定义数据段
    BUF    DB  -2,5,-3,6,10,0,-20,-9    ; 在数据段中定义字节类型变量BUF并初始化数据
    N      EQU $-BUF                    ; 计算BUF数组的长度
    RESULT DW  ?                        ; 在数据段中定义字类型变量RESULT，用于存储结果，未初始化
DATA ENDS                               ; 数据段定义结束
CODE SEGMENT                        ; 定义代码段
          ASSUME CS:CODE,DS:DATA    ; 告知汇编器代码段寄存器CS对应CODE段，数据段寄存器DS对应DATA段
    START:
          MOV    AX,DATA            ; 将数据段的段地址传送到AX寄存器
          MOV    DS,AX              ; 将AX中的值传送到数据段寄存器DS，初始化数据段
          MOV    AL ,45H
          ADD    AL ,71H
          DAA
          MOV    BL,AL
          ADC    AL ,19H
          DAA
          MOV    BH,AL
          MOV    AH,4CH             ; 将AH寄存器设置为4CH，程序正常结束的功能号
          INT    21H                ; 执行DOS系统功能调用，结束程序并返回操作系统
CODE ENDS                           ; 代码段定义结束
END START                 ; 汇编语言程序结束，告知汇编器从START标号处开始执行