// rpn/utils_test.go
package rpn

import "testing"

func TestIsOperator(t *testing.T) {
	if !isOperator("+") {
		t.Errorf("Expected + to be an operator")
	}
	if isOperator("5") {
		t.Errorf("Expected 5 not to be an operator")
	}
}

func TestIsOperand(t *testing.T) {
	if !isOperand("123") {
		t.Errorf("Expected 123 to be an operand")
	}
	if !isOperand("3.14") {
		t.Errorf("Expected 3.14 to be an operand")
	}
	if isOperand("+") {
		t.Errorf("Expected + not to be an operand")
	}
}

func TestPrecedence(t *testing.T) {
	if precedence("*") <= precedence("+") {
		t.Errorf("Expected * to have higher precedence than +")
	}
	if precedence("/") != precedence("*") {
		t.Errorf("Expected / and * to have same precedence")
	}
	if precedence("(") != 0 {
		t.Errorf("Expected precedence of ( to be 0, got %d", precedence("("))
	}
}
