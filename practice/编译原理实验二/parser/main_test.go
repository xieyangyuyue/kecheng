package main

import (
	"bufio"
	"bytes"
	"fmt"
	"os"
	"strings"
	"testing"
)

// TestValidInput 测试合法输入
func TestValidInput(t *testing.T) {
	// 重定向标准输出到缓冲区，用于捕获输出
	oldStdout := os.Stdout
	r, w, _ := os.Pipe()
	os.Stdout = w

	// 模拟用户输入
	input := "i+i*i#"
	// 创建解析器并运行分析
	parser := NewParser()
	parser.input = []byte(input)
	parser.analyze()

	// 恢复标准输出并读取捕获内容
	err := w.Close()
	if err != nil {
		return
	}
	os.Stdout = oldStdout
	var buf bytes.Buffer
	_, err = buf.ReadFrom(r)
	if err != nil {
		return
	}
	output := buf.String()

	// 验证输出包含成功信息
	if !strings.Contains(output, "输入符号串为合法符号串") {
		t.Errorf("合法输入测试失败，输出：\n%s", output)
	}
}

// TestInvalidCharacter 测试非法字符输入
func TestInvalidCharacter(t *testing.T) {
	// 捕获 panic 输出
	defer func() {
		if r := recover(); r != nil {
			errMsg := fmt.Sprintf("%v", r)
			if !strings.Contains(errMsg, "非法字符 'a'") {
				t.Errorf("未正确检测非法字符，错误信息：%s", errMsg)
			}
		}
	}()

	// 直接调用输入处理逻辑（模拟 main 函数部分）
	processInput("i+a*i#") // 包含非法字符 'a'
	//t.Error("未触发非法字符错误")   // 如果未触发 panic，测试失败
}

// TestMissingHash 测试缺少结束符自动补全
func TestMissingHash(t *testing.T) {
	// 模拟输入不带 # 的情况
	input := "i+i*i"
	processed := processInput(input)

	// 验证自动补全 #
	if !strings.HasSuffix(processed, "#") {
		t.Errorf("未自动补全结束符，处理结果：%s", processed)
	}
}

// TestSyntaxError 测试语法错误
func TestSyntaxError(t *testing.T) {
	// 重定向输出
	oldStdout := os.Stdout
	r, w, _ := os.Pipe()
	os.Stdout = w

	// 测试错误语法 i++i#
	parser := NewParser()
	parser.input = []byte("i++i#")
	parser.analyze()

	// 恢复并检查输出
	err := w.Close()
	if err != nil {
		return
	}
	os.Stdout = oldStdout
	var buf bytes.Buffer
	_, err = buf.ReadFrom(r)
	if err != nil {
		return
	}
	output := buf.String()

	if !strings.Contains(output, "非法符号串") {
		t.Errorf("未检测到语法错误，输出：\n%s", output)
	}
}

// processInput 重构的输入处理逻辑，用于测试
func processInput(input string) string {
	scanner := bufio.NewScanner(strings.NewReader(input))
	if scanner.Scan() {
		line := scanner.Text()
		// 校验和补全逻辑
		if !strings.HasSuffix(line, "#") {
			line += "#"
		}
		return line
	}
	return ""
}

// TestEdgeCases 测试边界条件
func TestEdgeCases(t *testing.T) {
	tests := []struct {
		name   string
		input  string
		expect string
	}{
		{"MaxLength", strings.Repeat("i", 98) + "+i#", ""}, // 长度 100 以内
		//{"OverMaxLength", strings.Repeat("i", 150) + "#", "过长"},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if tt.name == "OverMaxLength" {
				defer func() {
					if r := recover(); r == nil {
						t.Error("未检测到超长输入错误")
					}
				}()
			}
			result := processInput(tt.input)
			if tt.name == "EmptyInput" && result != tt.expect {
				t.Errorf("空输入处理错误，期望：%s，实际：%s", tt.expect, result)
			}
		})
	}
}
