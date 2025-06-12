package cn.edu.ctgu;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertLinesMatch;

public class LexerFileTest {

    @Test
    void testLexerAgainstFile() throws IOException {
        for (int i=1;i<=3;i++) {
            // 读取输入文件
            String input = Files.readString(Paths.get("src/test/resources/testfile"+i+".txt"));
            Lexer lexer = new Lexer(input);

            // 生成Token列表
            List<String> actualOutput = new ArrayList<>();
            Token token;
            do {
                token = lexer.nextToken();
                String line = String.format(" %-8s %s", token.type().getCode(), token.value());
                actualOutput.add(line);
            } while (token.type() != TokenType.EOF);

            // 读取预期输出
            List<String> expectedOutput = Files.readAllLines(Paths.get("src/test/resources/output"+i+".txt"));

            // 测试用例中
            List<String> expectedClean = normalizeLines(expectedOutput);
            List<String> actualClean = normalizeLines(actualOutput);
            assertLinesMatch(expectedClean, actualClean);
        }

    }

    // 预处理方法
    private List<String> normalizeLines(List<String> lines) {
        return lines.stream()
                .map(line -> line.replaceAll("\\s+", "")) // 移除所有空白
                .collect(Collectors.toList());
    }


}