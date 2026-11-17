package com.kuma.cloud.lab.basics;

/** Bounded, standalone experiments; failed checks always throw without requiring -ea. */
public final class JavaBasicsLearningDemo {
    private JavaBasicsLearningDemo() {}

    public static void main(String[] args) throws Exception {
        String lesson = args.length == 0 ? "all" : args[0];
        switch (lesson) {
            case "syntax" -> SyntaxLesson.run();
            case "volatile" -> VolatileLesson.run();
            case "objects" -> ObjectLesson.run();
            case "markword" -> MarkWordLesson.run();
            case "classloading" -> ClassLoadingLesson.run();
            case "inheritance" -> ObjectSemanticsLessons.inheritance();
            case "abstract" -> ObjectSemanticsLessons.abstraction();
            case "initialization" -> ObjectSemanticsLessons.initialization();
            case "passing" -> ObjectSemanticsLessons.passing();
            case "all" -> {
                SyntaxLesson.run();
                VolatileLesson.run();
                ObjectLesson.run();
                MarkWordLesson.run();
                ClassLoadingLesson.run();
                ObjectSemanticsLessons.inheritance();
                ObjectSemanticsLessons.abstraction();
                ObjectSemanticsLessons.initialization();
                ObjectSemanticsLessons.passing();
            }
            default -> throw new IllegalArgumentException("Choose all|syntax|volatile|objects|markword|classloading|inheritance|abstract|initialization|passing");
        }
        System.out.println("Java basics: " + lesson + " passed");
    }

    static void check(String label, boolean condition, Object evidence) {
        System.out.println(label + " = " + evidence + "; check=" + condition);
        if (!condition) throw new IllegalStateException(label + ": " + evidence);
    }
}
