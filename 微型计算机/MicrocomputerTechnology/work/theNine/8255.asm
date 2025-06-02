IOCON   EQU   02D6H        ; 8255控制口
IOA     EQU   02D0H        ; 端口A地址（LED低8位）
IOB     EQU   02D2H        ; 端口B地址（LED高8位）
IOC     EQU   02D4H        ; 端口C地址（开关状态输入）

CODE SEGMENT
                  ASSUME CS:CODE
      START:      
                  MOV    AL,10001001B      ; 控制字：A/B口输出，C口输入，模式0
                  MOV    DX,IOCON
                  OUT    DX,AL             ; 写入8255控制寄存器
                  MOV    BX,0001H          ; 初始LED状态（第1个灯亮）

      MAIN_LOOP:  
                  MOV    AL,BL             ; 低8位送端口A
                  MOV    DX,IOA
                  OUT    DX,AL
                  MOV    AL,BH             ; 高8位送端口B
                  MOV    DX,IOB
                  OUT    DX,AL

      ; 检测开关状态（C口bit1）
                  MOV    DX,IOC
                  IN     AL,DX
                  TEST   AL,00000010B      ; 测试开关是否闭合（bit1=0）
                  JNZ    SWITCH_OPEN       ; 开关未闭合跳转

      ; 开关闭合时执行移位
                  ROL    BX,1              ; 实现从上到下流动
                  CALL   DELAY             ; 保持当前显示时间
                  JMP    MAIN_LOOP

      SWITCH_OPEN:
      ; 开关打开时保持状态
                  CALL   DELAY             ; 仍需延时防止抖动
                  JMP    MAIN_LOOP

      ;--------------- 延时子程序 ---------------
DELAY PROC
                  PUSH   CX
                  MOV    CX,0FFFFH
      DLP:        
                  NOP
                  LOOP   DLP
                  POP    CX
                  RET
DELAY ENDP

CODE ENDS
END START