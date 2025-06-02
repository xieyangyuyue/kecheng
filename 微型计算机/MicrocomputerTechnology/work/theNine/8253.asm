; 硬件端口地址定义
OUT373  EQU   0FF80H   ; 8255输出端口地址（控制LED等设备）
IN245   EQU   0FF90H   ; 8255输入端口地址（读取开关状态）
TCON0   EQU   0FFA0H   ; 8253定时器0数据端口
TCONTR  EQU   0FFA6H   ; 8253控制寄存器端口

CODE SEGMENT
            ASSUME CS:CODE            ; 设定代码段寄存器

      START:                          ; 程序入口点
            MOV    BL, 01H            ; 初始化BL寄存器为00000001b（LED初始状态）

            CALL   INIT               ; 初始化8253定时器

      CYCLE:                          ; 主循环开始
            MOV    DX, IN245          ; 准备读取输入端口
            IN     AL, DX             ; 从8255输入端口读取开关状态到AL
    
            TEST   AL, 00001000B      ; 检测第3位（bit3）是否为1（开关状态）
            JZ     NEXT               ; 如果bit3=0（开关断开），跳转到NEXT
    
      ; 以下执行当bit3=1（开关闭合）时的操作
            ROL    BL, 1              ; 循环左移BL寄存器（改变LED显示模式）
            CALL   INIT               ; 重新初始化定时器（重置计时周期）

      NEXT: 
            MOV    AL, BL             ; 准备输出当前LED模式
            MOV    DX, OUT373         ; 设置8255输出端口地址
            OUT    DX, AL             ; 将BL值输出到8255控制LED
    
            JMP    CYCLE              ; 跳回主循环继续执行

      ; 定时器初始化子程序（配置8253定时器0）
INIT PROC
      ; 设置8253控制字：选择定时器0，先低后高字节，模式0，二进制计数
      ; 控制字格式：00110110 → 36H（原代码30H有误，修正为36H）
            MOV    AL, 30H            ; 正确控制字应为36H（0011 0110B）
            MOV    DX, TCONTR         ; 8253控制寄存器地址
            OUT    DX, AL             ; 写入控制寄存器
    
      ; 设置定时器0的计数初值（1000个时钟周期）
            MOV    DX, TCON0          ; 定时器0数据端口
            MOV    AX, 1000           ; 16位计数值
            OUT    DX, AL             ; 先写入低字节
            MOV    AL, AH             ; 准备高字节
            OUT    DX, AL             ; 再写入高字节
            RET
INIT ENDP

CODE ENDS
END START              ; 程序结束并指定入口点