package main

import (
	"bufio"
	"fmt"
	"os"
)

// 常量定义
const (
	maxStack   = 100 // 分析栈最大容量
	maxInput   = 100 // 输入字符串最大长度
	numNonTerm = 5   // 非终结符数量 (E, G, T, S, F)
	numTerm    = 8   // 终结符数量 (i, +, -, *, /, (, ), #)
)

// Production 产生式结构体
type Production struct {
	Origin byte   // 产生式左部非终结符 (如 'E', 'T')
	Array  string // 产生式右部字符串 (如 "TG", "+TG")
	Length int    // 产生式长度 (0 表示空产生式 ε)
}

// Parser 语法分析器状态
type Parser struct {
	stack    []byte                          // 分析栈
	input    []byte                          // 输入字符串
	inputPos int                             // 当前输入位置
	table    [numNonTerm][numTerm]Production // LL(1) 分析表
}

// NewParser 创建并初始化解析器
func NewParser() *Parser {
	p := &Parser{
		stack: make([]byte, 0, maxStack), // 初始化空栈
		input: make([]byte, 0, maxInput), // 初始化输入缓冲区
	}
	p.initProductionsAndTable() // 填充产生式和分析表
	return p
}

// initProductionsAndTable 初始化产生式并构建 LL(1) 分析表
func (p *Parser) initProductionsAndTable() {
	// 定义所有产生式（根据文法规则）
	prodE := Production{Origin: 'E', Array: "TG", Length: 2}       // E -> TG
	prodGpos := Production{Origin: 'G', Array: "+TG", Length: 3}   // G -> +TG
	prodGeps := Production{Origin: 'G', Array: "ε", Length: 0}     // G -> ε
	prodT := Production{Origin: 'T', Array: "FS", Length: 2}       // T -> FS
	prodSop := Production{Origin: 'S', Array: "*FS", Length: 3}    // S -> *FS
	prodSeps := Production{Origin: 'S', Array: "ε", Length: 0}     // S -> ε
	prodFparen := Production{Origin: 'F', Array: "(E)", Length: 3} // F -> (E)
	prodFid := Production{Origin: 'F', Array: "i", Length: 1}      // F -> i
	errorProd := Production{Origin: 'N', Array: "", Length: 0}     // 错误产生式

	// 初始化分析表为错误产生式
	for i := range p.table {
		for j := range p.table[i] {
			p.table[i][j] = errorProd
		}
	}

	// 填充分析表（非终结符行 -> 终结符列）
	// 行 E (索引 0)
	p.table[p.getNonTerminalIndex('E')][p.getTerminalIndex('i')] = prodE
	p.table[p.getNonTerminalIndex('E')][p.getTerminalIndex('(')] = prodE

	// 行 G (索引 1)
	p.table[p.getNonTerminalIndex('G')][p.getTerminalIndex('+')] = prodGpos
	p.table[p.getNonTerminalIndex('G')][p.getTerminalIndex('-')] = prodGpos
	p.table[p.getNonTerminalIndex('G')][p.getTerminalIndex(')')] = prodGeps
	p.table[p.getNonTerminalIndex('G')][p.getTerminalIndex('#')] = prodGeps

	// 行 T (索引 2)
	p.table[p.getNonTerminalIndex('T')][p.getTerminalIndex('i')] = prodT
	p.table[p.getNonTerminalIndex('T')][p.getTerminalIndex('(')] = prodT

	// 行 S (索引 3)
	p.table[p.getNonTerminalIndex('S')][p.getTerminalIndex('*')] = prodSop
	p.table[p.getNonTerminalIndex('S')][p.getTerminalIndex('/')] = prodSop
	p.table[p.getNonTerminalIndex('S')][p.getTerminalIndex('+')] = prodSeps
	p.table[p.getNonTerminalIndex('S')][p.getTerminalIndex('-')] = prodSeps
	p.table[p.getNonTerminalIndex('S')][p.getTerminalIndex(')')] = prodSeps
	p.table[p.getNonTerminalIndex('S')][p.getTerminalIndex('#')] = prodSeps

	// 行 F (索引 4)
	p.table[p.getNonTerminalIndex('F')][p.getTerminalIndex('i')] = prodFid
	p.table[p.getNonTerminalIndex('F')][p.getTerminalIndex('(')] = prodFparen
}

// getTerminalIndex 终结符映射到分析表列索引
func (p *Parser) getTerminalIndex(c byte) int {
	switch c { // 根据终结符顺序映射
	case 'i':
		return 0
	case '+':
		return 1
	case '-':
		return 2
	case '*':
		return 3
	case '/':
		return 4
	case '(':
		return 5
	case ')':
		return 6
	case '#':
		return 7
	default:
		return -1 // 非法终结符
	}
}

