package cn.edu.ctgu;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomStringGenerator {
    // 可选的字符集（大小写字母 + 数字）
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int STRING_LENGTH = 7;
    private static final int TOTAL_STRINGS = 500;

    public static void main(String[] args) {
        List<String> randomStrings = generateRandomStrings();
        
        // 打印前10个结果作为示例
        System.out.println("生成示例（前10个）:");
        for (int i = 0; i < 500; i++) {
            System.out.println(randomStrings.get(i));
        }
    }

    /**
     * 生成500个7位随机字符串
     */
    private static List<String> generateRandomStrings() {
        Random random = new Random();
        List<String> strings = new ArrayList<>(TOTAL_STRINGS);

        for (int i = 0; i < TOTAL_STRINGS; i++) {
            StringBuilder sb = new StringBuilder(STRING_LENGTH);
            for (int j = 0; j < STRING_LENGTH; j++) {
                // 从字符集中随机选择一个字符
                int randomIndex = random.nextInt(CHARACTERS.length());
                sb.append(CHARACTERS.charAt(randomIndex));
            }
            strings.add(sb.toString());
        }
        return strings;
    }
}