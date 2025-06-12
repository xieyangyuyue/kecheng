#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_STACK 100
#define MAX_INPUT 100
#define MAX_PRODUCTIONS 20

// 定义产生式结构体
typedef struct {
	char origin;
	char array[10];
	int length;
} Production;

// 全局变量定义
Production C[5][8]; // 分析表
Production e, g1, g2, t, s1, s2, f1, f2, f3; // 产生式
char stack[MAX_STACK]; // 分析栈
int top; // 栈顶指针
char input[MAX_INPUT]; // 输入串
int input_pos; // 当前输入位置
int error_pos; // 错误位置

// 函数声明
void initProductions();
void initParsingTable();
void printStep(int step);
int getTerminalIndex(char c);
int getNonTerminalIndex(char c);
void analyze();
int isValidChar(char c);
void replaceChar(char* str, char oldChar, char newChar);

int main() {
	// 初始化
	initProductions();
	initParsingTable();
	
	// 读取输入串
	printf("请输入符号串（以#结束）：");
	int i = 0;
	char ch;
	while ((ch = getchar()) != '#' && i < MAX_INPUT - 1) {
		if (isValidChar(ch)) {
			input[i++] = ch;
		} else {
			printf("非法字符：'%c'，位置%d\n", ch, i);
			return 1;
		}
	}
	input[i] = '#'; // 添加结束符
	input[i+1] = '\0'; // 字符串结束符
	
	// 执行分析
	analyze();
	
	return 0;
}

// 初始化产生式
void initProductions() {
	e.origin = 'E';
	strcpy(e.array, "TG");
	e.length = 2;
	
	g1.origin = 'G';
	strcpy(g1.array, "+TG");
	g1.length = 3;
	
	g2.origin = 'G';
	strcpy(g2.array, "ε");
	g2.length = 0;
	
	t.origin = 'T';
	strcpy(t.array, "FS");
	t.length = 2;
	
	s1.origin = 'S';
	strcpy(s1.array, "*FS");
	s1.length = 3;
	
	s2.origin = 'S';
	strcpy(s2.array, "ε");
	s2.length = 0;
	
	f1.origin = 'F';
	strcpy(f1.array, "(E)");
	f1.length = 3;
	
	f2.origin = 'F';
	strcpy(f2.array, "i");
	f2.length = 1;
	
	// 错误产生式
	Production error;
	error.origin = 'N';
	strcpy(error.array, "");
	error.length = 0;
	
	// 填充错误产生式到所有表项
	for (int i = 0; i < 5; i++) {
		for (int j = 0; j < 8; j++) {
			C[i][j] = error;
		}
	}
}

// 初始化分析表
void initParsingTable() {
	// E的产生式
	C[0][0] = e; // E->TG, FIRST(E)={'(', 'i'}
	C[0][5] = e;
	
	// G的产生式
	C[1][1] = g1; // G->+TG, FIRST(+TG)={'+'}
	C[1][2] = g1; // G->+TG, FIRST(+TG)={'-'}
	C[1][6] = g2; // G->ε, FOLLOW(G)={'),'#'}
	C[1][7] = g2;
	
	// T的产生式
	C[2][0] = t; // T->FS, FIRST(T)={'(', 'i'}
	C[2][5] = t;
	
	// S的产生式
	C[3][3] = s1; // S->*FS, FIRST(*FS)={'*'}
	C[3][4] = s1; // S->*FS, FIRST(*FS)={'/'}
	C[3][1] = s2; // S->ε, FOLLOW(S)={'+', '-', ')', '#'}
	C[3][2] = s2;
	C[3][6] = s2;
	C[3][7] = s2;
	
	// F的产生式
	C[4][0] = f2; // F->i, FIRST(i)={'i'}
	C[4][5] = f1; // F->(E), FIRST((E))={'('}
}

// 获取终结符的索引
int getTerminalIndex(char c) {
	switch(c) {
		case 'i': return 0;
		case '+': return 1;
		case '-': return 2;
		case '*': return 3;
		case '/': return 4;
		case '(': return 5;
		case ')': return 6;
		case '#': return 7;
		default: return -1;
	}
}

// 获取非终结符的索引
int getNonTerminalIndex(char c) {
	switch(c) {
		case 'E': return 0;
		case 'G': return 1;
		case 'T': return 2;
		case 'S': return 3;
		case 'F': return 4;
		default: return -1;
	}
}

// 检查字符是否为有效符号
int isValidChar(char c) {
	return (c == 'i' || c == '+' || c == '-' || c == '*' || c == '/' || 
		c == '(' || c == ')' || c == '#');
}

// 字符替换函数
void replaceChar(char* str, char oldChar, char newChar) {
	for (int i = 0; str[i] != '\0'; i++) {
		if (str[i] == oldChar) {
			str[i] = newChar;
		}
	}
}

// 打印当前步骤
void printStep(int step) {
	char stackCopy[MAX_STACK];
	strcpy(stackCopy, stack);
	replaceChar(stackCopy, '^', 'ε');
	
	char inputCopy[MAX_INPUT];
	strcpy(inputCopy, &input[input_pos]);
	replaceChar(inputCopy, '^', 'ε');
	
	printf("%-5d%-20s%-20s", step, stackCopy, inputCopy);
}

// 执行LL(1)分析
void analyze() {
	// 初始化栈
	top = -1;
	stack[++top] = '#';
	stack[++top] = 'E';
	
	input_pos = 0;
	error_pos = 0;
	
	int step = 1;
	printf("步骤\t分析栈\t\t输入串\t\t产生式\n");
	
	while (1) {
		char x = stack[top];
		char a = input[input_pos];
		
		// 打印当前步骤
		printStep(step++);
		
		// 分析逻辑
		if (x == '#') {
			if (a == '#') {
				printf("分析成功\n");
				break;
			} else {
				printf("分析失败：栈顶为#，但输入未结束\n");
				error_pos = input_pos;
				break;
			}
		} else if (getTerminalIndex(x) != -1) {
			// x是终结符
			if (x == a) {
				printf("%c匹配\n", x);
				top--;
				input_pos++;
			} else {
				printf("分析失败：栈顶终结符'%c'与输入符'%c'不匹配，位置%d\n", x, a, input_pos);
				error_pos = input_pos;
				break;
			}
		} else {
			// x是非终结符
			int i = getNonTerminalIndex(x);
			int j = getTerminalIndex(a);
			
			if (i != -1 && j != -1 && C[i][j].origin != 'N') {
				Production prod = C[i][j];
				top--; // 弹出栈顶非终结符
				
				// 输出产生式
				char prodStr[20];
				sprintf(prodStr, "%c->%s", prod.origin, prod.array);
				replaceChar(prodStr, '^', 'ε');
				printf("%s\n", prodStr);
				
				// 将产生式右部逆序压入栈
				if (prod.length > 0) { // 处理非ε产生式
					for (int k = prod.length - 1; k >= 0; k--) {
						stack[++top] = prod.array[k];
					}
				}
			} else {
				printf("分析失败：无可用产生式，栈顶'%c'，输入符'%c'，位置%d\n", x, a, input_pos);
				error_pos = input_pos;
				break;
			}
		}
	}
}    
