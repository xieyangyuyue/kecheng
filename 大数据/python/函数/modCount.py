# coding:utf-8 

counter = 0

def access():
    global counter  # 使用global关键字来声明我们要修改的是全局变量counter
    counter += 1    # 每次调用函数时，counter的值加1

for i in range(5):
    access()
  
print(counter)
