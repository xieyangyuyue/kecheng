// evaluator_test.go
package rpn

import (
	"math"
	"testing"
)

func TestEvaluatePostfix(t *testing.T) {
	tests := []struct {
		name    string
		tokens  []string
		want    float64
		wantErr bool
	}{
		{"文档示例", []string{"28", "68", "+", "2", "*"}, 192, false},                        // (28+68)*2 = 96*2 = 192 [cite: 1]
		{"来自转换器1", []string{"1", "2", "3", "*", "+"}, 7, false},                          // 1 + (2*3) = 7
		{"来自转换器2", []string{"1", "2", "+", "3", "*"}, 9, false},                          // (1+2)*3 = 9
		{"复杂运算", []string{"100", "200", "10", "/", "+", "3", "2", "*", "-"}, 114, false}, // 100 + (200/10) - (3*2) = 100 + 20 - 6 = 114
		{"小数运算", []string{"3.14", "2.5", "2", "*", "+"}, 8.14, false},                    // 3.14 + (2.5*2) = 3.14 + 5 = 8.14
		{"除零", []string{"5", "0", "/"}, 0, true},
		{"操作数不足1", []string{"1", "+"}, 0, true},
		{"操作数不足2", []string{"+"}, 0, true},
		{"无效Token", []string{"1", "abc", "+"}, 0, true},
		{"最终栈元素过多", []string{"1", "2"}, 0, true}, // 表达式不完整，计算后栈里不止一个数
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			got, err := EvaluatePostfix(tt.tokens)
			if (err != nil) != tt.wantErr {
				t.Errorf("EvaluatePostfix() error = %v, wantErr %v", err, tt.wantErr)
				return
			}
			// 比较浮点数时要注意精度问题
			if !tt.wantErr && math.Abs(got-tt.want) > 1e-9 { // 1e-9 是一个小的容差
				t.Errorf("EvaluatePostfix() = %v, want %v", got, tt.want)
			}
		})
	}
}
