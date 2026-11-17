package com.kuma.cloud.lab.basics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.kuma.cloud.lab.basics.JavaBasicsLearningDemo.check;

final class ObjectLesson {
    private static class Parent {
        String name = "parent-field";
        String describe() { return "parent-method"; }
    }
    private static final class Child extends Parent {
        String name = "child-field";
        @Override String describe() { return "child-method"; }
    }
    private static final class Box { int value = 1; }
    private record Person(String name, List<String> tags) {}

    static void run() {
        System.out.println("--- 对象：引用、值传递、多态、equals/hashCode、浅拷贝 ---");
        Box original = new Box();
        mutateAndReassign(original);
        check("Java 值传递：引用副本可改对象、重新赋值不改调用方", original.value == 2, original.value);
        Parent polymorphic = new Child();
        check("字段隐藏与方法动态分派", polymorphic.name.equals("parent-field")
                && polymorphic.describe().equals("child-method"), polymorphic.name + "/" + polymorphic.describe());
        Person first = new Person("Kuma", new ArrayList<>(List.of("java")));
        Person equal = new Person("Kuma", new ArrayList<>(List.of("java")));
        check("record 的内容相等与 hashCode 契约", first != equal && first.equals(equal)
                && first.hashCode() == equal.hashCode(), first);
        check("HashSet 按 equals/hashCode 去重", new HashSet<>(List.of(first, equal)).size() == 1, "size=1");
        Person shallow = new Person(first.name(), first.tags());
        Person copied = new Person(first.name(), List.copyOf(first.tags()));
        first.tags().add("jvm");
        check("record 仅浅不可变；共享成员与集合副本", shallow.tags().size() == 2 && copied.tags().size() == 1,
                "shared=" + shallow.tags() + ", copied=" + copied.tags());
        System.out.println("参与 hashCode 的可变成员变化后，不应继续把该对象作为哈希集合键使用。");
    }

    private static void mutateAndReassign(Box box) {
        box.value = 2;
        box = new Box();
        box.value = 99;
    }
}
