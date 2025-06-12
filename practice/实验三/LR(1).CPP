#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include <stdlib.h>

#define MAX_STACK 20

// 定义终结符（vt）：+ - * / ( ) i #
const char vt[] = {'+', '-', '*', '/', '(', ')', 'i', '#'};
// 定义非终结符（vn）：E T F
const char vn[] = {'E', 'T', 'F'};
// 产生式集合（修正为8条）
const char* LR[] = {
	"E->E+T",   // 0
	"E->E-T",   // 1
	"E->T",     // 2
	"T->T*F",   // 3
	"T->T/F",   // 4
	"T->F",     // 5
	"F->(E)",   // 6
	"F->i"      // 7
};

// 更新后的ACTION表（15个状态，8个终结符）
const char* ACTION[15][8] = {
	//+      -       *       /       (       )       i       #
	{"s5",  "s6",   NULL,   NULL,   "s4",   NULL,   "s7",   NULL},   //0
	{NULL,  NULL,   NULL,   NULL,   NULL,   NULL,   NULL,   "acc"},  //1
	{"r2",  "r2",   "s8",   "s9",   NULL,   "r2",   NULL,   "r2"},   //2
	{"r5",  "r5",   "r5",   "r5",   NULL,   "r5",   NULL,   "r5"},   //3
	{"s5",  "s6",   NULL,   NULL,   "s4",   NULL,   "s7",   NULL},   //4
	{"r7",  "r7",   "r7",   "r7",   NULL,   "r7",   NULL,   "r7"},   //5
	{"s5",  "s6",   NULL,   NULL,   "s4",   NULL,   "s7",   NULL},   //6
	{"s5",  "s6",   NULL,   NULL,   "s4",   NULL,   "s7",   NULL},   //7
	{"s5",  "s6",   NULL,   NULL,   "s4",   NULL,   "s7",   NULL},   //8
	{"s5",  "s6",   NULL,   NULL,   "s4",   NULL,   "s7",   NULL},   //9
	{"r0",  "r0",   "s8",   "s9",   NULL,   "r0",   NULL,   "r0"},   //10
	{"r1",  "r1",   "s8",   "s9",   NULL,   "r1",   NULL,   "r1"},   //11
	{"r3",  "r3",   "r3",   "r3",   NULL,   "r3",   NULL,   "r3"},   //12
	{"r4",  "r4",   "r4",   "r4",   NULL,   "r4",   NULL,   "r4"},   //13
	{"r6",  "r6",   "r6",   "r6",   NULL,   "r6",   NULL,   "r6"}    //14
};

// 更新后的GOTO表（15个状态，3个非终结符）
const int GOTO[15][3] = {
	//E  T  F
	{1, 2, 3},    //0
	{-1,-1,-1},    //1
	{-1,-1,-1},    //2
	{-1,-1,-1},    //3
	{-1,2,3},     //4
	{-1,-1,-1},    //5
	{10,2,3},     //6
	{-1,-1,11},    //7
	{-1,-1,12},    //8
	{-1,-1,13},    //9
	{-1,-1,-1},    //10
	{-1,-1,-1},    //11
	{-1,-1,-1},    //12
	{-1,-1,-1},    //13
	{-1,-1,-1}     //14
};

// 栈结构
int state_stack[MAX_STACK];
char symbol_stack[MAX_STACK];
int state_top = 0, symbol_top = 0;
char input_buffer[MAX_STACK];
int input_ptr = 0;

void init_stack() {
	state_stack[0] = 0;
	symbol_stack[0] = '#';
	state_top = symbol_top = 1;
	input_ptr = 0;
}

int find_vt(char c) {
	for (int i = 0; i < 8; i++)
		if (vt[i] == c) return i;
	return -1;
}

int find_vn(char c) {
	for (int i = 0; i < 3; i++)
		if (vn[i] == c) return i;
	return -1;
}

void print_step(int step) {
	printf("%-6d[", step);
	for (int i = 0; i < state_top; i++)
		printf(i < state_top-1 ? "%d " : "%d]", state_stack[i]);
	
	printf("\t[");
	for (int i = 0; i < symbol_top; i++)
		printf(i < symbol_top-1 ? "%c " : "%c]", symbol_stack[i]);
	
	printf("\t%-10s\t", input_buffer + input_ptr);
}

int lr_parse(const char* input, int verbose) {
	strcpy(input_buffer, input);
	init_stack();
	int step = 1;
	
	while (1) {
		int state = state_stack[state_top-1];
		char a = input_buffer[input_ptr];
		int vt_idx = find_vt(a);
		
		if (vt_idx == -1 && a != '\0') {
			if (verbose) printf("\n错误：非法字符'%c'\n", a);
			return 0;
		}
		
		const char* action = ACTION[state][vt_idx];
		if (verbose) print_step(step++);
		
		if (!action) {
			if (verbose) printf("\n错误：状态%d遇到非法符号'%c'\n", state, a);
			return 0;
		}
		
		if (strcmp(action, "acc") == 0) {
			if (verbose) printf("接受\n");
			return 1;
		}
		else if (action[0] == 's') {  // 移进
			int new_state = atoi(action+1);
			state_stack[state_top++] = new_state;
			symbol_stack[symbol_top++] = a;
			input_ptr++;
			if (verbose) printf("移进%s\n", action);
		}
		else if (action[0] == 'r') {  // 归约
			int prod = atoi(action+1);
			const char* p = LR[prod];
			int len = strlen(p) - 3;
			
			// 弹出产生式右部
			state_top -= len;
			symbol_top -= len;
			
			// 获取左部符号
			char A = p[0];
			int vn_idx = find_vn(A);
			
			// 查找GOTO表
			int new_state = GOTO[state_stack[state_top-1]][vn_idx];
			if (new_state == -1) {
				if (verbose) printf("\n错误：无法找到GOTO[%d, %c]\n", state_stack[state_top-1], A);
				return 0;
			}
			
			state_stack[state_top++] = new_state;
			symbol_stack[symbol_top++] = A;
			if (verbose) printf("归约%s → %s\n", p, p+3);
		}
		else {
			if (verbose) printf("\n未知动作：%s\n", action);
			return 0;
		}
	}
}
// 在main函数中添加以下测试代码（需临时修改）
void test_cases() {
	const char* tests[] = {
		"i+i*i#",      // 合法
		"(i+i)*i#",    // 合法
		"i/i-i#",      // 合法
		"i+",          // 非法（无#）
		"i+a#",        // 非法字符
		"i++i#",       // 运算符错误
		"#"            // 空输入
	};
	
	for (int i = 0; i < 7; i++) {
		printf("\n测试用例 %d: %s\n", i+1, tests[i]);
		lr_parse(tests[i], 1); // 详细输出过程
	}
}


int main() {
//	printf("LR(1)分析程序\t作者：XXX 学号：XXX 班级：XXX\n");
//	printf("请输入以#结尾的符号串：");
//	
//	fgets(input_buffer, MAX_STACK, stdin);
//	input_buffer[strcspn(input_buffer, "\n")] = '\0';
//	
//	if (input_buffer[strlen(input_buffer)-1] != '#') {
//		printf("错误：输入必须以#结尾\n");
//		return 1;
//	}
//	
//	printf("\n步骤\t状态栈\t\t符号栈\t\t剩余输入\t动作\n");
//	int result = lr_parse(input_buffer, 1);
//	
//	printf("\n输入符号串%s是合法句子\n", result ? "" : "不");
	test_cases(); 
	return 0;
}
