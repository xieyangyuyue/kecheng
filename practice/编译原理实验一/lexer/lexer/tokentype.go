package lexer

type TokenType int

const (
	// PUBLIC Keywords
	PUBLIC TokenType = iota
	CLASS
	STATIC
	VOID
	MAIN
	CHAR
	INT
	PRINTF
	SCANF
	SWITCH
	CASE
	DEFAULT
	FOR
	IF
	ELSE
	WHILE
	DO
	RETURN
	BREAK
	CONTINUE

	// IDENTIFIER Identifiers and literals
	IDENTIFIER
	STRING
	CharConst
	NUMBER

	// ASSIGN Operators
	ASSIGN
	PLUS
	MINUS
	MULTIPLY
	DIVIDE
	LESS
	LessEqual
	GREAT
	GreatEqual
	NOTEQ
	NOT
	EQUAL

	// COMMA Delimiters
	COMMA
	SEMICOLON
	COLON
	LeftParen
	RightParen
	LeftBrace
	RightBrace
	LeftBrack
	RightBrack

	// EOF Special
	EOF
)

var tokenTypeCodes = map[TokenType]string{
	PUBLIC:     "PUBLIC",
	CLASS:      "CLASS",
	STATIC:     "STATIC",
	VOID:       "VOIDTK",
	MAIN:       "MAINTK",
	CHAR:       "CHARTK",
	INT:        "INTTK",
	PRINTF:     "PRINTFTK",
	SCANF:      "SCANFTK",
	SWITCH:     "SWITCHTK",
	CASE:       "CASETK",
	DEFAULT:    "DEFAULTTK",
	FOR:        "FORTK",
	IF:         "IFTK",
	ELSE:       "ELSETK",
	WHILE:      "WHILETK",
	DO:         "DOTK",
	RETURN:     "RETURNTK",
	BREAK:      "BREAKTK",
	CONTINUE:   "CONTINUETK",
	IDENTIFIER: "IDENFR",
	STRING:     "STRCON",
	CharConst:  "CHARCON",
	NUMBER:     "INTCON",
	ASSIGN:     "ASSIGN",
	PLUS:       "PLUS",
	MINUS:      "MINU",
	MULTIPLY:   "MULT",
	DIVIDE:     "DIV",
	LESS:       "LSS",
	LessEqual:  "LEQ",
	GREAT:      "GRE",
	GreatEqual: "GEQ",
	NOTEQ:      "NEQ",
	NOT:        "NOT",
	EQUAL:      "EQL",
	COMMA:      "COMMA",
	SEMICOLON:  "SEMICN",
	COLON:      "COLON",
	LeftParen:  "LPARENT",
	RightParen: "RPARENT",
	LeftBrace:  "LBRACE",
	RightBrace: "RBRACE",
	LeftBrack:  "LBRACK",
	RightBrack: "RBRACK",
	EOF:        "EOF",
}

func (tt TokenType) Code() string {
	return tokenTypeCodes[tt]
}
