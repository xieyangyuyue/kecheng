package lexer

import (
	"fmt"
	"unicode"
)

type Lexer struct {
	input       string
	pos         int
	line        int
	column      int
	currentChar rune
}

// 新增反向映射表（放在文件顶部）
var reverseKeywords = map[string]TokenType{
	"public":   PUBLIC,
	"class":    CLASS,
	"static":   STATIC,
	"void":     VOID,
	"main":     MAIN,
	"char":     CHAR,
	"int":      INT,
	"printf":   PRINTF,
	"scanf":    SCANF,
	"switch":   SWITCH,
	"case":     CASE,
	"default":  DEFAULT,
	"for":      FOR,
	"if":       IF,
	"else":     ELSE,
	"while":    WHILE,
	"do":       DO,
	"return":   RETURN,
	"break":    BREAK,
	"continue": CONTINUE,
}

func New(input string) *Lexer {
	l := &Lexer{
		input:  input,
		line:   1,
		column: 1,
	}
	if len(input) > 0 {
		l.currentChar = rune(input[0])
	}
	return l
}

func (l *Lexer) advance() {
	if l.currentChar == '\n' {
		l.line++
		l.column = 0
	}
	l.pos++
	if l.pos >= len(l.input) {
		l.currentChar = 0
	} else {
		l.currentChar = rune(l.input[l.pos])
	}
	l.column++
}

func (l *Lexer) skipWhitespace() {
	for unicode.IsSpace(l.currentChar) {
		l.advance()
	}
}

func (l *Lexer) readIdentifier() Token {
	startPos := l.pos
	startLine := l.line
	startCol := l.column

	for unicode.IsLetter(l.currentChar) || unicode.IsDigit(l.currentChar) {
		l.advance()
	}

	// 修正后的关键字识别逻辑
	value := l.input[startPos:l.pos]
	tokenType := IDENTIFIER
	if code, exists := reverseKeywords[value]; exists {
		tokenType = code
	}
	return Token{tokenType, value, startLine, startCol}
}

func (l *Lexer) readNumber() Token {
	startPos := l.pos
	startLine := l.line
	startCol := l.column

	for unicode.IsDigit(l.currentChar) {
		l.advance()
	}

	value := l.input[startPos:l.pos]
	return Token{NUMBER, value, startLine, startCol}
}

// 新增字符字面量解析方法
func (l *Lexer) readChar() Token {
	startLine := l.line
	startCol := l.column
	l.advance() // 跳过开始的单引号

	var value string
	if l.currentChar == '\\' { // 处理转义字符
		l.advance()
		switch l.currentChar {
		case 'n':
			value = "\n"
		case 't':
			value = "\t"
		case 'r':
			value = "\r"
		case '\'':
			value = "'"
		case '\\':
			value = "\\"
		default:
			panic(fmt.Sprintf("无效转义字符: \\%c 在行 %d:%d", l.currentChar, l.line, l.column))
		}
		l.advance()
	} else {
		value = string(l.currentChar)
		l.advance()
	}

	if l.currentChar != '\'' {
		panic(fmt.Sprintf("字符未闭合，起始于行 %d:%d", startLine, startCol))
	}
	l.advance() // 跳过闭合的单引号
	return Token{CharConst, value, startLine, startCol}
}
func (l *Lexer) readString() Token {
	startLine := l.line
	startCol := l.column
	l.advance() // Skip opening "

	var value string
	for l.currentChar != '"' && l.currentChar != 0 {
		if l.currentChar == '\\' {
			l.advance()
			switch l.currentChar {
			case 'n':
				value += "\n"
			case 't':
				value += "\t"
			case 'r':
				value += "\r"
			case '"':
				value += "\""
			case '\\':
				value += "\\"
			default:
				panic(fmt.Sprintf("Invalid escape: \\%c at %d:%d", l.currentChar, l.line, l.column))
			}
			l.advance()
		} else {
			value += string(l.currentChar)
			l.advance()
		}
	}

	if l.currentChar != '"' {
		panic(fmt.Sprintf("Unclosed string at %d:%d", startLine, startCol))
	}
	l.advance() // Skip closing "
	return Token{STRING, value, startLine, startCol}
}

func (l *Lexer) NextToken() Token {
	for l.currentChar != 0 {
		if unicode.IsSpace(l.currentChar) {
			l.skipWhitespace()
			continue
		}
		// 新增字符字面量处理
		if l.currentChar == '\'' {
			return l.readChar()
		}

		if l.currentChar == '"' {
			return l.readString()
		}

		if unicode.IsLetter(l.currentChar) {
			return l.readIdentifier()
		}

		if unicode.IsDigit(l.currentChar) {
			return l.readNumber()
		}

		// Handle operators and symbols
		currentChar := l.currentChar
		currentLine := l.line
		currentCol := l.column
		l.advance()

		switch currentChar {
		case '=':
			if l.currentChar == '=' {
				l.advance()
				return Token{EQUAL, "==", currentLine, currentCol}
			}
			return Token{ASSIGN, "=", currentLine, currentCol}
		case '!':
			if l.currentChar == '=' {
				l.advance()
				return Token{NOTEQ, "!=", currentLine, currentCol}
			}
			return Token{NOT, "!", currentLine, currentCol}
		case '<':
			if l.currentChar == '=' {
				l.advance()
				return Token{LessEqual, "<=", currentLine, currentCol}
			}
			return Token{LESS, "<", currentLine, currentCol}
		case '>':
			if l.currentChar == '=' {
				l.advance()
				return Token{GreatEqual, ">=", currentLine, currentCol}
			}
			return Token{GREAT, ">", currentLine, currentCol}
		case '+':
			return Token{PLUS, "+", currentLine, currentCol}
		case '-':
			return Token{MINUS, "-", currentLine, currentCol}
		case '*':
			return Token{MULTIPLY, "*", currentLine, currentCol}
		case '/':
			return Token{DIVIDE, "/", currentLine, currentCol}
		case ';':
			return Token{SEMICOLON, ";", currentLine, currentCol}
		case ',':
			return Token{COMMA, ",", currentLine, currentCol}
		case '(':
			return Token{LeftParen, "(", currentLine, currentCol}
		case ')':
			return Token{RightParen, ")", currentLine, currentCol}
		case '{':
			return Token{LeftBrace, "{", currentLine, currentCol}
		case '}':
			return Token{RightBrace, "}", currentLine, currentCol}
		case '[':
			return Token{LeftBrack, "[", currentLine, currentCol}
		case ']':
			return Token{RightBrack, "]", currentLine, currentCol}
		case ':':
			return Token{COLON, ":", currentLine, currentCol}
		default:
			panic(fmt.Sprintf("Unexpected character: %c at %d:%d", currentChar, currentLine, currentCol))
		}
	}

	return Token{EOF, "", l.line, l.column}
}
