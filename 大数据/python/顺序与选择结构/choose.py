workYear = int(input())

# 如果workYear < 5的判断语句
if workYear < 5:
    print("工资涨幅为0")

# 如果workYear >= 5 and workYear < 10的判断语句
elif workYear >= 5 and workYear < 10:
    print("工资涨幅为5%")

# 如果workYear >= 10 and workYear < 15的判断语句
elif workYear >= 10 and workYear < 15:
    print("工资涨幅为10%")

# 当上述条件判断都为假时的判断语句
else:
    print("工资涨幅为15%")
