package lexer_test

import (
	"github.com/stretchr/testify/assert"
	"lexer/lexer"
	"os"
	"path/filepath"
	"strings"
	"testing"
)

func TestLexer(t *testing.T) {
	testCases := []struct {
		name       string
		testFile   string
		expectFile string
	}{
		{"Testcase1", "testfile1.txt", "output1.txt"},
		{"Testcase2", "testfile2.txt", "output2.txt"},
		{"Testcase3", "testfile3.txt", "output3.txt"},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			// Read input
			input := readFile(t, filepath.Join("testdata", tc.testFile))

			// Process tokens
			l := lexer.New(input)
			var tokens []string
			for {
				token := l.NextToken()
				tokens = append(tokens, token.String())
				if token.Type == lexer.EOF {
					break
				}
			}

			// Read expected output
			expected := readFileLines(t, filepath.Join("testdata", tc.expectFile))

			// Compare results
			assert.Equal(t, normalize(expected), normalize(tokens), "Test case failed: "+tc.name)
		})
	}
}

func readFile(t *testing.T, path string) string {
	data, err := os.ReadFile(path)
	if err != nil {
		t.Fatalf("Error reading file: %v", err)
	}
	return string(data)
}

func readFileLines(t *testing.T, path string) []string {
	content := readFile(t, path)
	return strings.Split(strings.TrimSpace(content), "\n")
}

func normalize(lines []string) []string {
	normalized := make([]string, 0, len(lines))
	for _, line := range lines {
		trimmed := strings.TrimSpace(line)
		if trimmed != "" {
			normalized = append(normalized, strings.Join(strings.Fields(trimmed), " "))
		}
	}
	return normalized
}
