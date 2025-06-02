DATA SEGMENT
    BUF1       DB  1, 2, 3, 0, 5, 0, 7, 8, 9
    N          EQU $-BUF1                       ;BUF1个数
    EVEN_COUNT DB  0                            ; 偶数计数器
    ODD_COUNT  DB  0                            ; 奇数计数器
    ZERO_COUNT DB  0                            ; 零计数器
    SPACE      DB  ' $'                         ; 空格分隔符
DATA ENDS

CODE SEGMENT
               ASSUME CS:CODE, DS:DATA
    START:     
               MOV    AX, DATA
               MOV    DS, AX

               XOR    BX, BX              ; BH存奇数计数，BL存偶数计数
               XOR    DX, DX              ; DL存零计数

               LEA    SI, BUF1
               MOV    CX, N

    LOOP_START:
               MOV    AL, [SI]            ; 读取当前元素到AL
               CMP    AL, 0
               JE     ZERO_LABEL          ; 元素为0，跳转至ZERO处理
               TEST   AL, 1
               JZ     EVEN_LABEL          ; 最低位为0，是偶数
               INC    BH                  ; 否则为奇数，BH++
               JMP    NEXT
    ZERO_LABEL:
               INC    DL                  ; 零计数DL++
               JMP    NEXT
    EVEN_LABEL:
               INC    BL                  ; 偶数计数BL++
    NEXT:      
               INC    SI                  ; 指针指向下一个元素
               LOOP   LOOP_START          ; 循环直到CX=0

    ; 将结果存入内存变量
               MOV    [EVEN_COUNT], BL
               MOV    [ODD_COUNT], BH
               MOV    [ZERO_COUNT], DL

    ; 输出结果
    ; 输出偶数个数
               MOV    AH, 02H
               MOV    DL, [EVEN_COUNT]
               ADD    DL, 30H             ; 转换为ASCII字符
               INT    21H
    ; 输出空格
               MOV    DX, OFFSET SPACE
               MOV    AH, 09H
               INT    21H
    ; 输出奇数个数
               MOV    AH, 02H
               MOV    DL, [ODD_COUNT]
               ADD    DL, 30H
               INT    21H
    ; 输出空格
               MOV    DX, OFFSET SPACE
               MOV    AH, 09H
               INT    21H
    ; 输出零的个数
               MOV    AH, 02H
               MOV    DL, [ZERO_COUNT]
               ADD    DL, 30H
               INT    21H
               
               MOV    AH, 4CH
               INT    21H
CODE ENDS
END START