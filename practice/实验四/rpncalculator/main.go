// main.go
package main

import (
	"bufio"
	"fmt"
	"os"
	"rpncalculator/rpn" // 导入我们自己创建的rpn包
	"strings"
)

func main() {
	reader := bufio.NewReader(os.Stdin)

	// 输出程序信息，参考文档格式 [cite: 1]
	fmt.Println("逆波兰式的生成及计算程序，编制人：姓名，学号，班级") // 请替换为你的信息
	fmt.Print("输入一以#结束的中缀表达式(包括+—*/（）数字#)：") // [cite: 1]

	input, err := reader.ReadString('\n')
	if err != nil {
		fmt.Fprintf(os.Stderr, "读取输入错误: %v\n", err)
		return
	}
	input = strings.TrimSpace(input) // 去除换行符和两端空格

	if !strings.HasSuffix(input, "#") {
		fmt.Fprintf(os.Stderr, "输入错误：表达式必须以 # 结束。\n")
		// 或者可以选择自动添加#： input += "#"
		// return
	}

	// 1. 将中缀表达式转换为后缀表达式
	postfixTokens, err := rpn.InfixToPostfix(input)
	if err != nil {
		fmt.Fprintf(os.Stderr, "中缀转后缀错误: %v\n", err)
		return
	}

	// 2. 格式化并显示后缀表达式 [cite: 1]
	formattedPostfix := rpn.FormatPostfixForDisplay(postfixTokens)
	fmt.Printf("逆波兰式为：%s\n", formattedPostfix)

	// 3. 计算后缀表达式的值
	result, err := rpn.EvaluatePostfix(postfixTokens)
	if err != nil {
		fmt.Fprintf(os.Stderr, "计算错误: %v\n", err)
		return
	}

	// 4. 显示计算结果 [cite: 1]
	// 使用 %g 格式化浮点数，它会根据情况选择 %e 或 %f，且不输出末尾无意义的0
	fmt.Printf("逆波兰式 %s 计算结果为 %g\n", formattedPostfix, result)
}
