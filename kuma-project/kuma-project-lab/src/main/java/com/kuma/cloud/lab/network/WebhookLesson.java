package com.kuma.cloud.lab.network;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Custom teaching protocol (not GitHub's wire protocol):
 * HMAC-SHA256 over id + '\n' + timestamp + '\n' + raw body bytes.
 * Authenticate before processing; retries retain event ID; duplicate handling must be atomic.
 * In-memory storage and synchronous simulated processing are only for this bounded experiment.
 */
public final class WebhookLesson {
    private static final byte[] SECRET = "local-lab-example-secret".getBytes(StandardCharsets.UTF_8);
    private static final int MAX_BODY = 4096;
    private final ConcurrentHashMap<String, byte[]> events = new ConcurrentHashMap<>();
    private final AtomicInteger retryAttempts = new AtomicInteger();
    private WebhookLesson() { }

    public static void main(String[] args) throws Exception { run(); }

    public static void run() throws Exception {
        WebhookLesson lesson = new WebhookLesson();
        var workers = Executors.newFixedThreadPool(4);
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.setExecutor(workers);
        server.createContext("/webhook", lesson::handle);
        try (HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build()) {
            server.start();
            URI url = URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/webhook");
            byte[] body = "{\"event\":\"order.created\",\"message\":\"订单已创建\"}".getBytes(StandardCharsets.UTF_8);
            String now = Long.toString(Instant.now().getEpochSecond());
            HttpRequest valid = request(url, "event-1", now, body, sign("event-1", now, body));
            expect(client, valid, 202);
            expect(client, valid, 200); // delivery repeated, business effect remains one
            byte[] changed = "{\"event\":\"tampered\"}".getBytes(StandardCharsets.UTF_8);
            expect(client, request(url, "tampered", now, changed, sign("tampered", now, body)), 401);
            expect(client, request(url, "event-1", now, changed, sign("event-1", now, changed)), 409);
            expect(client, request(url, "id-changed", now, body, sign("event-1", now, body)), 401);
            String old = Long.toString(Instant.now().minusSeconds(600).getEpochSecond());
            expect(client, request(url, "expired", old, body, sign("expired", old, body)), 401);
            String future = Long.toString(Instant.now().plusSeconds(600).getEpochSecond());
            expect(client, request(url, "future", future, body, sign("future", future, body)), 401);
            expect(client, request(url, "bad-signature", now, body, "not-hex"), 401);
            expect(client, HttpRequest.newBuilder(url).timeout(Duration.ofSeconds(5))
                    .POST(HttpRequest.BodyPublishers.ofByteArray(body)).build(), 400);
            expect(client, HttpRequest.newBuilder(url).timeout(Duration.ofSeconds(5)).GET().build(), 405);
            byte[] oversized = new byte[MAX_BODY + 1];
            expect(client, request(url, "oversized", now, oversized, sign("oversized", now, oversized)), 413);

            HttpRequest retry = request(url, "retry-event", now, body, sign("retry-event", now, body));
            int sends = 0;
            for (; sends < 3; sends++) {
                int status = client.send(retry, HttpResponse.BodyHandlers.discarding()).statusCode();
                System.out.printf("Webhook retry attempt=%d status=%d%n", sends + 1, status);
                if (status / 100 == 2) break;
                NetworkLearningDemo.check(status == 503, "只对模拟临时故障重试");
                TimeUnit.MILLISECONDS.sleep(25L << sends); // bounded educational backoff
            }
            NetworkLearningDemo.check(sends == 1 && lesson.retryAttempts.get() == 2, "预期首次 503，第二次成功");

            HttpRequest concurrent = request(url, "concurrent-event", now, body, sign("concurrent-event", now, body));
            var one = client.sendAsync(concurrent, HttpResponse.BodyHandlers.discarding());
            var two = client.sendAsync(concurrent, HttpResponse.BodyHandlers.discarding());
            int a = one.get(10, TimeUnit.SECONDS).statusCode();
            int b = two.get(10, TimeUnit.SECONDS).statusCode();
            NetworkLearningDemo.check((a == 202 && b == 200) || (a == 200 && b == 202), "并发重复应仅处理一次");
            NetworkLearningDemo.check(lesson.events.size() == 3, "只有三个有效事件应产生业务效果");
            System.out.println("Webhook：签名/篡改/时间窗口/重复与冲突/并发去重/503 重试/请求边界检查通过。");
        } finally {
            server.stop(0);
            workers.shutdownNow();
            NetworkLearningDemo.check(workers.awaitTermination(10, TimeUnit.SECONDS), "Webhook 线程退出超时");
        }
    }

    private void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            if (!exchange.getRequestURI().getPath().equals("/webhook")) { reply(exchange, 404); return; }
            if (!exchange.getRequestMethod().equals("POST")) {
                exchange.getResponseHeaders().set("Allow", "POST");
                reply(exchange, 405); return;
            }
            byte[] body = exchange.getRequestBody().readNBytes(MAX_BODY + 1);
            if (body.length > MAX_BODY) { reply(exchange, 413); return; }
            String id = exchange.getRequestHeaders().getFirst("X-Lab-Event-Id");
            String timestamp = exchange.getRequestHeaders().getFirst("X-Lab-Timestamp");
            String signature = exchange.getRequestHeaders().getFirst("X-Lab-Signature");
            if (id == null || !id.matches("[A-Za-z0-9-]{1,80}") || timestamp == null || signature == null) {
                reply(exchange, 400); return;
            }
            long sent;
            try { sent = Long.parseLong(timestamp); }
            catch (NumberFormatException e) { reply(exchange, 400); return; }
            long now = Instant.now().getEpochSecond();
            if (sent < now - 300 || sent > now + 300 || !validSignature(id, timestamp, body, signature)) {
                reply(exchange, 401); return;
            }
            if (id.equals("retry-event") && retryAttempts.incrementAndGet() == 1) {
                exchange.getResponseHeaders().set("Retry-After", "0");
                reply(exchange, 503); return; // fail BEFORE recording the event
            }
            // Atomic insertion models one business effect; no external side effect is performed.
            byte[] previous = events.putIfAbsent(id, body);
            reply(exchange, previous == null ? 202 : MessageDigest.isEqual(previous, body) ? 200 : 409);
        }
    }

    private static void reply(HttpExchange exchange, int status) throws IOException {
        exchange.sendResponseHeaders(status, -1);
    }

    private static boolean validSignature(String id, String timestamp, byte[] body, String signature) {
        if (!signature.matches("[0-9a-f]{64}")) return false;
        return MessageDigest.isEqual(HexFormat.of().parseHex(sign(id, timestamp, body)),
                HexFormat.of().parseHex(signature));
    }

    private static String sign(String id, String timestamp, byte[] body) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SECRET, "HmacSHA256"));
            mac.update((id + "\n" + timestamp + "\n").getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(mac.doFinal(body));
        } catch (GeneralSecurityException e) { throw new IllegalStateException(e); }
    }

    private static HttpRequest request(URI url, String id, String timestamp, byte[] body, String signature) {
        return HttpRequest.newBuilder(url).timeout(Duration.ofSeconds(5))
                .header("Content-Type", "application/json; charset=utf-8")
                .header("X-Lab-Event-Id", id).header("X-Lab-Timestamp", timestamp)
                .header("X-Lab-Signature", signature).POST(HttpRequest.BodyPublishers.ofByteArray(body)).build();
    }

    private static void expect(HttpClient client, HttpRequest request, int expected) throws Exception {
        int actual = client.send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
        NetworkLearningDemo.check(actual == expected, "Webhook 预期 " + expected + "，实际 " + actual);
    }
}
