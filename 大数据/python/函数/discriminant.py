# coding:utf-8
from math import sqrt

a = float(input()); b = float(input()); c = float(input())

def roots(a, b, c):
    # 使用二次方程的求根公式来求解方程的两个根
    discriminant = b**2 - 4*a*c  # 计算判别式
    if discriminant < 0:
        # 如果判别式小于0，则方程没有实数根
        return "无实数根"
    else:
        # 如果判别式大于等于0，则计算两个根
        root1 = (-b + sqrt(discriminant)) / (2 * a)
        root2 = (-b - sqrt(discriminant)) / (2 * a)
        return (root1, root2)  # 返回两个根

if a != 0:
    print(roots(a, b, c))
