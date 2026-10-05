package com.kuma.cloud.lab.jdk;

import module java.base;

/** JDK 25 final features; java.base module import replaces individual exported-type imports. */
final class Jdk25Lesson {
    private static final ScopedValue<String> REQUEST = ScopedValue.newInstance();
    private static final AtomicInteger BASE_CALLS = new AtomicInteger();
    private Jdk25Lesson() { }

    static void run() throws Exception {
        JdkVersionLearningDemo.check(!REQUEST.isBound(), "作用域初始应未绑定");
        ScopedValue.where(REQUEST, "outer").run(() -> {
            JdkVersionLearningDemo.check(readRequest().equals("outer"), "作用域读取错误");
            ScopedValue.where(REQUEST, "inner").run(() ->
                    JdkVersionLearningDemo.check(readRequest().equals("inner"), "嵌套重绑定错误"));
            JdkVersionLearningDemo.check(readRequest().equals("outer"), "嵌套退出后应恢复外层");
        });
        JdkVersionLearningDemo.check(!REQUEST.isBound(), "正常退出后绑定应释放");
        JdkVersionLearningDemo.expect(IllegalArgumentException.class, () ->
                ScopedValue.where(REQUEST, "failing").run(() -> { throw new IllegalArgumentException("simulated"); }));
        JdkVersionLearningDemo.check(!REQUEST.isBound(), "异常退出后绑定应释放");
        JdkVersionLearningDemo.expect(NoSuchElementException.class, REQUEST::get);
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var a = executor.submit(() -> ScopedValue.where(REQUEST, "A").call(Jdk25Lesson::readRequest));
            var b = executor.submit(() -> ScopedValue.where(REQUEST, "B").call(Jdk25Lesson::readRequest));
            JdkVersionLearningDemo.check(a.get(5, TimeUnit.SECONDS).equals("A")
                    && b.get(5, TimeUnit.SECONDS).equals("B"), "线程绑定应隔离");
        }
        int before = BASE_CALLS.get();
        JdkVersionLearningDemo.expect(IllegalArgumentException.class, () -> new Person(" "));
        JdkVersionLearningDemo.check(BASE_CALLS.get() == before, "验证失败不应调用父构造器");
        Person person = new Person(" Kuma ");
        JdkVersionLearningDemo.check(person.name.equals("Kuma") && BASE_CALLS.get() == before + 1, "super 之前的处理错误");
        System.out.println("ScopedValue 嵌套/异常清理/线程隔离；module import；super 之前验证与转换通过。");
    }

    private static String readRequest() { return REQUEST.get(); }

    private static class Named {
        final String name;
        Named(String name) { this.name = name; BASE_CALLS.incrementAndGet(); }
    }

    private static final class Person extends Named {
        Person(String raw) {
            String normalized = Objects.requireNonNull(raw).strip();
            if (normalized.isEmpty()) throw new IllegalArgumentException("empty name");
            super(normalized); // JDK25: safe statements are allowed before the constructor invocation
        }
    }
}
