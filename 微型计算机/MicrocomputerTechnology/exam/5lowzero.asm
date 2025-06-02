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
          MOV    AX,0               ; 将AX寄存器清零，用于统计小于0的数据个数
          LEA    BX,BUF             ; 将BUF的有效地址传送到BX寄存器
          MOV    CX,N               ; 将BUF数组元素个数传送到CX寄存器，作为循环计数器
    CYCLE:
          CMP    BYTE PTR [BX],0    ; 比较BX所指向的内存单元（字节类型）中的值与0的大小
          JGE    NEXT               ; 如果大于等于0，跳转到NEXT标号处
          INC    AX                 ; 如果小于0，将AX寄存器的值加1，统计小于0的数据个数
    NEXT: 
          INC    BX                 ; 将BX寄存器的值加1，指向下一个BUF数组元素
          LOOP   CYCLE              ; 将CX寄存器的值减1，若不为0则跳转到CYCLE标号处继续循环
          MOV    RESULT,AX          ; 将统计得到的小于0的数据个数传送到RESULT变量中
          MOV    AH,4CH             ; 将AH寄存器设置为4CH，程序正常结束的功能号
          INT    21H                ; 执行DOS系统功能调用，结束程序并返回操作系统
CODE ENDS                           ; 代码段定义结束
END START                 ; 汇编语言程序结束，告知汇编器从START标号处开始执行