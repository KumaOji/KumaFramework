package com.kuma.cloud.lab.jdk;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** JDK 8-12 API and language features; introduction versions are not compilation targets. */
final class EarlyJdkLessons {
    private EarlyJdkLessons() { }

    static void jdk8() throws Exception {
        List<Integer> result = Stream.of(1, 2, 3, 4).filter(n -> n % 2 == 0)
                .map(n -> n * n).collect(Collectors.toList());
        JdkVersionLearningDemo.check(result.equals(List.of(4, 16)), "Lambda/Stream 结果错误");
        String optional = Optional.<String>empty().orElseGet(() -> "fallback");
        JdkVersionLearningDemo.check(optional.equals("fallback"), "Optional 默认值错误");
        JdkVersionLearningDemo.check(LocalDate.of(2024, 2, 28).plusDays(1).getDayOfMonth() == 29, "java.time 闰年错误");
        var executor = java.util.concurrent.Executors.newSingleThreadExecutor();
        try {
            int answer = CompletableFuture.supplyAsync(() -> 20, executor).thenApply(n -> n * 2)
                    .thenCombine(CompletableFuture.completedFuture(2), Integer::sum).get(5, TimeUnit.SECONDS);
            JdkVersionLearningDemo.check(answer == 42, "CompletableFuture 组合错误");
        } finally {
            executor.shutdownNow();
            JdkVersionLearningDemo.check(executor.awaitTermination(5, TimeUnit.SECONDS), "异步线程退出超时");
        }
        System.out.println("Lambda / Stream / Optional / java.time / CompletableFuture 通过。");
    }

    static void jdk9() {
        List<String> fixed = List.of("a", "b");
        JdkVersionLearningDemo.expect(UnsupportedOperationException.class, () -> fixed.add("c"));
        JdkVersionLearningDemo.expect(NullPointerException.class, () -> List.of("a", null));
        List<Integer> prefix = Stream.of(1, 2, 5, 3).takeWhile(n -> n < 4).toList();
        List<Integer> tail = Stream.of(1, 2, 5, 3).dropWhile(n -> n < 4).toList();
        JdkVersionLearningDemo.check(prefix.equals(List.of(1, 2)) && tail.equals(List.of(5, 3)), "take/dropWhile 是连续前缀操作");
        Module base = String.class.getModule();
        JdkVersionLearningDemo.check(base.isNamed() && base.getName().equals("java.base"), "核心类所属模块错误");
        System.out.println("集合工厂的不可修改/null 边界、Stream 前缀操作、模块归属通过。");
    }

    static void jdk10() {
        var original = new ArrayList<String>(); // var is compile-time inference, not dynamic typing
        original.add("before");
        var snapshot = List.copyOf(original);
        original.add("after");
        JdkVersionLearningDemo.check(snapshot.equals(List.of("before")), "copyOf 快照不应随后变化");
        JdkVersionLearningDemo.expect(UnsupportedOperationException.class, () -> snapshot.clear());
        System.out.println("var 局部类型推断、List.copyOf 快照通过（元素仍可能是可变对象）。");
    }

    static void jdk11() throws IOException {
        JdkVersionLearningDemo.check("  \n".isBlank() && " x ".strip().equals("x"), "String blank/strip 错误");
        JdkVersionLearningDemo.check("ab".repeat(3).equals("ababab"), "repeat 错误");
        JdkVersionLearningDemo.check("a\nb".lines().count() == 2, "lines 错误");
        var file = Files.createTempFile("jdk11-lab-", ".txt");
        try {
            Files.writeString(file, "JDK 11：UTF-8 文本");
            JdkVersionLearningDemo.check(Files.readString(file).equals("JDK 11：UTF-8 文本"), "Files 字符串读写错误");
        } finally { Files.deleteIfExists(file); }
        System.out.println("String 新 API / Files.readString、writeString 通过；HTTP Client 实验见 JDK 18 场景及 network/WebhookLesson。");
    }

    static void jdk12() {
        record Summary(long count, int total) { }
        Summary result = Stream.of(1, 2, 3).collect(Collectors.teeing(
                Collectors.counting(), Collectors.summingInt(Integer::intValue), Summary::new));
        JdkVersionLearningDemo.check(result.equals(new Summary(3, 6)), "teeing 双路汇总错误");
        System.out.println("Collectors.teeing：一次收集组合 count=3、sum=6 通过（示例 record 是后续版本语法）。");
    }
}
