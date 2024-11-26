#coding=utf-8

# 创建并初始化`source_list`列表
source_list = []
while True:
    try:
        list_element = input()
        source_list.append(list_element)
        if list_element==' ':
            break
    except:
        break
    
# 请在此添加代码，对source_list列表进行排序等操作并打印输出排序后的列表
#********** Begin 
source_list.sort()
print(source_list)

#********** End **********#

