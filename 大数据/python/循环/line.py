partcount = int(input())
electric = int(input())
count = 0

# 当count < partcount时的while循环判断语句
while count < partcount:
    count += 1
    print("已加工零件个数:", count)
    if electric:
        print("停电了，停止加工")
        # 填入break语句
        break
