#coding=utf-8

import math

# 输入正整数a和b
a = float(input())
b = float(input())

# 根据勾股定理计算直角三角形的斜边边长
def calculate_hypotenuse(a, b):
    return math.sqrt(a**2 + b**2)

# 调用函数计算斜边边长并打印结果
hypotenuse = calculate_hypotenuse(a, b)
print(f"{hypotenuse:.3f}")
