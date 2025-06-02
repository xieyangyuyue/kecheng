DATAS SEGMENT
    HEADER     DB '3-Digit Narcissistic Numbers:',0DH,0AH,'$'
    CUBE_TABLE DW 0, 1, 8, 27, 64, 125, 216, 343, 512, 729       ; 16位立方表
    BUFFER     DB 6 DUP('$')
DATAS ENDS

STACKS SEGMENT
           DB 100H DUP(?)
STACKS ENDS

CODES SEGMENT
                     ASSUME CS:CODES,DS:DATAS,SS:STACKS

    START:           
                     MOV    AX, DATAS
                     MOV    DS, AX
    ; 显示标题
                     MOV    AH,09H
                     LEA    DX, HEADER
                     INT    21H

    ; 处理3位数：100-999，n=3
                     MOV    CX, 120
    THREE_DIGIT_LOOP:
                     CMP    CX, 130
                     JA     EXIT_PROGRAM
                     PUSH   CX
                     MOV    AX, CX
                     MOV    SI, 0                          ; SI = 总和
    CALCULATE_THREE: 
                     CMP    AX, 0
                     JE     CHECK_THREE
                     MOV    DX, 0
                     MOV    DI, 10
                     DIV    DI                             ; AX=商，DX=余数（0-9）
                     MOV    BX, DX                         ; 余数存入BX
                     SHL    BX, 1                          ; 索引×2（16位表每个元素占2字节）
                     MOV    AX, CUBE_TABLE[BX]             ; 读取立方值（16位）
                     ADD    SI, AX                         ; 累加到总和
                     JMP    CALCULATE_THREE
    CHECK_THREE:     
                     POP    CX
                     CMP    SI, CX
                     JNE    SKIP_THREE
                     CALL   PRINT_NUMBER
    SKIP_THREE:      
                     INC    CX
                     JMP    THREE_DIGIT_LOOP

    EXIT_PROGRAM:    
                     MOV    AH,4CH
                     INT    21H

    ;----- 子程序：数字转字符串并输出 -----
PRINT_NUMBER PROC
                     PUSH   AX
                     PUSH   BX
                     PUSH   CX
                     PUSH   DX
                     PUSH   SI
                     LEA    SI, BUFFER+5
                     MOV    BYTE PTR [SI], '$'
                     MOV    AX, CX
    CONVERT_LOOP:    
                     DEC    SI
                     MOV    DX, 0
                     MOV    BX, 10
                     DIV    BX
                     ADD    DL, '0'
                     MOV    [SI], DL
                     CMP    AX, 0
                     JNE    CONVERT_LOOP
                     MOV    DX, SI
                     MOV    AH, 09H
                     INT    21H
    ; 输出空格分隔符
                     MOV    DL, ' '
                     MOV    AH, 02H
                     INT    21H
                     POP    SI
                     POP    DX
                     POP    CX
                     POP    BX
                     POP    AX
                     RET
PRINT_NUMBER ENDP

CODES ENDS
END START