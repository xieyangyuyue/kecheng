// Package rpn
package rpn

import (
	"fmt"
	"strconv"
)

// EvaluatePostfix 计算后缀表达式（由token组成的切片）的值
// 例如：["28", "68", "+", "2", "*"] -> 192
func EvaluatePostfix(postfixTokens []string) (float64, error) {
	var operandStack []float64 // 操作数栈

	for _, token := range postfixTokens {
		if isOperand(token) { // 如果是操作数
			num, err := strconv.ParseFloat(token, 64)
			if err != nil {
				// 这个错误理论上不应该发生，因为 InfixToPostfix 应该已经保证了操作数的有效性
				// 但为了健壮性，还是加上
				return 0, fmt.Errorf("求值错误：无效的操作数 '%s': %v", token, err)
			}
			operandStack = append(operandStack, num) // 操作数入栈
		} else if isOperator(token) { // 如果是操作符
			if len(operandStack) < 2 {
				return 0, fmt.Errorf("求值错误：操作数不足，对于操作符 '%s'", token)
			}
			// 弹出两个操作数
			op2 := operandStack[len(operandStack)-1]
			operandStack = operandStack[:len(operandStack)-1]
			op1 := operandStack[len(operandStack)-1]
			operandStack = operandStack[:len(operandStack)-1]

			var result float64
			switch token {
			case "+":
				result = op1 + op2
			case "-":
				result = op1 - op2
			case "*":
				result = op1 * op2
			case "/":
				if op2 == 0 {
					return 0, fmt.Errorf("求值错误：除零错误") // [cite: 1] (文档提到错误提示)
				}
				result = op1 / op2
			default: // 理论上不会到这里，因为isOperator做了检查
				return 0, fmt.Errorf("求值错误：未知的操作符 '%s'", token)
			}
			operandStack = append(operandStack, result) // 运算结果入栈
		} else {
			// 非操作数也非操作符，属于无效token
			return 0, fmt.Errorf("求值错误：无效的token '%s'", token)
		}
	}

	// 最终，栈中应该只剩下一个元素，即表达式的结果
	if len(operandStack) != 1 {
		return 0, fmt.Errorf("求值错误：最终栈中元素数量不为1 (实际为 %d)，可能表达式不完整或格式错误", len(operandStack))
	}

	return operandStack[0], nil
}
