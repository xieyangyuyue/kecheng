DATA SEGMENT
       A1   DB  'AbcdaEFgh'       ; 原始字符串
       N    EQU $-A1              ; 计算字符串长度N
       A2   DB  N DUP('$')        ; 目标缓冲区初始化为$
DATA ENDS

CODESG SEGMENT
              ASSUME CS:CODESG, DS:DATA       ; 段寄存器声明
       START: 
       ; 初始化数据段
              MOV    AX, DATA
              MOV    DS, AX

       ; 筛选大写字母到A2
              LEA    SI, A1                   ; SI指向源字符串
              LEA    DI, A2                   ; DI指向目标缓冲区
              MOV    CX, N                    ; 设置循环次数为N
       LOP:   
              MOV    AL, [SI]                 ; 读取字符
              INC    SI                       ; 移动源指针
              CMP    AL, 'A'                  ; 检查是否小于'A'
              JB     NEXT                     ; 若低于'A'则跳过
              CMP    AL, 'Z'                  ; 检查是否大于'Z'
              JA     NEXT                     ; 若高于'Z'则跳过
              MOV    [DI], AL                 ; 存储合法大写字母
              INC    DI                       ; 移动目标指针
       NEXT:  
              LOOP   LOP                      ; 循环处理

       ; 显示处理后的字符串
              LEA    DX, A2                   ; DX指向目标缓冲区
              MOV    AH, 09H                  ; DOS功能号09H（显示字符串）
              INT    21H

       ; 程序终止
              MOV    AX, 4C00H
              INT    21H
CODESG ENDS
END START