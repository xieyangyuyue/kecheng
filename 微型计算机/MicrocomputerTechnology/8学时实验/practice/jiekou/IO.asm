IO0   EQU  11100000B      
CODE SEGMENT
          ASSUME CS:CODE

    START:
          OUT    IO0,AL     ; 输出操作：将AL寄存器的值发送到IO0端口
    ; 注意：AL在此前未被初始化，输出值不确定！
                                 
          JMP    $          ; 无限循环：'$'表示当前地址，等同于标号后的地址
    ; 作用：防止程序继续执行未知内存区域
                                 
CODE ENDS
END START          