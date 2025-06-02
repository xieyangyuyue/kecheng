DATA SEGMENT
    BUF3   DB 5, -3, 2, 7, 0, -1, 4, 9, -5, 1
    OUTPUT DB 30 DUP ('$')                       ; 输出缓冲区
    SPACE  DB ' $'                               ; 空格分隔符
DATA ENDS

CODE SEGMENT
                 ASSUME CS:CODE, DS:DATA
    START:       
                 MOV    AX, DATA
                 MOV    DS, AX

    ; --- 冒泡排序 ---
                 MOV    CX, 9                 ; 外层循环次数
    OUTER_LOOP:  
                 MOV    SI, OFFSET BUF3       ; SI指向数组首地址
                 MOV    DX, CX                ; 内层循环次数
    INNER_LOOP:  
                 MOV    AL, [SI]
                 CMP    AL, [SI+1]
                 JLE    NO_SWAP
                 XCHG   AL, [SI+1]
                 MOV    [SI], AL
    NO_SWAP:     
                 INC    SI
                 DEC    DX
                 JNZ    INNER_LOOP
                 LOOP   OUTER_LOOP

    ; --- 输出排序结果 ---
                 MOV    SI, OFFSET BUF3       ; SI遍历排序后的数组
                 MOV    DI, OFFSET OUTPUT     ; DI指向输出缓冲区
                 MOV    CX, 10                ; 循环10次（10个元素）
    PRINT_LOOP:  
                 MOV    AL, [SI]              ; 读取当前元素
    ; 处理符号
                 TEST   AL, 80H               ; 检查最高位（符号位）
                 JZ     POSITIVE              ; 非负数，跳过负号处理
    ; 输出负号
                 MOV    BYTE PTR [DI], '-'    ; 在缓冲区写入负号
                 INC    DI
                 NEG    AL                    ; 将负数转换为正数处理
    POSITIVE:    
    ; 转换为十进制ASCII字符
                 MOV    AH, 0                 ; 清除AH，准备除法
                 MOV    BL, 10
                 DIV    BL                    ; AL = 商（十位），AH = 余数（个位）
                 ADD    AL, 30H               ; 十位转ASCII
                 ADD    AH, 30H               ; 个位转ASCII
    ; 写入缓冲区
                 CMP    AL, '0'               ; 检查十位是否为0
                 JE     SINGLE_DIGIT          ; 若十位为0，不显示
                 MOV    [DI], AL              ; 写入十位字符
                 INC    DI
    SINGLE_DIGIT:
                 MOV    [DI], AH              ; 写入个位字符
                 INC    DI
    ; 写入空格
    ;  MOV    DX, OFFSET SPACE
                 MOV    AL, ' '
                 MOV    [DI], AL
                 INC    DI
    ; 下一个元素
                 INC    SI
                 LOOP   PRINT_LOOP

    ; 输出最终字符串
                 MOV    DX, OFFSET OUTPUT
                 MOV    AH, 09H
                 INT    21H

    ; 程序终止
                 MOV    AH, 4CH
                 INT    21H
CODE ENDS
END START