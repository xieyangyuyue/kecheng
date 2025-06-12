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
        // 基础关键字
        KEYWORDS.put("public", TokenType.PUBLIC);
        KEYWORDS.put("class", TokenType.CLASS);
        KEYWORDS.put("static", TokenType.STATIC);
        KEYWORDS.put("void", TokenType.VOID);
        KEYWORDS.put("int", TokenType.INT);

        // 新增关键字
        KEYWORDS.put("main", TokenType.MAIN);
        KEYWORDS.put("char", TokenType.CHAR);
        KEYWORDS.put("printf", TokenType.PRINTF);
        KEYWORDS.put("scanf", TokenType.SCANF);
        KEYWORDS.put("switch", TokenType.SWITCH);
        KEYWORDS.put("case", TokenType.CASE);
        KEYWORDS.put("default", TokenType.DEFAULT);
        KEYWORDS.put("for", TokenType.FOR);
        KEYWORDS.put("if", TokenType.IF);
        KEYWORDS.put("else", TokenType.ELSE);
        KEYWORDS.put("while", TokenType.WHILE);
        KEYWORDS.put("do", TokenType.DO);
        KEYWORDS.put("return", TokenType.RETURN);
        KEYWORDS.put("break", TokenType.BREAK);
        KEYWORDS.put("continue", TokenType.CONTINUE);
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
    private char peek() {
        if (pos + 1 >= input.length()) {
            return '\0';
        } else {
            return input.charAt(pos + 1);
        }
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

    // 处理字符串字面量（带转义和闭合检查）
    private Token readString() {
        int startLine = line;
        int startCol = column;
        advance(); // 跳过开始的双引号

        StringBuilder sb = new StringBuilder();
        while (currentChar != '"' && currentChar != '\0') {
            if (currentChar == '\\') {
                advance(); // 处理转义字符
                switch (currentChar) {
                    case 'n' -> sb.append('\n');
                    case 't' -> sb.append('\t');
                    case 'r' -> sb.append('\r');
                    case '"' -> sb.append('"');
                    case '\\' -> sb.append('\\');
                    default -> throw new RuntimeException("无效转义字符: \\" + currentChar
                            + " 在行 " + line + ":" + column);
                }
                advance();
            } else {
                sb.append(currentChar);
                advance();
            }
        }

        if (currentChar != '"') {
            throw new RuntimeException("字符串未闭合，起始于行 " + startLine + ":" + startCol);
        }
        advance(); // 跳过闭合的双引号
        return new Token(TokenType.STRING, sb.toString(), startLine, startCol);
    }

    // 处理字符字面量（带转义和闭合检查）
    private Token readChar() {
        int startLine = line;
        int startCol = column;
        advance(); // 跳过开始的单引号

        char value;
        if (currentChar == '\\') { // 处理转义字符
            advance();
            value = switch (currentChar) {
                case 'n' -> '\n';
                case 't' -> '\t';
                case 'r' -> '\r';
                case '\'' -> '\'';
                case '\\' -> '\\';
                default -> throw new RuntimeException("无效转义字符: \\" + currentChar
                        + " 在行 " + line + ":" + column);
            };
            advance();
        } else {
            value = currentChar;
            advance();
        }

        if (currentChar != '\'') {
            throw new RuntimeException("字符未闭合，起始于行 " + startLine + ":" + startCol);
        }
        advance(); // 跳过闭合的单引号
        return new Token(TokenType.CHAR_CONST, String.valueOf(value), startLine, startCol);
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

            // 处理字符串字面量
            if (currentChar == '"') {
                return readString();
            }

            // 处理字符字面量
            if (currentChar == '\'') {
                return readChar();
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
            int currentLine = line;
            int currentCol = column;

            Token token = switch (currentChar) {
                case '=' -> {
                    if (peek() == '=') {
                        advance(); // 消耗当前'='
                        advance(); // 消耗下一个'='
                        yield new Token(TokenType.EQUAL, "==", currentLine, currentCol);
                    } else {
                        advance();
                        yield new Token(TokenType.ASSIGN, "=", currentLine, currentCol);
                    }
                }
                case '!' -> {
                    if (peek() == '=') {
                        advance();
                        advance();
                        yield new Token(TokenType.NOTEQ, "!=", currentLine, currentCol);
                    } else {
                        advance();
                        yield new Token(TokenType.NOT, "!", currentLine, currentCol);
                    }
                }
                case '<' -> {
                    if (peek() == '=') {
                        advance();
                        advance();
                        yield new Token(TokenType.LESS_EQUAL, "<=", currentLine, currentCol);
                    } else {
                        advance();
                        yield new Token(TokenType.LESS, "<", currentLine, currentCol);
                    }
                }
                case '>' -> {
                    if (peek() == '=') {
                        advance();
                        advance();
                        yield new Token(TokenType.GREAT_EQUAL, ">=", currentLine, currentCol);
                    } else {
                        advance();
                        yield new Token(TokenType.GREAT, ">", currentLine, currentCol);
                    }
                }
                case '+' -> new Token(TokenType.PLUS, "+", currentLine, currentCol);
                case '-' -> new Token(TokenType.MINUS, "-", currentLine, currentCol);
                case '*' -> new Token(TokenType.MULTIPLY, "*", currentLine, currentCol);
                case ':' -> new Token(TokenType.COLON, ":", currentLine, currentCol);
                case '/' -> new Token(TokenType.DIVIDE, "/", currentLine, currentCol);
                case ';' -> new Token(TokenType.SEMICOLON, ";", currentLine, currentCol);
                case ',' -> new Token(TokenType.COMMA, ",", currentLine, currentCol);
                case '(' -> new Token(TokenType.LEFT_PAREN, "(", currentLine, currentCol);
                case ')' -> new Token(TokenType.RIGHT_PAREN, ")", currentLine, currentCol);
                case '{' -> new Token(TokenType.LEFT_BRACE, "{", currentLine, currentCol);
                case '}' -> new Token(TokenType.RIGHT_BRACE, "}", currentLine, currentCol);
                case '[' -> new Token(TokenType.LEFT_BRACK, "[", currentLine, currentCol);
                case ']' -> new Token(TokenType.RIGHT_BRACK, "]", currentLine, currentCol);
                default -> throw new RuntimeException("非法字符: " + currentChar + " 位于行 " + currentLine + ":" + currentCol);
            };

            advance();
            return token;
        }


        // 输入结束返回EOF标记
        return new Token(TokenType.EOF, "", line, column);
    }
}