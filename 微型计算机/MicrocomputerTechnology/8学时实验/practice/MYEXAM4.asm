DATAS SEGMENT
       BUF   DB 'HOW ARE YOU!$'
DATAS ENDS

CODES SEGMENT
             ASSUME CS:CODES, DS:DATAS
       START:
             MOV    AX,DATAS
             MOV    DS,AX
             MOV    DX,OFFSET BUF
             MOV    AH,09H
             INT    21H

             MOV    AH ,4CH
             INT    21H
CODES ENDS
END START
