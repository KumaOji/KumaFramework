package com.kuma.cloud.lab.jdk;

import java.util.List;

/** Representative release features, all run on JDK 25; version denotes feature introduction. */
public final class JdkVersionLearningDemo {
    public static final List<Integer> VERSIONS = List.of(8, 9, 10, 11, 12, 14, 15, 16, 17, 18, 21, 22, 24, 25);
    private JdkVersionLearningDemo() { }

    public static void main(String[] args) throws Exception {
        if (Runtime.version().feature() != 25)
            throw new IllegalStateException("这些实验按 JDK 25 编译与验证，请使用 JDK 25。");
        if (args.length > 1) throw new IllegalArgumentException("Usage: JdkVersionLearningDemo [all|version]");
        List<Integer> selected = args.length == 0 || args[0].equals("all")
                ? VERSIONS : List.of(Integer.parseInt(args[0]));
        for (int version : selected) {
            check(VERSIONS.contains(version), "未选择该版本的专项实验，可用版本：" + VERSIONS);
            System.out.println("--- JDK " + version + " feature lab (runtime JDK 25) ---");
            switch (version) {
                case 8 -> EarlyJdkLessons.jdk8();
                case 9 -> EarlyJdkLessons.jdk9();
                case 10 -> EarlyJdkLessons.jdk10();
                case 11 -> EarlyJdkLessons.jdk11();
                case 12 -> EarlyJdkLessons.jdk12();
                case 14 -> LanguageLessons.jdk14();
                case 15 -> LanguageLessons.jdk15();
                case 16 -> LanguageLessons.jdk16();
                case 17 -> LanguageLessons.jdk17();
                case 18 -> RecentJdkLessons.jdk18();
                case 21 -> RecentJdkLessons.jdk21();
                case 22 -> RecentJdkLessons.jdk22();
                case 24 -> RecentJdkLessons.jdk24();
                case 25 -> Jdk25Lesson.run();
                default -> throw new IllegalArgumentException("unsupported version");
            }
        }
        System.out.println("JDK 功能实验通过：" + selected);
    }

    static void check(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }

    static void expect(Class<? extends Throwable> type, Runnable action) {
        try { action.run(); }
        catch (Throwable failure) {
            if (type.isInstance(failure)) return;
            throw new IllegalStateException("预期 " + type.getName() + "，实际 " + failure, failure);
        }
        throw new IllegalStateException("未抛出预期异常：" + type.getName());
    }
}
