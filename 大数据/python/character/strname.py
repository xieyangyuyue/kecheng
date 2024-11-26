# 获取待处理的源字符串
source_string = input("请输入待处理的源字符串: ")

# 执行转换逻辑
string = source_string.title().strip()

# 打印转换后的字符串和长度
print(string)
print("--------------")
print(len(string))
