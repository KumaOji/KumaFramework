package com.kuma.cloud.lab.basics;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static com.kuma.cloud.lab.basics.JavaBasicsLearningDemo.check;

/** Small independent experiments: overload selection, dispatch, construction and argument values. */
final class ObjectSemanticsLessons {
    private static class Parent {
        String name = "parent-field";
        String describe() { return "parent-method"; }
        static String type() { return "parent-static"; }
        String accept(Object value) { return "parent-object"; }
    }
    private static final class Child extends Parent {
        String name = "child-field";
        @Override String describe() { return "child-method"; }
        static String type() { return "child-static"; }
        @Override String accept(Object value) { return "child-object"; }
        String accept(String value) { return "child-string"; }
        String parentDescription() { return super.describe(); }
    }

    @SuppressWarnings("static") // Deliberately show that static selection follows the reference's declared type.
    static void inheritance() {
        System.out.println("--- 继承：override / overload / super / static / instanceof ---");
        Child child = new Child();
        Parent parent = child;
        check("重写按运行时对象分派", parent.describe().equals("child-method"), parent.describe());
        check("字段按引用声明类型选择", parent.name.equals("parent-field") && child.name.equals("child-field"),
                parent.name + "/" + child.name);
        check("static 方法隐藏", parent.type().equals("parent-static") && child.type().equals("child-static"),
                parent.type() + "/" + child.type());
        Object text = "hello";
        check("重载编译期选签名、重写运行期选实现", parent.accept("hello").equals("child-object")
                && child.accept(text).equals("child-object") && child.accept("hello").equals("child-string"),
                "parent.accept(String)=child-object; child.accept(String)=child-string");
        check("super 指定父类实现", child.parentDescription().equals("parent-method"), child.parentDescription());
        check("向上转型保持对象身份", parent == child && parent instanceof Child, parent.getClass().getSimpleName());
        Parent plain = new Parent();
        try {
            Child invalid = (Child) plain;
            throw new IllegalStateException("Unexpected cast: " + invalid);
        } catch (ClassCastException expected) {
            System.out.println("父类实例强转子类 -> ClassCastException (expected); instanceof 可先检查");
        }
    }

    private interface Named {
        String name();
        default String label() { return "name=" + name(); }
    }
    private abstract static class Shape implements Named {
        private final String name;
        Shape(String name) { this.name = name; }
        @Override public final String name() { return name; }
        abstract int area();
        final String report() { return label() + ", area=" + area(); }
    }
    private static final class Rectangle extends Shape {
        private final int width;
        private final int height;
        Rectangle(int width, int height) { super("rectangle"); this.width = width; this.height = height; }
        @Override int area() { return width * height; }
    }

    static void abstraction() {
        System.out.println("--- 抽象类：构造器、状态、抽象方法、模板方法、接口默认方法 ---");
        Shape shape = new Rectangle(3, 4);
        check("抽象类型引用调用具体实现", shape.area() == 12, shape.report());
        check("抽象类构造器保存共享状态", shape.name().equals("rectangle"), shape.name());
        check("final 模板组合接口默认方法与抽象钩子", shape.report().equals("name=rectangle, area=12"), shape.report());
        Shape anonymous = new Shape("anonymous") { @Override int area() { return 7; } };
        check("匿名具体子类实现抽象方法", anonymous.area() == 7, anonymous.report());
        check("反射观察抽象修饰符", Modifier.isAbstract(Shape.class.getModifiers())
                && !Modifier.isAbstract(Rectangle.class.getModifiers()), "Shape=abstract, Rectangle=concrete");
        System.out.println("abstract 类不能直接 new；子类须实现抽象方法或继续声明 abstract；final 方法不能重写。"
                + "这些编译约束见教程中的 javac 反例。");
    }

    private static final List<String> INIT = new ArrayList<>();
    private static class InitParent {
        static { INIT.add("parent-static"); }
        int parent = init("parent-field", 10);
        { INIT.add("parent-block"); }
        InitParent() { INIT.add("parent-constructor"); observe(); }
        void observe() {}
    }
    private static final class InitChild extends InitParent {
        static { INIT.add("child-static"); }
        int child = init("child-field", 20);
        { INIT.add("child-block"); }
        InitChild() { this(1); INIT.add("child-noarg-body"); }
        InitChild(int ignored) { super(); INIT.add("child-constructor"); }
        @Override void observe() { INIT.add("virtual-child=" + child); }
    }
    private static int init(String event, int value) { INIT.add(event); return value; }

    static void initialization() {
        System.out.println("--- 对象初始化：默认值、static、字段、初始化块、super / this、构造器动态分派 ---");
        INIT.clear();
        InitChild first = new InitChild();
        List<String> instance = List.of("parent-field", "parent-block", "parent-constructor", "virtual-child=0",
                "child-field", "child-block", "child-constructor", "child-noarg-body");
        List<String> expected = new ArrayList<>(List.of("parent-static", "child-static"));
        expected.addAll(instance);
        check("首次初始化的完整顺序", INIT.equals(expected), INIT);
        check("构造完成后的字段", first.parent == 10 && first.child == 20, first.parent + "/" + first.child);
        INIT.clear();
        new InitChild();
        check("第二个对象不重复 static，this 链不重复实例初始化", INIT.equals(instance), INIT);
        System.out.println("父类构造器调用可重写方法时，子类字段尚为默认值0；实际业务应避免构造期间暴露未完成对象。"
                + "本例使用传统 super 在前的顺序；JDK25允许受限的构造器前置语句，此例不覆盖该变体。每项请在新 JVM 运行。");
    }

    private static final class Box { int value; Box(int value) { this.value = value; } }

    static void passing() {
        System.out.println("--- 值传递：基本类型、引用、String、数组、集合、swap ---");
        int number = 1;
        changePrimitive(number);
        check("基本类型参数是值副本", number == 1, number);
        Box box = new Box(1);
        changeBox(box);
        check("可修改共享对象，重绑形参不重绑调用方", box.value == 2, box.value);
        String text = "kuma";
        changeString(text);
        check("String 重新赋值不改变调用方", text.equals("kuma"), text);
        int[] array = {1};
        changeArray(array);
        check("数组元素修改可见，形参换数组不可见", array.length == 1 && array[0] == 2, array[0]);
        List<String> list = new ArrayList<>(List.of("first"));
        changeList(list);
        check("集合元素修改可见", list.equals(List.of("first", "second")), list);
        Box left = new Box(10), right = new Box(20);
        swap(left, right);
        check("交换形参引用不能交换调用方变量", left.value == 10 && right.value == 20, left.value + "/" + right.value);
        check("返回新值再赋给调用方", increment(number) == 2 && number == 1, increment(number));
    }

    private static void changePrimitive(int value) { value = 99; }
    private static void changeBox(Box value) { value.value = 2; value = new Box(99); }
    private static void changeString(String value) { value += "-changed"; }
    private static void changeArray(int[] value) { value[0] = 2; value = new int[] {99, 100}; }
    private static void changeList(List<String> value) { value.add("second"); value = new ArrayList<>(); }
    private static void swap(Box left, Box right) { Box temporary = left; left = right; right = temporary; }
    private static int increment(int value) { return value + 1; }
}
