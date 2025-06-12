package cn.edu.ctgu;
// Lexer.java

import java.util.HashMap;
import java.util.Map;

/**
 * 词法分析器核心类
 * 主要功能：
 * 1. 逐个字符读取输入字符串
 * 2. 识别不同词法单元模式
 * 3. 生成对应Token对象
 * 4. 跟踪源码位置信息
 */
public class Lexer {
    // 输入字符串和当前位置跟踪
    private final String input;   // 原始输入代码
    private int pos;              // 当前字符位置索引
    private int line;             // 当前行号（从1开始）
    private int column;           // 当前列号（从1开始）
    private char currentChar;     // 当前处理的字符


    // 关键字映射表（标识符到TokenType的映射）
    private static final Map<String, TokenType> KEYWORDS = new HashMap<>();
    static {
        // 初始化Java关键字（可根据需求扩展）
        KEYWORDS.put("public", TokenType.PUBLIC);
        KEYWORDS.put("class", TokenType.CLASS);
        KEYWORDS.put("static", TokenType.STATIC);
        KEYWORDS.put("void", TokenType.VOID);
        KEYWORDS.put("int", TokenType.INT);
    }

    // 构造方法：初始化词法分析器
    public Lexer(String input) {
        this.input = input;
        this.pos = 0;                        // 从第一个字符开始
        this.line = 1;                        // 初始行号为1
        this.column = 1;                      // 初始列号为1
        // 初始化当前字符（处理空输入情况）
        this.currentChar = !input.isEmpty() ? input.charAt(0) : '\0';
    }

    // 前进到下一个字符，更新位置信息
    private void advance() {
        // 处理换行符：行号增加，列号重置
        if (currentChar == '\n') {
            line++;
            column = 0;  // 下一轮advance会+1
        }

        pos++;           // 移动字符指针
        column++;        // 列号递增

        // 判断是否到达输入末尾
        if (pos >= input.length()) {
            currentChar = '\0';  // 结束符
        } else {
            currentChar = input.charAt(pos);  // 读取下一个字符
        }
    }

    // 跳过空白字符（包括空格、制表符、换行等）
    private void skipWhitespace() {
        // 循环处理连续空白字符
        while (Character.isWhitespace(currentChar)) {
            advance();
        }
    }

    // 读取标识符或关键字
    private Token readIdentifier() {
        // 记录起始位置信息
        int startPos = pos;
        int startLine = line;
        int startCol = column;

        // 持续读取字母和数字
        while (Character.isLetterOrDigit(currentChar)) {
            advance();
        }

        // 提取标识符字符串
        String value = input.substring(startPos, pos);
        // 判断是否是关键字
        TokenType type = KEYWORDS.getOrDefault(value, TokenType.IDENTIFIER);
        return new Token(type, value, startLine, startCol);
    }

    // 读取数字字面量
    private Token readNumber() {
        // 记录起始位置信息
        int startPos = pos;
        int startLine = line;
        int startCol = column;

        // 持续读取数字字符
        while (Character.isDigit(currentChar)) {
            advance();
        }

        // 提取数字字符串
        String value = input.substring(startPos, pos);
        return new Token(TokenType.NUMBER, value, startLine, startCol);
    }

    // 主方法：生成下一个Token
    public Token nextToken() {
        // 循环处理直到生成有效Token或到达EOF
        while (currentChar != '\0') {
            // 跳过空白字符
            if (Character.isWhitespace(currentChar)) {
                skipWhitespace();
                continue;
            }

            // 处理标识符（字母开头）
            if (Character.isLetter(currentChar)) {
                return readIdentifier();
            }

            // 处理数字字面量
            if (Character.isDigit(currentChar)) {
                return readNumber();
            }

            // 处理符号和运算符
            Token token;
            int currentLine = line;   // 记录符号的起始位置
            int currentCol = column;

            token = switch (currentChar) {
                // 单字符符号直接匹配
                case '=' -> new Token(TokenType.EQUAL, "=", currentLine, currentCol);
                case '+' -> new Token(TokenType.PLUS, "+", currentLine, currentCol);
                case '-' -> new Token(TokenType.MINUS, "-", currentLine, currentCol);
                case '*' -> new Token(TokenType.MULTIPLY, "*", currentLine, currentCol);
                case '/' -> new Token(TokenType.DIVIDE, "/", currentLine, currentCol);
                case ';' -> new Token(TokenType.SEMICOLON, ";", currentLine, currentCol);
                case ',' -> new Token(TokenType.COMMA, ",", currentLine, currentCol);
                case '(' -> new Token(TokenType.LEFT_PAREN, "(", currentLine, currentCol);
                case ')' -> new Token(TokenType.RIGHT_PAREN, ")", currentLine, currentCol);
                case '{' -> new Token(TokenType.LEFT_BRACE, "{", currentLine, currentCol);
                case '}' -> new Token(TokenType.RIGHT_BRACE, "}", currentLine, currentCol);
                // 处理未知字符异常
                default ->
                        throw new RuntimeException("非法字符: " + currentChar + " 位于行 " + currentLine + ":" + currentCol);
            };

            advance();  // 移动指针到符号之后
            return token;
        }

        // 输入结束返回EOF标记
        return new Token(TokenType.EOF, "", line, column);
    }
}