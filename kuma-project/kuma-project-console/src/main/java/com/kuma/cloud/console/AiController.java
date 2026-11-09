package com.kuma.cloud.console;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {
    private final DeepSeekClient client;
    private final ObjectMapper mapper;
    public AiController(DeepSeekClient client, ObjectMapper mapper) { this.client = client; this.mapper = mapper; }

    @GetMapping("/status") public Object status() { return client.status(); }
    public record Connection(String apiKey) {}
    @PostMapping("/connection") public Object connection(@RequestBody Connection connection, HttpServletRequest request) throws IOException {
        checkOrigin(request); client.saveKey(connection.apiKey()); return client.status();
    }

    @PostMapping(value = "/chat", produces = "text/event-stream")
    public void chat(@RequestBody DeepSeekClient.Chat chat, @RequestParam String requestId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        checkOrigin(request); client.validate(chat);
        if (!requestId.matches("[A-Za-z0-9-]{16,80}")) throw new IllegalArgumentException("请求标识无效");
        response.setContentType("text/event-stream"); response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store"); response.setHeader("X-Accel-Buffering", "no");
        var writer = response.getWriter();
        client.stream(chat, (event, data) -> {
            writer.write("event: " + event + "\ndata: " + mapper.writeValueAsString(data) + "\n\n");
            writer.flush();
            if (writer.checkError()) throw new IOException("Client disconnected");
        }, requestId);
    }

    @PostMapping("/cancel/{id}") public Object cancel(@PathVariable String id, HttpServletRequest request) {
        checkOrigin(request);client.cancel(id);return Map.of("cancelled", true);
    }

    static void checkOrigin(HttpServletRequest request) {
        if ("cross-site".equals(request.getHeader("Sec-Fetch-Site"))) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        String origin = request.getHeader("Origin");
        if (origin == null) return;
        try {
            URI uri = URI.create(origin);
            int port = uri.getPort() < 0 ? ("https".equals(uri.getScheme()) ? 443 : 80) : uri.getPort();
            if (!java.util.Set.of("127.0.0.1", "localhost", "[::1]").contains(uri.getHost())
                    || !java.util.Set.of("http", "https").contains(uri.getScheme()) || port != request.getLocalPort())
                throw new IllegalArgumentException();
        } catch (IllegalArgumentException e) { throw new ResponseStatusException(HttpStatus.FORBIDDEN); }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object invalid(IllegalArgumentException error) { return Map.of("error", error.getMessage()); }
}
