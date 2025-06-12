package cn.edu.ctgu;// Token.java

/**
 * 词法单元封装类
 * 包含四个核心属性：
 * - type: 词法单元类型（来自TokenType枚举）
 * - value: 词素原始字符串值
 * - line: 所在行号（从1开始）
 * - column: 所在列号（从1开始）
 */

public record Token(TokenType type, String value, int line, int column) {
    // 全参数构造函数

    // 格式化输出方法（用于调试）
    @Override
    public String toString() {
        return String.format("Token(%s, '%s') at %d:%d",
                type, value, line, column);
    }
}