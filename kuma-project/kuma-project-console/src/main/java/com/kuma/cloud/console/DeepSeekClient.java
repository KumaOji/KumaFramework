package com.kuma.cloud.console;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

@Service
public class DeepSeekClient {
    static final Set<String> MODELS = Set.of("deepseek-chat", "deepseek-reasoner");
    private final ObjectMapper mapper;
    private final String environmentKey;
    private final Path keyFile;
    private final Path gradleProperties;
    private final URI endpoint;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NEVER).build();
    private final Semaphore slots = new Semaphore(2);
    private final ConcurrentMap<String, Runnable> cancellations = new ConcurrentHashMap<>();

    @org.springframework.beans.factory.annotation.Autowired
    public DeepSeekClient(ObjectMapper mapper, @Value("${DEEPSEEK_API_KEY:}") String key,
                          @Value("${console.workspace:}") String workspace) {
        this(mapper, key, Path.of(System.getProperty("user.home"), ".kuma-console", "deepseek.key"),
                URI.create("https://api.deepseek.com/chat/completions"),
                (workspace.isBlank() ? LabLifecycle.findWorkspace(Path.of(System.getProperty("user.dir")))
                        : Path.of(workspace).toAbsolutePath().normalize()).resolve("gradle.properties"));
    }

    DeepSeekClient(ObjectMapper mapper, String key, Path file, URI endpoint) {
        this(mapper, key, file, endpoint, null);
    }

    DeepSeekClient(ObjectMapper mapper, String key, Path file, URI endpoint, Path gradleProperties) {
        this.mapper = mapper; this.environmentKey = key.strip(); this.keyFile = file; this.endpoint = endpoint;
        this.gradleProperties = gradleProperties;
    }

    public record Message(String role, String content) {}
    public record Chat(String model, List<Message> messages) {}
    @FunctionalInterface interface Events { void send(String event, Object data) throws IOException; }

    private record Credential(String value, String source) {}

    private Credential credential() {
        if (!environmentKey.isEmpty()) return new Credential(environmentKey, "environment");
        if (gradleProperties != null && Files.isRegularFile(gradleProperties)) {
            var properties = new Properties();
            try (var reader = Files.newBufferedReader(gradleProperties, StandardCharsets.UTF_8)) {
                properties.load(reader);
                String key = properties.getProperty("deepseek.api.key", "").strip();
                if (!key.isEmpty()) return new Credential(key, "gradle");
            } catch (IOException | IllegalArgumentException ignored) { }
        }
        try { return new Credential(Files.readString(keyFile).strip(), "local"); }
        catch (IOException e) { return new Credential("", "local"); }
    }

    public Map<String, Object> status() {
        var credential = credential();
        return Map.of("configured", !credential.value().isEmpty(), "models", List.of("deepseek-chat", "deepseek-reasoner"),
                "source", credential.source());
    }

    public synchronized void saveKey(String key) throws IOException {
        if (!environmentKey.isEmpty()) throw new IllegalArgumentException("当前使用 DEEPSEEK_API_KEY，请在启动环境中修改");
        if (credential().source().equals("gradle")) throw new IllegalArgumentException("当前使用 gradle.properties 的 deepseek.api.key，请在该文件中修改");
        if (key == null || !key.matches("sk-[A-Za-z0-9_-]{16,200}"))
            throw new IllegalArgumentException("请输入有效格式的 DeepSeek API Key");
        Files.createDirectories(keyFile.getParent());
        Path temporary = Files.createTempFile(keyFile.getParent(), "deepseek-", ".tmp");
        try {
            if (Files.getFileStore(temporary).supportsFileAttributeView("posix"))
                Files.setPosixFilePermissions(temporary, java.nio.file.attribute.PosixFilePermissions.fromString("rw-------"));
            Files.writeString(temporary, key, StandardCharsets.UTF_8);
            Files.move(temporary, keyFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } finally { Files.deleteIfExists(temporary); }
    }

    void validate(Chat chat) {
        if (chat == null || !MODELS.contains(chat.model() == null ? "" : chat.model()))
            throw new IllegalArgumentException("请选择支持的 DeepSeek 模型");
        if (chat.messages() == null || chat.messages().isEmpty() || chat.messages().size() > 120)
            throw new IllegalArgumentException("每次最多发送 120 条消息，请新建对话后继续");
        long size = 0;
        for (Message message : chat.messages()) {
            if (message == null || message.role() == null || !Set.of("system", "user", "assistant").contains(message.role())
                    || message.content() == null || message.content().isBlank())
                throw new IllegalArgumentException("消息角色或正文无效");
            size += message.content().length();
        }
        if (size > 120_000) throw new IllegalArgumentException("对话超过 12 万字符，请新建对话后继续");
        if (!chat.messages().getLast().role().equals("user")) throw new IllegalArgumentException("最后一条消息必须是用户问题");
    }

    void stream(Chat chat, Events events) throws IOException {
        stream(chat, events, UUID.randomUUID().toString());
    }

    void cancel(String id) { var cancel = cancellations.get(id); if (cancel != null) cancel.run(); }

    void stream(Chat chat, Events events, String id) throws IOException {
        validate(chat);
        String apiKey = credential().value();
        if (apiKey.isEmpty()) { events.send("error", Map.of("message", "尚未配置 DeepSeek API Key，请打开连接设置")); return; }
        if (!slots.tryAcquire()) { events.send("error", Map.of("message", "已有两个回复正在生成，请稍后重试")); return; }
        Runnable registeredCancellation = null;
        try {
            var request = HttpRequest.newBuilder(endpoint).timeout(Duration.ofSeconds(180))
                    .header("Authorization", "Bearer " + apiKey).header("Content-Type", "application/json")
                    .header("Accept", "text/event-stream")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(Map.of(
                            "model", chat.model(), "messages", chat.messages(), "stream", true, "max_tokens", 8192)))).build();
            var bodyReference = new java.util.concurrent.atomic.AtomicReference<InputStream>();
            var cancelled = new java.util.concurrent.atomic.AtomicBoolean();
            var future = http.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream());
            Runnable cancellation = () -> {
                cancelled.set(true);
                future.cancel(true);
                var stream = bodyReference.get();
                if (stream != null) try { stream.close(); } catch (IOException ignored) {}
            };
            if (cancellations.putIfAbsent(id, cancellation) != null) {
                future.cancel(true);events.send("error", Map.of("message", "请求标识重复，请重试"));return;
            }
            registeredCancellation = cancellation;
            var response = future.get();
            bodyReference.set(response.body());
            if (cancelled.get()) { response.body().close(); return; }
            try (InputStream body = response.body(); var timer = Executors.newSingleThreadScheduledExecutor()) {
                // InputStream reads need their own deadline after response headers arrive.
                var deadline = timer.schedule(() -> { try { body.close(); } catch (IOException ignored) {} }, 180, TimeUnit.SECONDS);
                try {
                    if (response.statusCode() != 200) {
                        events.send("error", Map.of("message", upstreamError(response.statusCode()), "status", response.statusCode()));
                        return;
                    }
                    var reader = new BufferedReader(new InputStreamReader(body, StandardCharsets.UTF_8));
                    String line; int characters = 0;
                    while ((line = reader.readLine()) != null) {
                        if (!line.startsWith("data:")) continue;
                        String data = line.substring(5).strip();
                        if (data.equals("[DONE]")) { events.send("done", Map.of()); return; }
                        if (data.isEmpty()) continue;
                        var chunk = mapper.readTree(data);
                        if (chunk.has("error")) { events.send("error", Map.of("message", "DeepSeek 返回错误，请稍后重试")); return; }
                        var choice = chunk.path("choices").path(0);
                        var delta = choice.path("delta");
                        for (String field : List.of("reasoning_content", "content")) {
                            String value = delta.path(field).asString("");
                            characters += value.length();
                            if (characters > 300_000) { events.send("error", Map.of("message", "回复达到长度上限")); return; }
                            if (!value.isEmpty()) events.send(field.equals("content") ? "delta" : "reasoning", Map.of("text", value));
                        }
                        String finish = choice.path("finish_reason").asString("");
                        if (finish.equals("length")) events.send("notice", Map.of("message", "回复达到模型输出上限，可继续追问"));
                        if (finish.equals("content_filter")) { events.send("error", Map.of("message", "回复被模型内容策略中止")); return; }
                    }
                    events.send("error", Map.of("message", "连接提前结束，请重新生成"));
                } finally { deadline.cancel(false); }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            events.send("error", Map.of("message", "请求已中断"));
        } catch (ExecutionException | CancellationException e) {
            events.send("error", Map.of("message", "DeepSeek 请求中断或连接失败，请重试"));
        } catch (IOException e) {
            // Do not expose upstream bodies, authorization headers or exception messages.
            events.send("error", Map.of("message", "DeepSeek 连接中断或超时，请检查网络后重试"));
        } finally { if (registeredCancellation != null) cancellations.remove(id, registeredCancellation); slots.release(); }
    }

    static String upstreamError(int status) {
        return switch (status) {
            case 401 -> "API Key 无效，请检查连接设置";
            case 402 -> "DeepSeek 账户余额不足";
            case 429 -> "DeepSeek 请求过于频繁，请稍后重试";
            default -> "DeepSeek 请求失败（HTTP " + status + "），请稍后重试";
        };
    }
}
