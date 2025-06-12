package cn.edu.ctgu;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class PhoneNumberGenerator {
    // 手机号开头号段（常见号段）
    private static final String[] PREFIXES = { "133", "135", "136", "137", "138", "139", 
                                              "150", "151", "155", "156", "157", "158", "159",
                                              "180", "181", "185", "186", "187", "188", "189",
                                              "199" };
    private static final int TOTAL_NUMBERS = 500;

    public static void main(String[] args) {
        Set<String> phoneNumbers = generatePhoneNumbers();
        
        // 打印前10个示例
        System.out.println("生成的电话号码示例（前10个）:");
        phoneNumbers.stream().limit(500).forEach(System.out::println);
    }

    /**
     * 生成不重复的随机手机号码
     */
    private static Set<String> generatePhoneNumbers() {
        Random random = new Random();
        Set<String> numbers = new HashSet<>(TOTAL_NUMBERS);

        while (numbers.size() < TOTAL_NUMBERS) {
            // 随机选择号段
            String prefix = PREFIXES[random.nextInt(PREFIXES.length)];
            
            // 生成后8位数字
            StringBuilder suffix = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                suffix.append(random.nextInt(10));
            }
            
            numbers.add(prefix + suffix);
        }
        return numbers;
    }
}