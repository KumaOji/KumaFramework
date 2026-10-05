package com.kuma.cloud.lab.network;

/** Local-only network lessons; ephemeral ports, JDK 25, no Spring or external services. */
public final class NetworkLearningDemo {
    private NetworkLearningDemo() { }

    public static void main(String[] args) throws Exception {
        WebhookLesson.run();
        SocketLesson.run();
        System.out.println("全部 Webhook 与 Socket 实验通过。");
    }

    static void check(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
}
