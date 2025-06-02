DATA SEGMENT                         ; 定义数据段
    BUF1 DB ?,12H,34H,26H,60H,22H    ; 定义字节类型变量BUF1，第一个字节未初始化，后续字节初始化
N1=$-BUF1                            ; 计算BUF1数组长度
    BUF2 DB ?,16H,68H,47H,55H        ; 定义字节类型变量BUF2，第一个字节未初始化，后续字节初始化
N2=$-BUF2                            ; 计算BUF2数组长度
DATA ENDS                            ; 数据段定义结束
CODE SEGMENT                         ; 定义代码段
           ASSUME CS:CODE,DS:DATA    ; 告知汇编器代码段寄存器CS对应CODE段，数据段寄存器DS对应DATA段
    START: 
           MOV    AX,DATA            ; 将数据段的段地址传送到AX寄存器
           MOV    DS,AX              ; 将AX中的值传送到数据段寄存器DS，初始化数据段
           LEA    SI,BUF1            ; 将BUF1的有效地址传送到SI寄存器
           INC    SI                 ; 使SI指向BUF1数组中第一个已初始化元素
           MOV    CX,N1 - 1          ; 将BUF1中已初始化元素个数传送到CX寄存器作为循环计数器
           CALL   SUBROT             ; 调用SUBROT子程序
           MOV    BUF1,AL            ; 将AL寄存器的值传送到BUF1变量的第一个字节
           LEA    SI,BUF2            ; 将BUF2的有效地址传送到SI寄存器
           INC    SI                 ; 使SI指向BUF2数组中第一个已初始化元素
           MOV    CX,N2              ; 将BUF2数组元素个数传送到CX寄存器
           DEC    CX                 ; 将CX寄存器的值减1
           CALL   SUBROT             ; 再次调用SUBROT子程序
           MOV    BUF2,AL            ; 将AL寄存器的值传送到BUF2变量的第一个字节
           MOV    AH,4CH             ; 将AH寄存器设置为4CH，程序正常结束的功能号
           INT    21H                ; 执行DOS系统功能调用，结束程序并返回操作系统
SUBROT PROC                          ; SUBROT子程序定义开始
           DEC    CX                 ; 将CX寄存器的值再减1
           MOV    AL,[SI]            ; 将SI所指向的内存单元的值传送到AL寄存器
    LOOP1: 
           INC    SI                 ; 将SI寄存器的值加1，指向下一个数组元素
           CMP    AL,[SI]            ; 比较AL寄存器的值与SI所指向内存单元的值
           JA     NEXT               ; 如果AL中的值大于SI所指向的值，跳转到NEXT标号处
           MOV    AL,[SI]            ; 若不满足大于条件，将SI所指向内存单元的值传送到AL寄存器
    NEXT:  
           LOOP   LOOP1              ; 将CX寄存器的值减1，若不为0则跳转到LOOP1标号处继续循环
           RET                       ; 从子程序返回主程序
SUBROT ENDP                          ; SUBROT子程序定义结束
CODE ENDS                            ; 代码段定义结束
END START                 ; 汇编语言程序结束，告知汇编器从START标号处开始执行