// getNonTerminalIndex 非终结符映射到分析表行索引
func (p *Parser) getNonTerminalIndex(c byte) int {
	switch c { // 根据非终结符顺序映射
	case 'E':
		return 0
	case 'G':
		return 1
	case 'T':
		return 2
	case 'S':
		return 3
	case 'F':
		return 4
	default:
		return -1 // 非法非终结符
	}
}

// isValidChar 验证输入字符合法性
func isValidChar(c byte) bool {
	// 允许的终结符集合
	return c == 'i' || c == '+' || c == '-' || c == '*' || c == '/' ||
		c == '(' || c == ')' || c == '#'
}

// printStep 打印分析步骤
func (p *Parser) printStep(step int, action string) {
	// 格式化输出：步骤、分析栈、剩余输入、动作
	stackStr := string(p.stack)
	if stackStr == "" {
		stackStr = "ε"
	}

	inputStr := string(p.input[p.inputPos:])
	if inputStr == "" {
		inputStr = "#"
	}

	fmt.Printf("%-5d%-20s%-20s%s\n", step, stackStr, inputStr, action)
}

// analyze 执行 LL(1) 语法分析
func (p *Parser) analyze() {
	// 初始化栈：压入 # 和起始符号 E
	p.stack = append(p.stack, '#', 'E')
	p.inputPos = 0
	step := 1

	fmt.Printf("%-5s%-20s%-20s%s\n", "步骤", "分析栈", "剩余输入串", "所用产生式")

	for {
		X := p.stack[len(p.stack)-1] // 栈顶元素
		var a byte
		if p.inputPos < len(p.input) {
			a = p.input[p.inputPos] // 当前输入字符
		} else {
			a = '#' // 输入已结束
		}

		action := ""

		// 处理栈顶为 # 的情况
		if X == '#' {
			if a == '#' {
				p.printStep(step, "分析成功")
				fmt.Println("\n(4)输入符号串为合法符号串")
				return
			} else {
				fmt.Printf("\n(4)输入符号串为非法符号串: 错误在字符 '%c' (位置 %d)，期望结束符 '#'\n", a, p.inputPos)
				return
			}
		}

		// 栈顶为终结符
		if p.getTerminalIndex(X) != -1 {
			if X == a {
				action = fmt.Sprintf("匹配终结符 %c", X)
				p.printStep(step, action)
				p.stack = p.stack[:len(p.stack)-1] // 弹出栈顶
				p.inputPos++                       // 输入位置前进
			} else {
				fmt.Printf("\n(4)输入符号串为非法符号串: 错误在字符 '%c' (位置 %d)，期望 '%c'\n", a, p.inputPos, X)
				return
			}
		} else { // 栈顶为非终结符
			ntIdx := p.getNonTerminalIndex(X)
			tIdx := p.getTerminalIndex(a)

			// 检查索引有效性
			if ntIdx == -1 || tIdx == -1 {
				fmt.Printf("\n(4)输入符号串为非法符号串: 无效符号 '%c'\n", X)
				return
			}

			prod := p.table[ntIdx][tIdx]
			if prod.Origin == 'N' { // 查表无产生式
				fmt.Printf("\n(4)输入符号串为非法符号串: 无可用产生式 (非终结符 '%c', 输入 '%c')\n", X, a)
				return
			}

			// 应用产生式
			action = fmt.Sprintf("%c->%s", X, prod.Array)
			p.printStep(step, action)
			p.stack = p.stack[:len(p.stack)-1] // 弹出非终结符

			// 逆序压入产生式右部
			if prod.Length > 0 {
				for i := prod.Length - 1; i >= 0; i-- {
					p.stack = append(p.stack, prod.Array[i])
				}
			}
		}

		step++
		if step > maxStack { // 防止无限循环
			fmt.Println("分析失败：步骤超过最大限制")
			return
		}
	}
}

func main() {
	// 打印开发者信息
	fmt.Println("(1)LL（1）分析程序，编制人：胡国昌，202210120518，计算机4班")

	// 创建解析器
	parser := NewParser()

	// 读取输入
	fmt.Print("(2)输入一以#结束的符号串(包括+—*/（）i#)：")
	scanner := bufio.NewScanner(os.Stdin)
	if scanner.Scan() {
		input := scanner.Text()

		// 输入校验
		valid := true
		hasHash := false
		for i, c := range input {
			if !isValidChar(byte(c)) {
				fmt.Printf("错误：非法字符 '%c' 在位置 %d\n", c, i)
				valid = false
				break
			}
			if c == '#' {
				if i != len(input)-1 {
					fmt.Println("错误：'#' 只能出现在末尾")
					valid = false
					break
				}
				hasHash = true
			}
		}

		if !valid {
			return
		}
		if !hasHash {
			input += "#" // 自动补全结束符
		}

		parser.input = []byte(input)
	}

	// 执行分析
	fmt.Println("\n(3)输出过程如下：")
	parser.analyze()
}
