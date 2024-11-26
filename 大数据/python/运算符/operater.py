

# 修正函数名拼写错误，并正确调用函数
def theOperation(apple, pear):
    # 请在此处填入计算苹果个数加梨的个数的代码，并将结果存入sum_result变量
    ###### Begin ######
    sum_result = apple + pear
    ####### End #######
    print(sum_result)

    # 请在此处填入苹果个数除以梨的个数的代码，并将结果存入div_result变量
    ###### Begin ######
    # 注意：这里需要确保pear不为0，否则会发生除以0的错误
    if pear != 0:
        div_result = apple / pear
    else:
        div_result = '无法除以0'
    ####### End #######
    print(div_result)
    
    # 请在此处填入苹果个数的2次幂的代码，并将结果存入exp_result变量
    ###### Begin ######
    exp_result = apple ** 2
    ####### End #######
    print(exp_result)
    
    # 请在此处填入判断苹果个数是否与梨的个数相等的代码，并将结果存入isequal变量
    ###### Begin ######
    isequal = apple == pear
    ####### End #######
    print(isequal)
    
    # 请在此处填入判断苹果个数是否大于等于梨的个数的代码，并将结果存入ismax变量
    ###### Begin ######
    ismax = apple >= pear
    ####### End #######
    print(ismax)
    
    # 请在此处填入用赋值乘法运算符计算梨个数乘以2的代码，并将结果存入multi_result变量
    ###### Begin ######
    multi_result = pear * 2
    ####### End #######
    print(multi_result)

# 获取用户输入，并调用theOperation函数
apple = int(input())
pear = int(input())
theOperation(apple, pear)
