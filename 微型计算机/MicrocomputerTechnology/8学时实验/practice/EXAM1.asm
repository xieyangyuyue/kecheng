DATA SEGMENT
       BUF  DB 'HOW ARE YOU!$'       ;
DATA ENDS

CODESG SEGMENT
              ASSUME CS:CODESG, DS:DATA       ; 段寄存器关联声明
       START: 
       ; 初始化数据段
              MOV    AX, DATA                 ; 将数据段地址装入AX
              MOV    DS, AX                   ; DS指向数据段
    
       ; 调用DOS 21H中断显示字符串
              MOV    DX, OFFSET BUF           ; 字符串偏移地址送DX
              MOV    AH, 09H                  ; 功能号09H（显示字符串）
              INT    21H                      ; DOS中断调用
    
       ; 程序终止
              MOV    AX, 4C00H                ; 功能号4CH（程序终止）
              INT    21H                      ; DOS中断调用
CODESG ENDS
END START