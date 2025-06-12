// Package rpn
package rpn

import "strconv"

// isOperator 判断一个token是否为操作符
func isOperator(token string) bool {
	switch token {
	case "+", "-", "*", "/":
		return true
	}
	return false
}

// isOperand 判断一个token是否为操作数（这里简化为数字）
// 在更复杂的场景下，可能需要更完善的数字或变量名校验
func isOperand(token string) bool {
	_, err := strconv.ParseFloat(token, 64)
	return err == nil
}

// precedence 返回操作符的优先级
// * 和 / 优先级高于 + 和 -
func precedence(op string) int {
	switch op {
	case "+", "-":
		return 1
	case "*", "/":
		return 2
	}
	return 0 // 其他情况（如括号或无效操作符）
}
