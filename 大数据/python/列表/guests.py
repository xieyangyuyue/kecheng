# 创建并初始化Guests列表
guests = []
while True:
    try:
        guest = input("请输入嘉宾名字，输入空行结束：")
        if guest == "":
            break
        guests.append(guest)
    except EOFError:
        break

# 请在此添加代码，对guests列表进行插入、删除等操作
###### Begin ######
# 删除guests列表末尾的元素，并保存到deleted_guest变量
deleted_guest = guests.pop()

# 将deleted_guest插入到索引位置为2的地方
# 注意：如果guests列表的长度小于3，这将引发IndexError
guests.insert(2, deleted_guest)

# 删除guests列表索引位置为1的元素
# 注意：如果guests列表为空或长度小于2，这将引发IndexError
del guests[1]

#######  End #######

# 打印输出step1的deleted_guest变量
print("step1的deleted_guest变量:", deleted_guest)

# 打印输出step3改变后的guests列表
print("step3改变后的guests列表:", guests)
