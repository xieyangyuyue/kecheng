package main

import (
	"fmt"
	"strconv"
	"strings"
	// "bufio" // 如果需要从 stdin 读取输入，则取消注释
	// "os"    // 如果需要从 stdin 读取输入，则取消注释
)

// 定义常量
// const maxStack = 20 // C++代码中的MAX_STACK，Go的slice动态管理，但可以用于逻辑限制

// 定义终结符（vt）：+ - * / ( ) i #
var vt = []rune{'+', '-', '*', '/', '(', ')', 'i', '#'}

// 定义非终结符（vn）：E T F
var vn = []rune{'E', 'T', 'F'}

// 产生式集合
var lrProductions = []string{
	"E->E+T", // 0
	"E->E-T", // 1
	"E->T",   // 2
	"T->T*F", // 3
	"T->T/F", // 4
	"T->F",   // 5
	"F->(E)", // 6
	"F->i",   // 7
}

// ACTION表 (与C++代码一致)
// "" 表示 C++ 中的 NULL (错误或无动作)
var actionTable = [][]string{
	// +     -      * /      (      )      i      #
	{"s5", "s6", "", "", "s4", "", "s7", ""},     //0
	{"", "", "", "", "", "", "", "acc"},          //1
	{"r2", "r2", "s8", "s9", "", "r2", "", "r2"}, //2
	{"r5", "r5", "r5", "r5", "", "r5", "", "r5"}, //3
	{"s5", "s6", "", "", "s4", "", "s7", ""},     //4
	{"r7", "r7", "r7", "r7", "", "r7", "", "r7"}, //5
	{"s5", "s6", "", "", "s4", "", "s7", ""},     //6
	{"s5", "s6", "", "", "s4", "", "s7", ""},     //7
	{"s5", "s6", "", "", "s4", "", "s7", ""},     //8
	{"s5", "s6", "", "", "s4", "", "s7", ""},     //9
	{"r0", "r0", "s8", "s9", "", "r0", "", "r0"}, //10
	{"r1", "r1", "s8", "s9", "", "r1", "", "r1"}, //11
	{"r3", "r3", "r3", "r3", "", "r3", "", "r3"}, //12
	{"r4", "r4", "r4", "r4", "", "r4", "", "r4"}, //13
	{"r6", "r6", "r6", "r6", "", "r6", "", "r6"}, //14
}

// GOTO表 (与C++代码一致)
// -1 表示错误或无转换
var gotoTable = [][]int{
	// E   T   F
	{1, 2, 3},    //0
	{-1, -1, -1}, //1
	{-1, -1, -1}, //2
	{-1, -1, -1}, //3
	{-1, 2, 3},   //4 (GOTO[4][E] = -1, 根据C++代码)
	{-1, -1, -1}, //5
	{10, 2, 3},   //6
	{-1, -1, 11}, //7
	{-1, -1, 12}, //8
	{-1, -1, 13}, //9
	{-1, -1, -1}, //10
	{-1, -1, -1}, //11
	{-1, -1, -1}, //12
	{-1, -1, -1}, //13
	{-1, -1, -1}, //14
}

// 分析栈 - 使用 slice 模拟
var stateStack []int
var symbolStack []rune

// 输入串相关
var inputRunes []rune // 作为 input_buffer 的替代
var inputPtr int      // 当前输入指针

// initGlobalStacksAndInput 初始化分析栈和输入串
// 根据C++的 init_stack() 和 strcpy(input_buffer, input)
func initGlobalStacksAndInput(input string) {
	stateStack = []int{0}     // 初始状态0
	symbolStack = []rune{'#'} // 初始符号#
	inputRunes = []rune(input)
	inputPtr = 0
}

// findVt 查找终结符索引
func findVt(char rune) int {
	for i, t := range vt {
		if t == char {
			return i
		}
	}
	return -1 // 未找到
}

// findVn 查找非终结符索引
func findVn(char rune) int {
	for i, nt := range vn {
		if nt == char {
			return i
		}
	}
	return -1 // 未找到
}

// printAnalysisStep 打印分析过程中的一步
// 对应C++中的 print_step() 和后续的动作打印
func printAnalysisStep(step int, currentActionMsg string) {
	// 构建状态栈字符串
	var stateStackBuilder strings.Builder
	stateStackBuilder.WriteString("[")
	for i, s := range stateStack {
		stateStackBuilder.WriteString(strconv.Itoa(s))
		if i < len(stateStack)-1 {
			stateStackBuilder.WriteString(" ")
		}
	}
	stateStackBuilder.WriteString("]")

	// 构建符号栈字符串
	var symbolStackBuilder strings.Builder
	symbolStackBuilder.WriteString("[")
	for i, s := range symbolStack {
		symbolStackBuilder.WriteString(string(s))
		if i < len(symbolStack)-1 {
			symbolStackBuilder.WriteString(" ")
		}
	}
	symbolStackBuilder.WriteString("]")

	remainingInputStr := ""
	if inputPtr < len(inputRunes) {
		remainingInputStr = string(inputRunes[inputPtr:])
	}

	// 输出格式参考C++代码的 printf 格式和实验文档的示例 [cite: 3]
	// C++: printf("%-6d[state_stack]\t[symbol_stack]\t%-10s\tACTION_TEXT\n");
	// 调整宽度以获得更好的可读性
	fmt.Printf("%-6d%-23s%-23s%-23s%s\n",
		step,
		stateStackBuilder.String(),
		symbolStackBuilder.String(),
		remainingInputStr,
		currentActionMsg,
	)
}

