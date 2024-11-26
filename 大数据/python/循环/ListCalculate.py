List = []
member = input()
for i in member.split(','):
    result = i
    List.append(result)

# 转换List为迭代器
IterList = iter(List)

# 用next()函数遍历IterList的代码
while True:
    try:
        num = next(IterList)
        result = int(num) * 2
        print(result)
    except StopIteration:
        break
