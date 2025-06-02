OUT373 EQU 0FF80H  ; 输出端口地址
IN245  EQU 0FF80H  ; 输入端口地址 


CODE SEGMENT
               ASSUME CS:CODE


    START:     
    ; 读取开关状态
               MOV    DX,IN245      ; 加载输入端口地址
               IN     AL,DX         ; AL = 开关状态值 (8位二进制)

    ; 分支判断逻辑
               CMP    AL,01H        ; 检测是否仅最高位开关ON (二进制 00000001)
               JZ     M1            ; 是 → 跳转全亮模式

               CMP    AL,80H        ; 检测是否仅最低位开关ON (二进制 10000000)
               JZ     M2            ; 是 → 跳转中间4灯亮模式

               CMP    AL,0FFH       ; 检测是否所有开关ON (二进制 11111111)
               JZ     M3            ; 是 → 跳转闪烁模式

               JMP    M4            ; 其他情况 → 跳转全灭模式

    ;===================== LED模式处理 ==========================
    ; 模式1：所有LED全亮 (00000001 → 0FFH)
    M1:        
               MOV    AL,0FFH       ; 设置全亮模式码
               MOV    DX,OUT373     ; 加载输出端口
               OUT    DX,AL         ; 输出到LED
               JMP    MainLoop      ; 返回主循环检测

    ; 模式2：中间4个LED亮 (10000000 → 3CH=00111100)
    M2:        
               MOV    AL,3CH        ; 中间4位高电平
               MOV    DX,OUT373
               OUT    DX,AL
               JMP    MainLoop

    ; 模式3：所有LED闪烁 (11111111 → 交替输出全亮/全灭)
    M3:        
               MOV    AL,01H        ; 初始状态：最低位LED亮
    N1:        
               MOV    DX,OUT373     ; 输出当前LED状态
               OUT    DX,AL
               CALL   DELAY         ; 延时

    ; 检查输入是否仍为0FFH
               PUSH   AX            ; 保存AL（LED状态）
               MOV    DX,IN245
               IN     AL,DX
               CMP    AL,0FFH       ; 判断开关是否仍全开
               POP    AX            ; 恢复AL（LED状态）
               JNZ    ExitM3        ; 若输入改变则退出

               ROL    AL,1          ; 保持流水灯效果（左移一位）
               JMP    N1            ; 继续循环

    ExitM3:    JMP    MainLoop      ; 返回主循环
              
    ; 模式4：所有LED熄灭
    M4:        
               MOV    AL,00H
               MOV    DX,OUT373
               OUT    DX,AL
               JMP    MainLoop

    ;===================== 循环控制 ============================
    MainLoop:                       ; 统一返回点
               JMP    START         ; 持续检测开关状态

    ;===================== 延时子程序 ===========================
DELAY PROC                          ; 软件延时约500ms
               PUSH   CX
               PUSH   AX
               MOV    CX,0FFFFH     ; 增加循环次数
    Delay_Loop:
               NOP                  ; 空操作消耗时钟周期
               LOOP   Delay_Loop
               POP    AX
               POP    CX
               RET
DELAY ENDP

CODE ENDS
END START