// lrParse LR(1)分析主函数
func lrParse(input string, verbose bool) bool {
	initGlobalStacksAndInput(input)
	step := 1

	if verbose {
		// 打印表头，根据 C++ main 函数和实验文档的格式 [cite: 3]
		fmt.Println("步骤  状态栈                 符号栈                 剩余输入串             动作")
	}

	for {
		if len(stateStack) == 0 {
			if verbose {
				// 这种情况理论上在正确初始化后不应首先发生，除非栈操作错误
				printAnalysisStep(step, "错误 (状态栈为空)")
			}
			return false
		}
		currentState := stateStack[len(stateStack)-1]
		var currentInputSymbol rune

		if inputPtr >= len(inputRunes) {
			// 输入已耗尽。如果尚未接受，则为错误。
			// 'acc' 动作应在处理 '#' 时发生。
			if verbose {
				actionDescription := fmt.Sprintf("错误 (输入提前结束，当前状态 %d，期望 আরও 输入或接受)", currentState)
				printAnalysisStep(step, actionDescription)
			}
			return false
		}
		currentInputSymbol = inputRunes[inputPtr]

		vtIdx := findVt(currentInputSymbol)

		actionDescription := "" // 用于在出错或执行动作时传递给 printAnalysisStep

		if vtIdx == -1 {
			// C++: if (vt_idx == -1 && a != '\0') { printf("\n错误：非法字符'%c'\n", a); }
			// 在Go中，如果findVt返回-1，意味着它不是一个合法的终结符
			actionDescription = fmt.Sprintf("错误 (非法字符 '%c')", currentInputSymbol)
			if verbose {
				printAnalysisStep(step, actionDescription)
			}
			return false
		}

		action := ""
		// 检查ACTION表访问是否越界
		if currentState >= 0 && currentState < len(actionTable) && vtIdx >= 0 && vtIdx < len(actionTable[currentState]) {
			action = actionTable[currentState][vtIdx]
		} else {
			actionDescription = fmt.Sprintf("错误 (ACTION表访问越界, 状态=%d, 输入符号索引=%d)", currentState, vtIdx)
			if verbose {
				printAnalysisStep(step, actionDescription)
			}
			return false
		}

		if action == "" { // 对应 C++ 中的 NULL，表示错误条目
			actionDescription = fmt.Sprintf("错误 (状态 %d 对输入 '%c' 无动作)", currentState, currentInputSymbol)
			if verbose {
				printAnalysisStep(step, actionDescription)
			}
			return false
		}

		if action == "acc" {
			actionDescription = "接受"
			if verbose {
				printAnalysisStep(step, actionDescription)
			}
			return true
		} else if action[0] == 's' { // 移进 (Shift)
			newState, err := strconv.Atoi(action[1:])
			if err != nil {
				actionDescription = fmt.Sprintf("错误 (无效移进动作 '%s')", action)
				if verbose {
					printAnalysisStep(step, actionDescription)
				}
				return false
			}

			// 执行移进
			stateStack = append(stateStack, newState)
			symbolStack = append(symbolStack, currentInputSymbol)
			inputPtr++
			actionDescription = fmt.Sprintf("移进到状态 %d (%s)", newState, action)
			if verbose {
				printAnalysisStep(step, actionDescription)
			}
		} else if action[0] == 'r' { // 归约 (Reduce)
			prodRuleIdx, err := strconv.Atoi(action[1:])
			if err != nil || prodRuleIdx < 0 || prodRuleIdx >= len(lrProductions) {
				actionDescription = fmt.Sprintf("错误 (无效归约动作 '%s')", action)
				if verbose {
					printAnalysisStep(step, actionDescription)
				}
				return false
			}

			production := lrProductions[prodRuleIdx] // 例如 "E->E+T"
			parts := strings.Split(production, "->")
			if len(parts) != 2 { // 基本的产生式格式校验
				actionDescription = fmt.Sprintf("错误 (产生式格式错误 '%s')", production)
				if verbose {
					printAnalysisStep(step, actionDescription)
				}
				return false
			}
			lhsSymbol := []rune(parts[0])[0] // 左部符号, e.g., 'E'
			rhsStr := parts[1]               // 右部字符串, e.g., "E+T"
			rhsLen := len(rhsStr)            // 右部长度 (C++: strlen(p+3) or strlen(p)-3)

			// 检查栈中是否有足够的元素进行归约
			if len(stateStack) <= rhsLen || len(symbolStack) <= rhsLen { // 注意：符号栈包含初始'#'，状态栈包含初始'0'
				actionDescription = fmt.Sprintf("错误 (栈元素不足以按 '%s' 归约)", production)
				if verbose {
					printAnalysisStep(step, actionDescription)
				}
				return false
			}
			// 弹出产生式右部对应的状态和符号
			stateStack = stateStack[:len(stateStack)-rhsLen]
			symbolStack = symbolStack[:len(symbolStack)-rhsLen]

			if len(stateStack) == 0 { // 不应发生
				actionDescription = "错误 (归约后状态栈为空)"
				if verbose {
					printAnalysisStep(step, actionDescription)
				}
				return false
			}
			prevStateForGoto := stateStack[len(stateStack)-1]
			vnIdx := findVn(lhsSymbol)

			if vnIdx == -1 { // 左部非终结符未找到
				actionDescription = fmt.Sprintf("错误 (归约时未识别产生式左部 '%c')", lhsSymbol)
				if verbose {
					printAnalysisStep(step, actionDescription)
				}
				return false
			}

			newState := -1
			// 检查GOTO表访问是否越界
			if prevStateForGoto >= 0 && prevStateForGoto < len(gotoTable) && vnIdx >= 0 && vnIdx < len(gotoTable[prevStateForGoto]) {
				newState = gotoTable[prevStateForGoto][vnIdx]
			}

			if newState == -1 { // GOTO失败
				actionDescription = fmt.Sprintf("错误 (GOTO[%d, %c] 未定义)", prevStateForGoto, lhsSymbol)
				if verbose {
					printAnalysisStep(step, actionDescription)
				}
				return false
			}

			// 将新状态和产生式左部符号压栈
			stateStack = append(stateStack, newState)
			symbolStack = append(symbolStack, lhsSymbol)

			// C++: printf("归约%s → %s\n", p, p+3);
			actionDescription = fmt.Sprintf("归约 %s (GOTO -> %d)", production, newState)
			if verbose {
				printAnalysisStep(step, actionDescription)
			}
		} else {
			actionDescription = fmt.Sprintf("错误 (未知动作 '%s')", action)
			if verbose {
				printAnalysisStep(step, actionDescription)
			}
			return false
		}
		step++
		if step > 200 { // 安全措施，防止因表错误导致的无限循环
			if verbose {
				fmt.Println("\n错误：分析步骤过多，可能存在死循环或分析表问题")
			}
			return false
		}
	}
}

