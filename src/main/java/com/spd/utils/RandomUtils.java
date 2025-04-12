package com.spd.utils;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadLocalRandom;

public final class RandomUtils {

    private static final String NUMBER_CHARS = "0123456789";
    private static final String LETTER_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ALL_CHARS = NUMBER_CHARS + LETTER_CHARS;

    private RandomUtils() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }

    /**
     * 生成随机整数(包含min和max)
     * @param min 最小值
     * @param max 最大值
     * @return 随机整数
     */
    public static int nextInt(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("最小值不能大于最大值");
        }
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    /**
     * 生成随机整数(0到max-1)
     * @param max 最大值(不包含)
     * @return 随机整数
     */
    public static int nextInt(int max) {
        return ThreadLocalRandom.current().nextInt(max);
    }

    /**
     * 生成随机长整数(包含min和max)
     * @param min 最小值
     * @param max 最大值
     * @return 随机长整数
     */
    public static long nextLong(long min, long max) {
        if (min > max) {
            throw new IllegalArgumentException("最小值不能大于最大值");
        }
        return ThreadLocalRandom.current().nextLong(min, max + 1);
    }

    /**
     * 生成随机双精度浮点数(包含min和max)
     * @param min 最小值
     * @param max 最大值
     * @return 随机双精度浮点数
     */
    public static double nextDouble(double min, double max) {
        if (min > max) {
            throw new IllegalArgumentException("最小值不能大于最大值");
        }
        return ThreadLocalRandom.current().nextDouble() * (max - min) + min;
    }

    /**
     * 生成随机布尔值
     * @return 随机布尔值
     */
    public static boolean nextBoolean() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    /**
     * 生成随机字符串(仅数字)
     * @param length 字符串长度
     * @return 随机字符串
     */
    public static String randomNumeric(int length) {
        return randomString(length, NUMBER_CHARS);
    }

    /**
     * 生成随机字符串(仅字母)
     * @param length 字符串长度
     * @return 随机字符串
     */
    public static String randomAlphabetic(int length) {
        return randomString(length, LETTER_CHARS);
    }

    /**
     * 生成随机字符串(字母和数字)
     * @param length 字符串长度
     * @return 随机字符串
     */
    public static String randomAlphanumeric(int length) {
        return randomString(length, ALL_CHARS);
    }

    /**
     * 从集合中随机选择一个元素
     * @param collection 集合
     * @param <T> 元素类型
     * @return 随机元素
     */
    public static <T> T randomElement(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException("集合不能为空");
        }
        int index = nextInt(collection.size());
        if (collection instanceof List) {
            return ((List<T>) collection).get(index);
        }
        return collection.stream().skip(index).findFirst().orElseThrow();
    }

    /**
     * 从数组中随机选择一个元素
     * @param array 数组
     * @param <T> 元素类型
     * @return 随机元素
     */
    @SafeVarargs
    public static <T> T randomElement(T... array) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException("数组不能为空");
        }
        return array[nextInt(array.length)];
    }

    /**
     * 生成随机字符串
     * @param length 字符串长度
     * @param chars 字符集
     * @return 随机字符串
     */
    private static String randomString(int length, String chars) {
        if (length <= 0) {
            throw new IllegalArgumentException("长度必须大于0");
        }
        if (chars == null || chars.isEmpty()) {
            throw new IllegalArgumentException("字符集不能为空");
        }

        StringBuilder sb = new StringBuilder(length);
        Random random = ThreadLocalRandom.current();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * 生成随机颜色RGB值
     * @return RGB颜色字符串，如"#FF0033"
     */
    public static String randomColor() {
        return String.format("#%06X", ThreadLocalRandom.current().nextInt(0x1000000));
    }

    /**
     * 生成随机IP地址
     * @return IP地址字符串
     */
    public static String randomIp() {
        return nextInt(1, 255) + "." + nextInt(0, 255) + "."
                + nextInt(0, 255) + "." + nextInt(1, 254);
    }

    /**
     * 生成随机手机号
     * @return 手机号字符串
     */
    public static String randomPhone() {
        String[] prefixes = {"13", "14", "15", "16", "17", "18", "19"};
        return randomElement(prefixes) + randomNumeric(9);
    }
}
