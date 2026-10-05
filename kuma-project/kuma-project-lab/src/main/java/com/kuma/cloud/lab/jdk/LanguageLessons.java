package com.kuma.cloud.lab.jdk;

import java.util.List;

/** Permanent versions: switch expressions 14, text blocks 15, records/instanceof patterns 16, sealed 17. */
final class LanguageLessons {
    private LanguageLessons() { }

    static void jdk14() {
        String day = "MON";
        int hours = switch (day) {
            case "SAT", "SUN" -> 0;
            default -> { int standard = 8; yield standard; }
        };
        JdkVersionLearningDemo.check(hours == 8, "switch expression/yield 错误");
        System.out.println("switch 表达式：箭头分支、多个标签、yield 通过。");
    }

    static void jdk15() {
        String block = """
                alpha
                  beta
                """;
        JdkVersionLearningDemo.check(block.equals("alpha\n  beta\n"), "文本块缩进或末尾换行错误");
        System.out.println("Text blocks：公共缩进移除与末尾换行检查通过。");
    }

    record User(String name, int age) {
        User {
            if (name == null || name.isBlank() || age < 0) throw new IllegalArgumentException("invalid user");
        }
    }

    static void jdk16() {
        Object value = new User("Kuma", 25);
        JdkVersionLearningDemo.check(value instanceof User user && user.age() == 25, "instanceof 模式匹配错误");
        JdkVersionLearningDemo.check(value.equals(new User("Kuma", 25)), "record 值相等错误");
        JdkVersionLearningDemo.expect(IllegalArgumentException.class, () -> new User("Kuma", -1));
        List<String> list = java.util.stream.Stream.of("a", "b").toList();
        JdkVersionLearningDemo.expect(UnsupportedOperationException.class, () -> list.add("c"));
        System.out.println("record 访问器/equals/紧凑构造器、instanceof 模式匹配、Stream.toList 不可修改通过。");
    }

    sealed interface Shape permits Circle, Rectangle { }
    record Circle(double radius) implements Shape { }
    record Rectangle(double width, double height) implements Shape { }

    static void jdk17() {
        JdkVersionLearningDemo.check(Shape.class.isSealed(), "sealed 元数据错误");
        JdkVersionLearningDemo.check(Shape.class.getPermittedSubclasses().length == 2, "允许子类数量错误");
        System.out.println("sealed：继承边界与 permitted subclasses 元数据检查通过；穷举模式 switch 见 JDK 21。");
    }
}
