DATAS SEGMENT
       A1    DB  'AbcdaEFgh'
       N     EQU $-A1
       A2    DB  N DUP('$')
DATAS ENDS

CODESG SEGMENT
              ASSUME CS:CODESG, DS:DATAS
       START: 
              MOV    AX ,DATAS
              MOV    DS ,AX

              LEA    SI ,A1
              LEA    DI,A2
              MOV    CX,N
       LOP:   
              MOV    AL,[SI]
              INC    SI
              CMP    AL,'A'
              JB     NEXT
              CMP    AL ,'Z'
              JA     NEXT
              MOV    [DI],AL
              INC    DI
       NEXT:  
              LOOP   LOP
        
              LEA    DX,A2
              MOV    AH,09H
              INT    21H
              MOV    AH ,4CH
              INT    21H

CODESG ENDS
END START
