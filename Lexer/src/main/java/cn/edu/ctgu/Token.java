package cn.edu.ctgu;// Token.java

/**
 * 词法单元封装类
 * 包含四个核心属性：
 * - type: 词法单元类型（来自TokenType枚举）
 * - value: 词素原始字符串值
 * - line: 所在行号（从1开始）
 * - column: 所在列号（从1开始）
 */
/*
public class Token {
    private final TokenType type;
    private final String value;
    private final int line;
    private final int column;

    // 全参数构造函数
    public Token(TokenType type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    // Getter方法（省略setter保证不可变性）
    public TokenType getType() { return type; }
    public String getValue() { return value; }
    public int getLine() { return line; }
    public int getColumn() { return column; }

    // 格式化输出方法（用于调试）
    @Override
    public String toString() {
        return String.format("Token(%s, '%s') at %d:%d",
                type, value, line, column);
    }
}*/
public record Token(TokenType type, String value, int line, int column) {
    // 全参数构造函数

    // 格式化输出方法（用于调试）
    @Override
    public String toString() {
        return String.format("Token(%s, '%s') at %d:%d",
                type, value, line, column);
    }
}