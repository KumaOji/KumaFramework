package com.kuma.cloud.lab.basics;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.kuma.cloud.lab.basics.JavaBasicsLearningDemo.check;

final class ClassLoadingLesson {
    private static final List<String> EVENTS = new ArrayList<>();
    private static class Parent {
        static { EVENTS.add("parent-static"); }
        { EVENTS.add("parent-instance"); }
        Parent() { EVENTS.add("parent-constructor"); }
    }
    private static final class Child extends Parent {
        static final int CONSTANT = 7;
        static { EVENTS.add("child-static"); }
        { EVENTS.add("child-instance"); }
        Child() { EVENTS.add("child-constructor"); }
    }

    static void run() throws Exception {
        System.out.println("--- 类加载：加载与初始化、双亲委派、类身份 ---");
        EVENTS.clear();
        String name = ClassLoadingLesson.class.getName() + "$Child";
        ClassLoader application = ClassLoadingLesson.class.getClassLoader();
        Class<?> loaded = Class.forName(name, false, application);
        check("Class.forName initialize=false", EVENTS.isEmpty(), EVENTS);
        int constant = Child.CONSTANT;
        check("编译期常量不触发初始化", constant == 7 && EVENTS.isEmpty(), EVENTS);
        Class.forName(name, true, application);
        check("父类先于子类初始化", EVENTS.equals(List.of("parent-static", "child-static")), EVENTS);
        new Child();
        new Child();
        check("static 仅一次、实例初始化每次执行", EVENTS.equals(List.of("parent-static", "child-static",
                "parent-instance", "parent-constructor", "child-instance", "child-constructor",
                "parent-instance", "parent-constructor", "child-instance", "child-constructor")), EVENTS);
        check("重复加载返回同一 Class", loaded == Class.forName(name, false, application), loaded.getName());
        for (ClassLoader current = application; current != null; current = current.getParent())
            System.out.println("loader=" + current.getClass().getName());
        check("核心 String 由 bootstrap 加载", String.class.getClassLoader() == null, "bootstrap (null)");
        String isolatedName = LoaderIdentityPayload.class.getName();
        Class<?> isolated = new IsolatedLoader(application, isolatedName).loadClass(isolatedName);
        check("同名类 + 不同 defining loader = 不同类型", isolated.getName().equals(isolatedName)
                && isolated != LoaderIdentityPayload.class
                && !LoaderIdentityPayload.class.isAssignableFrom(isolated), isolated.getClassLoader());
        check("自定义加载器仍对 java.lang.String 委派", isolated.getClassLoader().loadClass("java.lang.String") == String.class, true);
        System.out.println("仅对实验 payload 定向加载字节码，其余类型沿用默认双亲委派；加载不等于初始化。");
    }

    private static final class IsolatedLoader extends ClassLoader {
        private final String isolatedName;
        IsolatedLoader(ClassLoader parent, String isolatedName) { super(parent); this.isolatedName = isolatedName; }
        @Override protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            synchronized (getClassLoadingLock(name)) {
                if (!name.equals(isolatedName)) return super.loadClass(name, resolve);
                Class<?> type = findLoadedClass(name);
                if (type == null) {
                    try (InputStream input = getParent().getResourceAsStream(name.replace('.', '/') + ".class")) {
                        if (input == null) throw new ClassNotFoundException(name);
                        byte[] bytes = input.readAllBytes();
                        type = defineClass(name, bytes, 0, bytes.length);
                    } catch (IOException ex) { throw new ClassNotFoundException(name, ex); }
                }
                if (resolve) resolveClass(type);
                return type;
            }
        }
    }
}

// Top-level payload avoids nestmate/enclosing-class relationships across loaders.
final class LoaderIdentityPayload {}
