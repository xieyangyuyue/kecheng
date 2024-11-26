# 创建并初始化menu_dict字典
menu_dict = {}
while True:
    try:
        food = input("请输入菜品名称（输入'退出'结束）：")
        if food.lower() == '退出':
            break
        price = input("请输入菜品价格：")
        menu_dict[food] = int(price)
    except ValueError:
        print("价格输入无效，请输入数字。")
        continue

#请在此添加代码，实现对menu_dict的遍历操作并打印输出键与值
###### Begin ######
for key in menu_dict.keys():
    print(key)
for value in menu_dict.values():
    print(value)


#######  End #######



