package com.kuma.cloud.lab.jdk;

import com.sun.net.httpserver.SimpleFileServer;
import java.lang.classfile.ClassFile;
import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

/** Selected permanent JDK 18, 21, 22 and 24 features, exercised on JDK 25. */
final class RecentJdkLessons {
    private RecentJdkLessons() { }

    static void jdk18() throws Exception {
        var directory = Files.createTempDirectory("jdk18-server-lab-");
        var file = directory.resolve("hello.txt");
        var workers = Executors.newFixedThreadPool(2);
        var server = SimpleFileServer.createFileServer(new InetSocketAddress("127.0.0.1", 0),
                directory, SimpleFileServer.OutputLevel.NONE);
        server.setExecutor(workers);
        try (HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build()) {
            Files.writeString(file, "JDK18 simple server / JDK11 HTTP Client");
            server.start();
            String base = "http://127.0.0.1:" + server.getAddress().getPort();
            var request = HttpRequest.newBuilder(URI.create(base + "/hello.txt")).timeout(Duration.ofSeconds(5)).build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JdkVersionLearningDemo.check(response.statusCode() == 200 && response.body().equals(Files.readString(file)), "文件服务响应错误");
            var missing = HttpRequest.newBuilder(URI.create(base + "/missing.txt")).timeout(Duration.ofSeconds(5)).build();
            JdkVersionLearningDemo.check(client.send(missing, HttpResponse.BodyHandlers.discarding()).statusCode() == 404, "不存在的文件应返回404");
            System.out.println("SimpleFileServer（18）+ HTTP Client（11）：文件请求 200、缺失文件 404 通过。");
        } finally {
            server.stop(0);
            workers.shutdownNow();
            JdkVersionLearningDemo.check(workers.awaitTermination(5, TimeUnit.SECONDS), "文件服务线程退出超时");
            Files.deleteIfExists(file);
            Files.deleteIfExists(directory);
        }
    }

    static void jdk21() throws Exception {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = new ArrayList<java.util.concurrent.Future<Integer>>();
            for (int i = 0; i < 128; i++) {
                int value = i;
                futures.add(executor.submit(() -> {
                    JdkVersionLearningDemo.check(Thread.currentThread().isVirtual(), "任务应运行在虚拟线程");
                    Thread.sleep(10); // blocking operation, not a throughput benchmark
                    return value;
                }));
            }
            int sum = 0;
            for (var future : futures) sum += future.get(5, TimeUnit.SECONDS);
            JdkVersionLearningDemo.check(sum == 8128, "虚拟线程任务结果错误");
        }
        var original = new ArrayList<>(List.of("first", "last"));
        var reversed = original.reversed();
        reversed.addFirst("new-last"); // backed view, not a detached copy
        JdkVersionLearningDemo.check(original.getLast().equals("new-last") && reversed.getLast().equals("first"), "SequencedCollection 视图错误");
        JdkVersionLearningDemo.check(describe(new LanguageLessons.Circle(3)).equals("circle:3.0"), "record pattern 错误");
        JdkVersionLearningDemo.check(describe(new LanguageLessons.Rectangle(2, 4)).equals("rectangle:8.0"), "穷举 switch 错误");
        System.out.println("虚拟线程 128 个任务、SequencedCollection 反向视图、record patterns + sealed 穷举 switch 通过。");
    }

    private static String describe(LanguageLessons.Shape shape) {
        return switch (shape) {
            case LanguageLessons.Circle(var radius) -> "circle:" + radius;
            case LanguageLessons.Rectangle(var width, var height) -> "rectangle:" + width * height;
        };
    }

    static void jdk22() throws Exception {
        MemorySegment segment;
        try (Arena arena = Arena.ofConfined()) {
            segment = arena.allocate(ValueLayout.JAVA_INT);
            segment.set(ValueLayout.JAVA_INT, 0, 42);
            JdkVersionLearningDemo.check(segment.get(ValueLayout.JAVA_INT, 0) == 42, "外部内存读写错误");
            JdkVersionLearningDemo.expect(IndexOutOfBoundsException.class,
                    () -> segment.get(ValueLayout.JAVA_INT, 4));
            try (var worker = Executors.newVirtualThreadPerTaskExecutor()) {
                worker.submit(() -> JdkVersionLearningDemo.expect(WrongThreadException.class,
                        () -> segment.get(ValueLayout.JAVA_INT, 0))).get(5, TimeUnit.SECONDS);
            }
        }
        JdkVersionLearningDemo.expect(IllegalStateException.class, () -> segment.get(ValueLayout.JAVA_INT, 0));
        Object input = "ignored component";
        boolean matched = switch (input) {
            case String _ -> true; // unnamed pattern: deliberately no binding
            default -> false;
        };
        JdkVersionLearningDemo.check(matched, "unnamed pattern 错误");
        System.out.println("FFM：原生内存读写/越界/线程约束/关闭后访问拒绝；未命名模式通过（未调用外部 C 函数）。");
    }

    static void jdk24() throws Exception {
        var windows = Stream.of(1, 2, 3, 4, 5).gather(Gatherers.windowFixed(2)).toList();
        JdkVersionLearningDemo.check(windows.equals(List.of(List.of(1, 2), List.of(3, 4), List.of(5))), "Gatherers 分窗错误");
        var sums = Stream.of(1, 2, 3).gather(Gatherers.scan(() -> 0, Integer::sum)).toList();
        JdkVersionLearningDemo.check(sums.equals(List.of(1, 3, 6)), "Gatherers scan 前缀和错误");
        ClassFile format = ClassFile.of();
        byte[] bytes = format.build(ClassDesc.of("com.kuma.cloud.lab.jdk.GeneratedAnswer"), builder ->
                builder.withFlags(ClassFile.ACC_PUBLIC).withMethodBody("answer",
                        MethodTypeDesc.of(ConstantDescs.CD_int), ClassFile.ACC_PUBLIC | ClassFile.ACC_STATIC,
                        code -> code.bipush(42).ireturn()));
        var model = format.parse(bytes);
        JdkVersionLearningDemo.check(model.methods().size() == 1
                && model.methods().getFirst().methodName().stringValue().equals("answer"), "Class-File parse 错误");
        JdkVersionLearningDemo.check(format.verify(bytes).isEmpty(), "Class-File verify 错误");
        // Fresh loader makes this experiment repeatable without duplicate class-definition errors.
        class LabLoader extends ClassLoader {
            Class<?> loadBytes(byte[] data) { return defineClass(null, data, 0, data.length); }
        }
        Class<?> generated = new LabLoader().loadBytes(bytes);
        JdkVersionLearningDemo.check(generated.getMethod("answer").invoke(null).equals(42), "生成字节码执行错误");
        System.out.println("Gatherers 分窗/前缀和；Class-File API 生成、解析、验证、加载执行 answer=42 通过。");
    }
}