// testCases 运行C++代码中提供的测试用例
func testCases() {
	tests := []struct {
		name  string
		input string
	}{
		{"合法1", "i+i*i#"},
		{"合法2", "(i+i)*i#"},
		{"合法3", "i/i-i#"},
		{"非法 (无#)", "i+"},
		{"非法字符", "i+a#"},
		{"运算符错误", "i++i#"},
		{"空输入", "#"},
	}

	fmt.Println("LR(1)分析程序 (Go 版本)")     // 根据实验文档[cite:3] (1)
	fmt.Println("编制人：胡国昌学号：202210120518 班级：02") // 占位符信息

	for i, tc := range tests {
		// 根据实验文档[cite:3] (2) 模拟输入提示
		fmt.Printf("\n--- 测试用例 %d ---\n", i+1)
		fmt.Printf("输入一以#结束的符号串: %s\n", tc.input) // 模拟用户输入

		// 检查输入是否以 # 结尾，这部分逻辑原在 C++ main 中
		if !strings.HasSuffix(tc.input, "#") {
			fmt.Println("错误：输入必须以#结尾")
			fmt.Printf("\n输入符号串 \"%s\" 是不合法句子\n", tc.input) // 根据实验文档[cite:4]
			fmt.Println(strings.Repeat("-", 50))
			continue
		}

		result := lrParse(tc.input, true) // 详细输出过程

		// 根据实验文档[cite:4] 输出最终结果
		if result {
			fmt.Printf("\n输入符号串 \"%s\" 是合法句子\n", tc.input)
		} else {
			fmt.Printf("\n输入符号串 \"%s\" 是不合法句子\n", tc.input)
		}
		fmt.Println(strings.Repeat("-", 50))
	}
}

// main 主函数
func main() {
	// 如果需要手动输入，可以取消注释下面的代码块：
	/*
		fmt.Println("LR(1)分析程序 (Go 版本)\t作者：XXX 学号：XXX 班级：XXX")
		fmt.Print("请输入以#结尾的符号串：")

		reader := bufio.NewReader(os.Stdin)
		inputLine, _ := reader.ReadString('\n')
		inputLine = strings.TrimSpace(inputLine) // 去除换行符等

		if !strings.HasSuffix(inputLine, "#") {
			fmt.Println("错误：输入必须以#结尾")
			return
		}

		fmt.Println("\n步骤  状态栈                 符号栈                 剩余输入串             动作")
		result := lrParse(inputLine, true)

		if result {
			fmt.Printf("\n输入符号串 \"%s\" 是合法句子\n", inputLine)
		} else {
			fmt.Printf("\n输入符号串 \"%s\" 是不合法句子\n", inputLine)
		}
	*/
	testCases()
}
