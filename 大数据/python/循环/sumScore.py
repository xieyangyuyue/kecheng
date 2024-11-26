studentnum = int(input())

# 填入for循环遍历学生人数的代码
for student in range(1, studentnum + 1):  # 从1开始计数，因为学生编号通常从1开始
    sum = 0
    subjectscore = []
    inputlist = input()
    for i in inputlist.split(','):
        result = i
        subjectscore.append(result)

    # 填入for循环遍历学生分数的代码
    for score in subjectscore:
        score = int(score)
        sum = sum + score
    print("第%d位同学的总分为:%d" % (student, sum))
