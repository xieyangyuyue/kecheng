// Package rpn
package rpn

import (
	"fmt"
	"strings"
	"unicode"
)

// InfixToPostfix 将中缀表达式字符串转换为后缀表达式（表示为字符串切片）
// 例如："(28+68)*2" -> ["28", "68", "+", "2", "*"]
func InfixToPostfix(infix string) ([]string, error) {
	var output []string        // 输出队列，用于存放后缀表达式的token
	var operatorStack []string // 操作符栈

	// 预处理：移除所有空格，并将#替换为空字符串（作为表达式结束标记，但我们按字符串长度处理）
	infix = strings.ReplaceAll(infix, " ", "")
	if strings.HasSuffix(infix, "#") {
		infix = infix[:len(infix)-1]
	}

	var numBuffer strings.Builder // 用于累积多位数字

	for i := 0; i < len(infix); i++ {
		char := rune(infix[i])

		if unicode.IsDigit(char) || (char == '.' && numBuffer.Len() > 0 && strings.ContainsRune(numBuffer.String(), '.')) { // . 应该在数字中
			numBuffer.WriteRune(char)
		} else if char == '.' && numBuffer.Len() == 0 { // 小数点开头
			numBuffer.WriteRune(char)
		} else {
			// 数字累积结束，将其加入输出
			if numBuffer.Len() > 0 {
				output = append(output, numBuffer.String())
				numBuffer.Reset()
			}

			token := string(char)
			switch token {
			case "+", "-": // 加减操作符
				// 当栈不为空，且栈顶不是左括号，且栈顶操作符优先级大于等于当前操作符
				for len(operatorStack) > 0 && operatorStack[len(operatorStack)-1] != "(" &&
					precedence(operatorStack[len(operatorStack)-1]) >= precedence(token) {
					output = append(output, operatorStack[len(operatorStack)-1]) // 弹出栈顶操作符到输出
					operatorStack = operatorStack[:len(operatorStack)-1]         // 从栈中移除
				}
				operatorStack = append(operatorStack, token) // 当前操作符入栈
			case "*", "/": // 乘除操作符
				// 当栈不为空，且栈顶不是左括号，且栈顶操作符优先级大于等于当前操作符
				// 注意：* / 优先级相同时，也需要弹出栈顶（左结合性）
				for len(operatorStack) > 0 && operatorStack[len(operatorStack)-1] != "(" &&
					precedence(operatorStack[len(operatorStack)-1]) >= precedence(token) {
					output = append(output, operatorStack[len(operatorStack)-1])
					operatorStack = operatorStack[:len(operatorStack)-1]
				}
				operatorStack = append(operatorStack, token)
			case "(": // 左括号
				operatorStack = append(operatorStack, token) // 直接入栈
			case ")": // 右括号
				foundLeftParen := false
				for len(operatorStack) > 0 {
					op := operatorStack[len(operatorStack)-1]
					operatorStack = operatorStack[:len(operatorStack)-1] // 弹出
					if op == "(" {
						foundLeftParen = true
						break // 找到左括号，结束
					}
					output = append(output, op) // 操作符加入输出
				}
				if !foundLeftParen {
					return nil, fmt.Errorf("表达式错误：括号不匹配，缺少左括号")
				}
			default: // 非数字、非操作符、非括号，视为非法字符
				// 如果numBuffer在此之前有内容，先处理
				if numBuffer.Len() > 0 {
					output = append(output, numBuffer.String())
					numBuffer.Reset()
				}
				return nil, fmt.Errorf("表达式错误：包含非法字符 '%s'", token)
			}
		}
	}

	// 处理最后一个数字（如果表达式以数字结尾）
	if numBuffer.Len() > 0 {
		output = append(output, numBuffer.String())
	}

	// 将栈中剩余的操作符全部弹出到输出队列
	for len(operatorStack) > 0 {
		op := operatorStack[len(operatorStack)-1]
		if op == "(" { // 如果栈顶是左括号，说明括号不匹配
			return nil, fmt.Errorf("表达式错误：括号不匹配，多余的左括号")
		}
		output = append(output, op)
		operatorStack = operatorStack[:len(operatorStack)-1]
	}

	return output, nil
}

// FormatPostfixForDisplay 根据文档要求格式化后缀表达式字符串 [cite: 1]
// 例如：["28", "68", "+", "2", "*"] -> "28&68+2*"
func FormatPostfixForDisplay(postfixTokens []string) string {
	var result strings.Builder
	for i, token := range postfixTokens {
		result.WriteString(token)
		if i < len(postfixTokens)-1 {
			// 如果当前token和下一个token都是操作数，则用'&'分隔 [cite: 1]
			if isOperand(token) && isOperand(postfixTokens[i+1]) {
				result.WriteString("&")
			}
			// 如果当前是操作数，下一个是操作符，或者当前是操作符，下一个是操作数，
			// 或者都是操作符，则不需要特殊分隔符，它们自然分开。
			// C代码中数字后会加'#'，这里我们通过token列表已经区分了。
			// 文档的例子 "28&68+2*" 表明只有连续数字间加&。
		}
	}
	return result.String()
}
