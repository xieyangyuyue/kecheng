# coding=utf-8

# 创建并初始化my_menu列表
my_menu = []
while True:
    try:
        food = input()
        if food==' ':
            break
        my_menu.append(food)
       
    except:
        break

# 请在此添加代码，对my_menu列表进行切片操作
###### Begin ######
print(my_menu[::3])
print(my_menu[-3:])


#######  End #######

