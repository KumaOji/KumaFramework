package com.kuma.cloud.lab.basics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.kuma.cloud.lab.basics.JavaBasicsLearningDemo.check;

final class SyntaxLesson {
    static void run() {
        System.out.println("--- Java 语法：数值、控制流、字符串、泛型、集合、异常、Stream ---");
        int value = 5;
        int expression = value++ + ++value;
        check("前置/后置自增", expression == 12 && value == 7, expression + "/" + value);
        check("整数除法/浮点除法", 5 / 2 == 2 && 5 / 2.0 == 2.5, "2 / 2.5");
        check("窄化转换与溢出", (byte) 130 == -126 && Integer.MAX_VALUE + 1 == Integer.MIN_VALUE, (byte) 130);
        check("移位", (-1 >>> 1) == Integer.MAX_VALUE && (-1 >> 1) == -1, -1 >>> 1);
        int[] calls = {0};
        boolean ignored = false && ++calls[0] > 0;
        boolean evaluated = false & ++calls[0] > 0;
        check("短路求值", !ignored && !evaluated && calls[0] == 1, calls[0]);
        int sum = 0;
        for (int i = 0; i < 6; i++) {
            if (i == 2) continue;
            if (i == 5) break;
            sum += i;
        }
        int selected = switch (sum) { case 8 -> 80; default -> -1; };
        check("for/continue/break/switch", selected == 80, selected);
        String literal = "kuma";
        String allocated = new String(literal);
        check("String ==/equals/intern", literal != allocated && literal.equals(allocated)
                && literal == allocated.intern(), "引用不同、内容相等、intern相同");
        Integer boxed = 42;
        check("装箱/拆箱", boxed.intValue() == 42, boxed);
        Integer absent = null;
        try {
            int invalid = absent;
            throw new IllegalStateException("Unexpected unboxing: " + invalid);
        } catch (NullPointerException expected) {
            System.out.println("null 拆箱 -> NullPointerException (expected)");
        }
        List<Integer> mutable = new ArrayList<>(List.of(1, 2, 3));
        List<? extends Number> producer = mutable;
        List<Number> consumer = new ArrayList<>();
        copy(producer, consumer);
        List<Integer> snapshot = List.copyOf(mutable);
        mutable.add(4);
        check("泛型 extends/super 与集合快照", consumer.size() == 3 && snapshot.size() == 3, consumer);
        Map<Integer, Integer> squares = mutable.stream().filter(n -> n % 2 == 0)
                .collect(Collectors.toMap(n -> n, n -> n * n));
        check("Lambda/Stream/filter/collect", squares.equals(Map.of(2, 4, 4, 16)), squares);
        List<String> events = new ArrayList<>();
        try (Resource first = new Resource("first", events); Resource second = new Resource("second", events)) {
            throw new IllegalArgumentException("body");
        } catch (IllegalArgumentException expected) {
            check("try-with-resources 逆序关闭与 suppressed", events.equals(List.of("second", "first"))
                    && expected.getSuppressed().length == 2, events);
        }
    }

    private static void copy(List<? extends Number> source, List<? super Number> target) {
        target.addAll(source);
    }

    private record Resource(String name, List<String> events) implements AutoCloseable {
        @Override public void close() { events.add(name); throw new IllegalStateException(name); }
    }
}
