package lexer

import "fmt"

type Token struct {
    Type   TokenType
    Value  string
    Line   int
    Column int
}

func (t Token) String() string {
    return fmt.Sprintf("%-8s %s", t.Type.Code(), t.Value)
}