# 定义addressone和addresstwo两个变量，并为其赋值
addressone = 20
addresstwo = 20
addressthree = 12

# 在if后面的括号中填入判断变量addressone与变量addresstwo是否有相同的存储单元的语句
###### Begin ######
if addressone is addresstwo:
    print("变量addressone与变量addresstwo有相同的存储单元")
else:
    print("变量addressone与变量addresstwo的存储单元不同")
####### End #######

# 在if后面的括号中填入判断变量addresstwo与变量addressthree是否没有相同的存储单元的语句
###### Begin ######
if addresstwo is not addressthree:
    print("变量addresstwo与变量addressthree的存储单元不同")
else:
    print("变量addresstwo与变量addressthree有相同的存储单元")
####### End #######
