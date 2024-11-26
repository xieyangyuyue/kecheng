#coding=utf-8

# 导入math模块
import math

# 输入两个整数a和b
a = int(input())
b = int(input())

# 请在此添加代码，要求判断是否存在两个整数，它们的和为a，积为b
#********** Begin *********#

def find_integers(a, b):
    # 初始化结果列表
    result = []
    # 遍历所有可能的整数对
    for x in range(a + 1):
        y = a - x
        if x * y == b:
            result.append((x, y))
    return result

# 调用函数并打印结果
integers = find_integers(a, b)
if integers:
    print(f"存在整数对：{integers[0]}，它们的和为{a}，积为{b}")
else:
    print(f"不存在整数对，其和为{a}且积为{b}")

#********** End **********#
