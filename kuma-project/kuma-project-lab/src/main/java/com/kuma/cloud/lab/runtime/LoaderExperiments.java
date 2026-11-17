package com.kuma.cloud.lab.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicInteger;

import static com.kuma.cloud.lab.runtime.RuntimeLearningDemo.check;

final class LoaderExperiments {
    private static final AtomicInteger ATTEMPTS = new AtomicInteger();
    private static final class Broken {
        static { fail(); }
        private static void fail() { ATTEMPTS.incrementAndGet(); throw new IllegalArgumentException("initialization failure"); }
    }

    static void failure() throws Exception {
        System.out.println("--- 类加载：初始化失败与错误状态 ---");
        ClassLoader loader = LoaderExperiments.class.getClassLoader();
        String name = LoaderExperiments.class.getName() + "$Broken";
        Class.forName(name, false, loader);
        check("仅加载未执行失败的 static", ATTEMPTS.get() == 0, ATTEMPTS.get());
        try { Class.forName(name, true, loader); throw new IllegalStateException("Expected initialization failure"); }
        catch (ExceptionInInitializerError expected) {
            check("首次初始化保留原始 cause", expected.getCause() instanceof IllegalArgumentException, expected.getCause().getMessage());
        }
        try { Class.forName(name, true, loader); throw new IllegalStateException("Expected erroneous class state"); }
        catch (NoClassDefFoundError expected) {
            check("同一加载器不重试失败的初始化", ATTEMPTS.get() == 1, "NoClassDefFoundError; attempts=" + ATTEMPTS.get());
        }
        System.out.println("此处 class 文件存在；NoClassDefFoundError 来自先前初始化失败。每次实验请启动新 JVM。");
    }

    static void identity() throws Exception {
        System.out.println("--- 类加载：定向隔离、默认委派与类身份 ---");
        String name = RuntimePayload.class.getName();
        ClassLoader parent = LoaderExperiments.class.getClassLoader();
        ClassLoader first = new PayloadLoader(parent, name), second = new PayloadLoader(parent, name);
        Class<?> left = first.loadClass(name), right = second.loadClass(name);
        check("同一加载器缓存同一 Class", left == first.loadClass(name), left.getName());
        check("同名类由不同加载器定义后不兼容", left != right && !left.isAssignableFrom(right), left.getName());
        check("核心类仍向父加载器委派", first.loadClass("java.lang.String") == String.class, "java.lang.String");
        try { first.loadClass(name + "Missing"); throw new IllegalStateException("Expected missing class"); }
        catch (ClassNotFoundException expected) { System.out.println("不存在的类 -> ClassNotFoundException (expected)"); }
    }

    static void context() throws Exception {
        System.out.println("--- 类加载：线程上下文加载器与资源查找 ---");
        var folder = Files.createTempDirectory("kuma-loader-");
        var resource = folder.resolve("kuma-runtime-resource.txt");
        Thread current = Thread.currentThread();
        ClassLoader previous = current.getContextClassLoader();
        try {
            Files.writeString(resource, "resource-from-isolated-loader", StandardCharsets.UTF_8);
            try (URLClassLoader isolated = new URLClassLoader(new java.net.URL[] {folder.toUri().toURL()}, previous)) {
                current.setContextClassLoader(isolated);
                try (InputStream input = current.getContextClassLoader().getResourceAsStream(resource.getFileName().toString())) {
                    if (input == null) throw new IllegalStateException("Resource missing");
                    String text = new String(input.readAllBytes(), StandardCharsets.UTF_8);
                    check("显式使用 TCCL 查找隔离资源", text.equals("resource-from-isolated-loader"), text);
                } finally { current.setContextClassLoader(previous); }
            }
            check("finally 恢复原上下文加载器", current.getContextClassLoader() == previous, true);
        } finally {
            current.setContextClassLoader(previous);
            Files.deleteIfExists(resource);
            Files.deleteIfExists(folder);
        }
        System.out.println("TCCL 不会自动改变每次 Class.forName 的加载器；使用它的 API 必须显式遵循相应查找规则。");
    }

    private static final class PayloadLoader extends ClassLoader {
        private final String isolatedName;
        PayloadLoader(ClassLoader parent, String name) { super(parent); isolatedName = name; }
        @Override protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            synchronized (getClassLoadingLock(name)) {
                if (!name.equals(isolatedName)) return super.loadClass(name, resolve);
                Class<?> type = findLoadedClass(name);
                if (type == null) try (InputStream input = getParent().getResourceAsStream(name.replace('.', '/') + ".class")) {
                    if (input == null) throw new ClassNotFoundException(name);
                    byte[] bytes = input.readAllBytes();
                    type = defineClass(name, bytes, 0, bytes.length);
                } catch (IOException error) { throw new ClassNotFoundException(name, error); }
                if (resolve) resolveClass(type);
                return type;
            }
        }
    }
}

final class RuntimePayload {}
