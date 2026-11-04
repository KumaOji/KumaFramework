package com.kuma.cloud.lab.p0;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Per-invocation evidence; a failed assertion fails the experiment, never reports success. */
public record LabReport(String lesson, List<Step> steps, Map<String, Boolean> checks) {
    public record Step(String name, String explanation, Object evidence) {}
    public static final class Builder {
        private final String lesson;
        private final List<Step> steps = new ArrayList<>();
        private final Map<String, Boolean> checks = new LinkedHashMap<>();
        public Builder(String lesson) { this.lesson = lesson; }
        public void step(String name, String explanation, Object evidence) {
            steps.add(new Step(name, explanation, evidence));
        }
        public void check(String name, boolean passed) {
            checks.put(name, passed);
            if (!passed) throw new IllegalStateException("实验检查失败: " + name);
        }
        public LabReport build() { return new LabReport(lesson, List.copyOf(steps), Map.copyOf(checks)); }
    }
    public static void validate(String lesson, String... choices) {
        if (!List.of(choices).contains(lesson)) throw new IllegalArgumentException("Unknown lesson: " + lesson);
    }
    public static boolean selected(String lesson, String name) { return lesson.equals("all") || lesson.equals(name); }
}
