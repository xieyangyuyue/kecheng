# absencenum = int(input())
# studentname = []
# inputlist = input()
# for i in inputlist.split(','):
#     result = i
#     studentname.append(result)
# count = 0
# 
# # 填入循环遍历studentname列表的代码
# for student in studentname:
#     count += 1
#     if count == absencenum:
#         # 填入continue语句
#         continue
#     print(student, "的试卷已阅")


absencenum = int(input())
studentname=[]
inputlist=input()
count=0
for student in inputlist.split(','):
    studentname.append(student)

for student in studentname:
    count+=1
    if count==absencenum:
        continue
    print(student, "的试卷已阅")