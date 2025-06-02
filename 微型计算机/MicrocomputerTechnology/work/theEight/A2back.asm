OUT373 EQU 0FF80H
IN245 EQU 0FF80H
CODE SEGMENT
	           ASSUME CS:CODE
	START:     
	           MOV    DX,IN245
	           IN     AL,DX     	;读取开关状态
	;当只有最上面的开关为ON，8个LED灯全亮；00000001
	           CMP    AL,01H
	           JZ     M1
	;当只有最下面的开关为ON，中间4个LED灯亮；10000000
	           CMP    AL,80H
	           JZ     M2
	;当开关全部为ON，8个LED灯闪烁发光；11111111
	           CMP    AL,0FFH
	           JZ     M6
	;当开关为其余状态时，所有的LED灯全灭。
	           JMP    M4
	
	M1:        MOV    AL,0FFH
	           MOV    DX,OUT373
	           OUT    DX,AL
	           JMP    M5
	
	M2:        MOV    AL,3CH
	           MOV    DX,OUT373
	           OUT    DX,AL
	           JMP    M5
	
	; M3:                         	;同时熄灭或者同时亮起
	;            MOV    AL,0FFH
	;            MOV    DX,OUT373
	;            OUT    DX,AL
	;            CALL   DELAY
	;            MOV    AL,00H
	;            MOV    DX,OUT373
	;            OUT    DX,AL
	;            CALL   DELAY
	;            JMP    M5
	
	M4:        MOV    AL,00H
	           MOV    DX,OUT373
	           OUT    DX,AL
	           JMP    M5
	
	M6:                         	;循环闪烁
	           MOV    AL,01H
	N1:        MOV    DX,OUT373
	           OUT    DX,AL
	           CALL   DELAY
	           ROL    AL,1
	           JMP    N1

	
	
DELAY PROC                  		;延迟子程序
	           PUSH   AX
	           PUSH   CX
	           MOV    CX,0FFFFH
	DELAY_LOOP:
	           NOP
	           LOOP   DELAY_LOOP
	           POP    CX
	           POP    AX
	           RET
DELAY ENDP
	
	M5:        JMP    START

CODE ENDS
	END START	

