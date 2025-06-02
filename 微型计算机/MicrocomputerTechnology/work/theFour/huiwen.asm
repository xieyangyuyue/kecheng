;=============================================
; 程序功能：判断输入的正整数是否为回文数
;=============================================
DATAS SEGMENT
    INPUT_MSG   DB 'Enter a positive integer: $'
    INVALID_MSG DB 0DH,0AH,'Invalid input!$'
    YES_MSG     DB 0DH,0AH,'YES$'
    NO_MSG      DB 0DH,0AH,'NO$'
    BUFFER      DB 21                               ; 最大输入长度20
                DB ?                                ; 实际输入长度
                DB 21 DUP('$')                      ; 输入存储区
DATAS ENDS

STACKS SEGMENT
           DB 100H DUP(?)    ; 256字节堆栈
STACKS ENDS

CODES SEGMENT
                   ASSUME CS:CODES,DS:DATAS,SS:STACKS

    START:         
                   MOV    AX, DATAS
                   MOV    DS, AX

    ;----- 显示输入提示 -----
                   LEA    DX, INPUT_MSG
                   MOV    AH, 09H
                   INT    21H

    ;----- 读取用户输入 -----
                   LEA    DX, BUFFER
                   MOV    AH, 0AH
                   INT    21H

    ;----- 输入有效性检查与指针初始化 -----
                   XOR    CX, CX
                   MOV    CL, BUFFER+1                   ; CL = 实际输入长度
                   CMP    CL, 0
                   JE     INVALID_INPUT                  ; 处理空输入
                   LEA    SI, BUFFER+2                   ; SI指向输入首字符

    ;----- 检查是否全为数字字符 -----
    CHECK_DIGITS:  
                   MOV    AL, [SI]
                   CMP    AL, '0'
                   JB     INVALID_INPUT
                   CMP    AL, '9'
                   JA     INVALID_INPUT
                   INC    SI
                   LOOP   CHECK_DIGITS

    ;----- 重新初始化指针 -----
                   MOV    CL, BUFFER+1                   ; 重新获取长度
                   LEA    SI, BUFFER+2                   ; SI指向字符串首
                   MOV    DI, SI                         ; DI = SI（正确复制地址）
                   ADD    DI, CX                         ; DI += 字符串长度
                   DEC    DI                             ; DI指向最后一个字符

    ;----- 回文检测循环 -----
                   SHR    CX, 1                          ; 循环次数 = 长度/2
                   JCXZ   PALINDROME                     ; 处理长度为1的特殊情况            ; 长度为1自动视为回文

    COMPARE_LOOP:  
                   MOV    AL, [SI]
                   CMP    AL, [DI]
                   JNE    NOT_PALINDROME
                   INC    SI
                   DEC    DI
                   LOOP   COMPARE_LOOP

    PALINDROME:                                          ; 是回文数
                   LEA    DX, YES_MSG
                   JMP    OUTPUT_RESULT

    NOT_PALINDROME:                                      ; 非回文数
                   LEA    DX, NO_MSG
                   JMP    OUTPUT_RESULT

    INVALID_INPUT:                                       ; 非法输入处理
                   LEA    DX, INVALID_MSG

    OUTPUT_RESULT:                                       ; 统一结果输出
                   MOV    AH, 09H
                   INT    21H

    EXIT:          
                   MOV    AH, 4CH
                   INT    21H
CODES ENDS
END START