package com.kuma.cloud.console;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.nio.file.*;
import java.time.Duration;
import java.util.*;

@RestController
@RequestMapping("/api/credentials")
public class CredentialsController {
    private final CommandRunner commands;
    private final ObjectMapper mapper;
    private final Path script;
    private final String distribution;

    public CredentialsController(CommandRunner commands, ObjectMapper mapper,
                                 @Value("${console.workspace:}") String workspace,
                                 @Value("${console.life.distribution:Ubuntu-24.04}") String distribution) {
        this.commands = commands;
        this.mapper = mapper;
        this.distribution = distribution;
        Path root = workspace.isBlank() ? LabLifecycle.findWorkspace(Path.of(System.getProperty("user.dir"))) : Path.of(workspace);
        script = root.resolve("scripts/get-console-credentials.ps1").toAbsolutePath().normalize();
    }

    public record Query(String service, boolean reveal) {}

    @PostMapping
    public JsonNode read(@RequestBody Query query, HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        authorize(request);
        String service = query.service() == null ? "all" : query.service();
        if (!service.matches("[a-z0-9][a-z0-9-]{0,79}") || (query.reveal() && service.equals("all")))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "请选择一个服务查看凭据");
        if (!System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win") || !Files.isRegularFile(script))
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "需要 Windows 本机及工作区中的凭据脚本");
        var command = new ArrayList<>(List.of("powershell.exe", "-NoProfile", "-NonInteractive", "-ExecutionPolicy", "Bypass", "-Command",
                "[Console]::OutputEncoding = [Text.UTF8Encoding]::new($false); & '" + script.toString().replace("'", "''")
                        + "' -AsJson -Service '" + service + "' -Distribution '" + distribution.replace("'", "''") + "'"
                        + (query.reveal() ? " -Reveal" : "")));
        var result = commands.run(command, Duration.ofSeconds(60));
        if (!result.success()) throw unavailable();
        try {
            JsonNode data = mapper.readTree(result.output());
            if (!data.isArray()) throw unavailable();
            return data;
        } catch (Exception ignored) { throw unavailable(); }
    }

    private ResponseStatusException unavailable() {
        // Never include stdout/stderr: either can contain credentials.
        return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "凭据读取失败，请检查本机脚本和私有凭据文件");
    }

    static void authorize(HttpServletRequest request) {
        try {
            if (!Set.of("127.0.0.1", "::1", "0:0:0:0:0:0:0:1").contains(request.getRemoteAddr())
                    || !"console".equals(request.getHeader("X-Kuma-Credentials"))
                    || "cross-site".equals(request.getHeader("Sec-Fetch-Site"))) throw new IllegalArgumentException();
            URI origin = URI.create(Objects.requireNonNull(request.getHeader("Origin")));
            URI local = URI.create(request.getRequestURL().toString());
            if (!Set.of("localhost", "127.0.0.1", "[::1]").contains(local.getHost())
                    || !Objects.equals(origin.getScheme(), local.getScheme())
                    || !Objects.equals(origin.getHost(), local.getHost())
                    || origin.getPort() != local.getPort()) throw new IllegalArgumentException();
        } catch (Exception ignored) { throw new ResponseStatusException(HttpStatus.FORBIDDEN, "仅允许本机 Console 访问凭据"); }
    }
}
