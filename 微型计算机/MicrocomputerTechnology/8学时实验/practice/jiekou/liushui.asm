OUT373 EQU 0E0H
IN245  EQU 0E4H

CODE SEGMENT
               ASSUME CS:CODE
    START:     
               IN     AL, IN245     ; 读取开关状态
               NOT    AL            ; 取反，使ON状态对应1
               TEST   AL, 01H       ; 检测第一个开关
               JNZ    M1            ; 若开启，跳转M1处理依次发光
               TEST   AL, 02H       ; 检测第二个开关
               JNZ    M2            ; 若开启，跳转M2处理闪烁
    ; 无开关开启，关闭LED并重新检测
               MOV    AL, 00H
               OUT    OUT373, AL
               JMP    START

    M1:                             ; 第一个开关ON，依次发光
               MOV    AL, 01H       ; 初始化第一个LED亮
    M1_LOOP:   
               OUT    OUT373, AL    ; 输出到LED
               CALL   DELAY         ; 延时
    ; 检查开关是否仍开启
               PUSH   AX
               IN     AL, IN245
               NOT    AL
               TEST   AL, 01H
               POP    AX
               JZ     START         ; 若关闭，返回主循环
               ROL    AL, 1         ; 左移一位，点亮下一个LED
               JMP    M1_LOOP       ; 继续循环

    M2:                             ; 第二个开关ON，闪烁
               MOV    AL, 0FFH      ; 初始化所有LED亮
    M2_LOOP:   
               OUT    OUT373, AL    ; 输出到LED
               CALL   DELAY         ; 延时
               XOR    AL, 0FFH      ; 切换LED状态（亮/灭）
    ; 检查开关是否仍开启
               PUSH   AX
               IN     AL, IN245
               NOT    AL
               TEST   AL, 02H
               POP    AX
               JZ     START         ; 若关闭，返回主循环
               JMP    M2_LOOP       ; 继续循环

DELAY PROC                          ; 延时子程序
               PUSH   CX
               MOV    CX, 0FFFFH
    DELAY_LOOP:
               NOP
               LOOP   DELAY_LOOP
               POP    CX
               RET
DELAY ENDP
CODE ENDS
END START