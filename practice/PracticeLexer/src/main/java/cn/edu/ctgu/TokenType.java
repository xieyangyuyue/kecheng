package cn.edu.ctgu;

/**
 * 定义所有词法单元类型的枚举（根据C语言词法分析需求调整）
 * 每个枚举值关联输出代码（括号中的名称）
 */
public enum TokenType {
    // 关键字类型（对应文档2中的TK结尾）
    PUBLIC("PUBLIC"),
    CLASS("CLASS"),
    STATIC("STATIC"),
    VOID("VOIDTK"),
    MAIN("MAINTK"),
    CHAR("CHARTK"),
    INT("INTTK"),
    PRINTF("PRINTFTK"),
    SCANF("SCANFTK"),
    SWITCH("SWITCHTK"),
    CASE("CASETK"),
    DEFAULT("DEFAULTTK"),
    FOR("FORTK"),
    IF("IFTK"),
    ELSE("ELSETK"),
    WHILE("WHILETK"),
    DO("DOTK"),
    RETURN("RETURNTK"),
    BREAK("BREAKTK"),
    CONTINUE("CONTINUETK"),

    // 标识符和字面量
    IDENTIFIER("IDENFR"),
    STRING("STRCON"),
    CHAR_CONST("CHARCON"),
    NUMBER("INTCON"),

    // 运算符和边界符（对应文档2中的符号）
    ASSIGN("ASSIGN"),
    PLUS("PLUS"),
    MINUS("MINU"),
    MULTIPLY("MULT"),
    DIVIDE("DIV"),

    // 关系运算符
    LESS("LSS"),
    LESS_EQUAL("LEQ"),
    LESSEQ("LEQ"),
    GREAT("GRE"),
    GREAT_EQUAL("GREAT_EQUAL"),
    GREATEQ("GEQ"),
    NOTEQ("NEQ"),
    NOT("NOT"),
    EQUAL("EQL"),


    // 分隔符
    COMMA("COMMA"),
    SEMICOLON("SEMICN"),
    COLON("COLON"),
    LEFT_PAREN("LPARENT"),
    RIGHT_PAREN("RPARENT"),
    LEFT_BRACE("LBRACE"),
    RIGHT_BRACE("RBRACE"),
    LEFT_BRACK("LBRACK"),
    RIGHT_BRACK("RBRACK"),

    // 特殊标记
    EOF("EOF");

    private final String code;

    TokenType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}