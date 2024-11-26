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

# 对menu_dict字典进行添加、查找、修改和删除操作
###### Begin ######

# 添加菜品'lamb'，价格为50
menu_dict['lamb'] = 50

# 打印出'fish'的价格，如果不存在则提示
fish_price = menu_dict.get('fish', '该菜品不存在')
print(f"菜品'fish'的价格为：{fish_price}")

# 如果'fish'存在，则修改其价格为100
if 'fish' in menu_dict:
    menu_dict['fish'] = 100

# 删除'menu_dict'中的'noodles'菜品
menu_dict.pop('noodles', None)

#######  End #######

# 打印更新后的菜单
print("更新后的菜单：")
for dish, cost in menu_dict.items():
    print(f"{dish}: {cost}")
