package cn.edu.ctgu;
// TokenType.java

/**
 * 定义所有词法单元类型的枚举
 * 包含：
 * - 关键字类型（PUBLIC, CLASS等）
 * - 标识符
 * - 字面量（目前仅支持数字）
 * - 运算符和分隔符
 * - 特殊EOF标记
 */
public enum TokenType {
    // Java基础关键字（部分示例）
    PUBLIC, CLASS, STATIC, VOID, INT,

    // 用户定义的变量/类名等标识符
    IDENTIFIER,

    // 数字字面量（整数）
    NUMBER,

    // 运算符（算术和赋值）
    EQUAL, PLUS, MINUS, MULTIPLY, DIVIDE,

    /*分隔符和结构符号*/
    //分号
    SEMICOLON, COMMA,
    //括号
    LEFT_PAREN, RIGHT_PAREN,
    //大括号
    LEFT_BRACE, RIGHT_BRACE,

    // 文件结束标记
    EOF
}