#coding=utf-8

# 创建并读入range函数的相应参数
lower = int(input())
upper = int(input())
step = int(input())

# 请在此添加代码，实现编程要求
###### Begin ######
data_list=list(range(lower,upper,step))
print(len(data_list))
div=max(data_list)-min(data_list)
print(div)
####### End #######
