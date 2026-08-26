package com.kuma.cloud.lab.javacore.support;

import com.kuma.cloud.lab.javacore.domain.vo.ClassLoaderEntryVO;
import java.util.ArrayList;
import java.util.List;

/**
 * 类加载器层次与委派模型演示。
 */
public final class ClassLoaderInspector {

    private ClassLoaderInspector() {
    }

    public static List<ClassLoaderEntryVO> inspectHierarchy() {
        List<ClassLoaderEntryVO> entries = new ArrayList<>();

        entries.add(describe("java.lang.String", "JDK 核心类由 Bootstrap ClassLoader 加载（Java 代码中表现为 null）"));
        entries.add(describe("javax.sql.DataSource", "JDK 扩展/平台类通常由 Platform ClassLoader 加载"));
        entries.add(describe(ClassLoaderInspector.class.getName(), "应用类由 AppClassLoader 加载"));
        entries.add(describeCurrentThreadContext());

        ClassLoader app = ClassLoaderInspector.class.getClassLoader();
        entries.add(new ClassLoaderEntryVO(
                "AppClassLoader",
                app == null ? "null" : app.getClass().getName(),
                parentName(app),
                "加载 classpath 下的应用类，遵循双亲委派"));

        ClassLoader platform = app == null ? null : app.getParent();
        entries.add(new ClassLoaderEntryVO(
                "PlatformClassLoader",
                platform == null ? "null" : platform.getClass().getName(),
                parentName(platform),
                "加载 Java 平台模块（JDK 9+），替代旧的 ExtClassLoader"));

        entries.add(new ClassLoaderEntryVO(
                "BootstrapClassLoader",
                "null",
                "null",
                "由 C/C++ 实现，加载 rt.jar / 核心模块，Java 中无法直接获取引用"));

        return entries;
    }

    public static List<String> loadingPhases() {
        return List.of(
                "Loading：通过类名查找并读取 .class 字节流",
                "Linking：Verification（验证）→ Preparation（准备静态变量默认值）→ Resolution（符号引用解析，可延迟）",
                "Initialization：执行 <clinit>，初始化静态变量与静态代码块",
                "双亲委派：子加载器先委派父加载器，父无法加载时才自己尝试，避免核心类被篡改");
    }

    private static ClassLoaderEntryVO describe(String className, String note) {
        try {
            Class<?> type = Class.forName(className);
            ClassLoader loader = type.getClassLoader();
            return new ClassLoaderEntryVO(
                    className,
                    loader == null ? "null (Bootstrap)" : loader.getClass().getName(),
                    parentName(loader),
                    note);
        } catch (ClassNotFoundException ex) {
            return new ClassLoaderEntryVO(className, "N/A", "N/A", "类未找到: " + ex.getMessage());
        }
    }

    private static ClassLoaderEntryVO describeCurrentThreadContext() {
        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        return new ClassLoaderEntryVO(
                "Thread.contextClassLoader",
                contextLoader == null ? "null" : contextLoader.getClass().getName(),
                parentName(contextLoader),
                "SPI 等场景通过线程上下文类加载器打破双亲委派");
    }

    private static String parentName(ClassLoader loader) {
        if (loader == null) {
            return "null";
        }
        ClassLoader parent = loader.getParent();
        return parent == null ? "null" : parent.getClass().getName();
    }

}
