# 定义成员片段函数
def member(me, member_list=[]):
    if me in member_list:
        print("我是篮球社成员")
    else:
        print("我不是篮球社成员")

    if me not in member_list:
        print("我不是篮球社成员")
    else:
        print("我是篮球社成员")

# 从用户那里获取输入
me = input("请输入名字: ")
inputlist = input("请输入成员名单，用逗号分隔: ")

# 将输入的成员名单转换为列表
list = []
for i in inputlist.split(','):
    list.append(i.strip())

# 调用member函数
member(me, list)
