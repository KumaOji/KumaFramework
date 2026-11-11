package com.kuma.cloud.console;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {
    private final DshLifecycle dsh;
    public AiController(DshLifecycle dsh) { this.dsh = dsh; }

    @GetMapping("/status") public Object status(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store"); return dsh.status();
    }
    @GetMapping("/logs") public Object logs(HttpServletResponse response) throws IOException {
        response.setHeader("Cache-Control", "no-store");
        return Map.of("body", dsh.logs().get("body").replaceAll("([?&]token=)[A-Za-z0-9_-]+", "$1[redacted]"));
    }
    @PostMapping("/start") public Object start(HttpServletRequest request) throws IOException {
        checkOrigin(request); return dsh.start();
    }
    @PostMapping("/stop") public Object stop(HttpServletRequest request) {
        checkOrigin(request); return dsh.stop();
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
