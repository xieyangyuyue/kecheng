package cn.edu.ctgu;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

class LexerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LexerTest.class);

    @Test
    void testKeywords() {
        Lexer lexer = new Lexer("public class static void int");
        assertToken(lexer.nextToken(), TokenType.PUBLIC, "public", 1, 1);
        assertToken(lexer.nextToken(), TokenType.CLASS, "class", 1, 8);
        assertToken(lexer.nextToken(), TokenType.STATIC, "static", 1, 14);
        assertToken(lexer.nextToken(), TokenType.VOID, "void", 1, 21);
        assertToken(lexer.nextToken(), TokenType.INT, "int", 1, 26);
        assertToken(lexer.nextToken(), TokenType.EOF, "", 1, 29);
    }

    @Test
    void testIdentifiers() {
        Lexer lexer = new Lexer("variableName test123");

        assertToken(lexer.nextToken(), TokenType.IDENTIFIER, "variableName", 1, 1);
        assertToken(lexer.nextToken(), TokenType.IDENTIFIER, "test123", 1, 14);
    }

    @Test
    void testNumbers() {
        Lexer lexer = new Lexer("123 456");

        assertToken(lexer.nextToken(), TokenType.NUMBER, "123", 1, 1);
        assertToken(lexer.nextToken(), TokenType.NUMBER, "456", 1, 5);
    }

    @Test
    void testOperators() {
        Lexer lexer = new Lexer("= + - * /");

        assertToken(lexer.nextToken(), TokenType.EQUAL, "=", 1, 1);
        assertToken(lexer.nextToken(), TokenType.PLUS, "+", 1, 3);
        assertToken(lexer.nextToken(), TokenType.MINUS, "-", 1, 5);
        assertToken(lexer.nextToken(), TokenType.MULTIPLY, "*", 1, 7);
        assertToken(lexer.nextToken(), TokenType.DIVIDE, "/", 1, 9);
    }

    @Test
    void testDelimiters() {
        Lexer lexer = new Lexer("( ) { } ; ,");

        assertToken(lexer.nextToken(), TokenType.LEFT_PAREN, "(", 1, 1);
        assertToken(lexer.nextToken(), TokenType.RIGHT_PAREN, ")", 1, 3);
        assertToken(lexer.nextToken(), TokenType.LEFT_BRACE, "{", 1, 5);
        assertToken(lexer.nextToken(), TokenType.RIGHT_BRACE, "}", 1, 7);
        assertToken(lexer.nextToken(), TokenType.SEMICOLON, ";", 1, 9);
        assertToken(lexer.nextToken(), TokenType.COMMA, ",", 1, 11);
    }

    @Test
    void testPositionTracking() {
        Lexer lexer = new Lexer("a\n 123\nb");

        assertToken(lexer.nextToken(), TokenType.IDENTIFIER, "a", 1, 1);
        assertToken(lexer.nextToken(), TokenType.NUMBER, "123", 2, 2);
        assertToken(lexer.nextToken(), TokenType.IDENTIFIER, "b", 3, 1);
    }

    @Test
    void testInvalidCharacter() {
        Lexer lexer = new Lexer("~");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                lexer::nextToken
        );
        LOGGER.debug(exception.getMessage());
        assertTrue(exception.getMessage().contains("非法字符"));
        assertTrue(exception.getMessage().contains("位于行 1:1"));
    }


    @Test
    void testFullSample() {
        String input = """
                public class Main {
                    void method() {
                        int n = 42;
                    }
                }""";

        Lexer lexer = new Lexer(input);

        assertToken(lexer.nextToken(), TokenType.PUBLIC, "public", 1, 1);
        assertToken(lexer.nextToken(), TokenType.CLASS, "class", 1, 8);
        assertToken(lexer.nextToken(), TokenType.IDENTIFIER, "Main", 1, 14);
        assertToken(lexer.nextToken(), TokenType.LEFT_BRACE, "{", 1, 19);

    }

    @Test
    void testEmptyInput() {
        Lexer lexer = new Lexer("");
        assertToken(lexer.nextToken(), TokenType.EOF, "", 1, 1);
    }

    private void assertToken(Token token, TokenType expectedType,
                             String expectedValue, int expectedLine,
                             int expectedColumn) {
        assertEquals(expectedType, token.type());
        assertEquals(expectedValue, token.value());
        assertEquals(expectedLine, token.line());
        assertEquals(expectedColumn, token.column());
    }
}