package com.kuma.cloud.lab.spring.support;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Component;

/**
 * 内存记录事件与生命周期日志，便于通过 API 观察 Spring 行为。
 */
@Component
public class SpringLabEventHistory {

    private final CopyOnWriteArrayList<String> handledEvents = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<String> lifecycleLogs = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<String> contextLogs = new CopyOnWriteArrayList<>();

    public void recordEvent(String message) {
        handledEvents.add(timestamp() + " " + message);
    }

    public void recordLifecycle(String message) {
        lifecycleLogs.add(timestamp() + " " + message);
    }

    public void recordContext(String message) {
        contextLogs.add(timestamp() + " " + message);
    }

    public List<String> handledEvents() {
        return Collections.unmodifiableList(new ArrayList<>(handledEvents));
    }

    public List<String> lifecycleLogs() {
        return Collections.unmodifiableList(new ArrayList<>(lifecycleLogs));
    }

    public List<String> contextLogs() {
        return Collections.unmodifiableList(new ArrayList<>(contextLogs));
    }

    private static String timestamp() {
        return LocalDateTime.now().toString();
    }

}
