DATA SEGMENT
    BUF2 DB  23, 15, 7, 45, 3, 99, 1, 18, 4, 2
    N    EQU $-BUF2                               ;BUF2个数
    MIN  DB  ?                                    ; 存储最小值的变量
DATA ENDS

CODE SEGMENT
                ASSUME CS:CODE, DS:DATA
    START:      
                MOV    AX, DATA
                MOV    DS, AX

                LEA    SI, BUF2
                MOV    AL, [SI]
                MOV    CX, N-1

    FIND_MIN:   
                INC    SI
                CMP    [SI], AL            ; 比较当前元素与AL
                JAE    SKIP_UPDATE         ; 若当前元素≥AL，跳过更新
                MOV    AL, [SI]            ; 否则更新AL为更小值
    SKIP_UPDATE:
                LOOP   FIND_MIN            ; 循环直到CX=0
                MOV    [MIN], AL           ; 将最小值存入MIN

    ; 输出最小值
                MOV    AH, 02H
                MOV    DL, [MIN]
                ADD    DL, 30H             ; 转换为ASCII字符
                INT    21H

                MOV    AH, 4CH
                INT    21H
CODE ENDS
END START