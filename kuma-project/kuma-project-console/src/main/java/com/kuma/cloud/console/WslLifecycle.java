package com.kuma.cloud.console;

import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.*;

@Service
public class WslLifecycle {
    private final CommandRunner commands;
    private final Map<String, String> operations = new HashMap<>(), errors = new HashMap<>();
    private final Map<String, Process> sessions = new HashMap<>();
    private boolean shuttingDown;
    private String shutdownError = "";

    public WslLifecycle(CommandRunner commands) { this.commands = commands; }
    private static List<String> names(CommandRunner.Result result) {
        return result.output().lines().map(String::strip).filter(s -> !s.isBlank()).toList();
    }
    private List<String> installed() {
        var result = commands.run(List.of("wsl.exe", "--list", "--quiet"), Duration.ofSeconds(5));
        if (!result.success()) throw new IllegalArgumentException("无法读取 WSL 发行版：" + result.error());
        return names(result);
    }
    public synchronized Map<String, Object> status() {
        if (!System.getProperty("os.name").startsWith("Windows")) return Map.of("supported", false, "distributions", List.of(), "canShutdown", false, "error", "当前系统不支持 WSL");
        var all = installed();
        var result = commands.run(List.of("wsl.exe", "--list", "--running", "--quiet"), Duration.ofSeconds(5));
        if (!result.success()) throw new IllegalArgumentException("无法读取 WSL 运行状态：" + result.error());
        var running = new HashSet<>(names(result));
        var distributions = all.stream().map(name -> Map.of("name", name,
                "state", shuttingDown ? "STOPPING" : operations.getOrDefault(name, running.contains(name) ? "RUNNING" : "STOPPED"),
                "canStart", !shuttingDown && !operations.containsKey(name) && !running.contains(name),
                "canStop", !shuttingDown && !operations.containsKey(name) && running.contains(name),
                "error", errors.getOrDefault(name, ""))).toList();
        return Map.of("supported", true, "distributions", distributions, "canShutdown", !shuttingDown && operations.isEmpty() && !running.isEmpty(), "error", shutdownError);
    }
    public synchronized Map<String, Object> action(String distribution, String action) {
        if (!System.getProperty("os.name").startsWith("Windows") || !Set.of("start", "stop").contains(action)) throw new IllegalArgumentException("不支持的 WSL 操作");
        if (!installed().contains(distribution)) throw new IllegalArgumentException("请选择已安装的 WSL 发行版");
        if (shuttingDown || operations.containsKey(distribution)) throw new IllegalArgumentException("WSL 操作正在进行，请稍候");
        operations.put(distribution, action.equals("start") ? "STARTING" : "STOPPING"); errors.remove(distribution);
        Thread.ofVirtual().name("wsl-" + action).start(() -> {
            CommandRunner.Result result;
            if (action.equals("start")) result = startSession(distribution);
            else {
                result = commands.run(List.of("wsl.exe", "--terminate", distribution), Duration.ofSeconds(45));
                if (result.success()) synchronized (this) { Process session = sessions.remove(distribution); if (session != null) session.destroy(); }
            }
            synchronized (this) {
                operations.remove(distribution);
                if (!result.success()) errors.put(distribution, result.error().isBlank() ? result.output() : result.error());
            }
        });
        return status();
    }
    private CommandRunner.Result startSession(String distribution) {
        Process session = null;
        boolean ready = false;
        try {
            session = commands.start(List.of("wsl.exe", "--distribution", distribution, "--exec", "/bin/sh", "-c", "exec sleep infinity"));
            synchronized (this) { sessions.put(distribution, session); }
            long deadline = System.nanoTime() + Duration.ofSeconds(45).toNanos();
            while (session.isAlive() && System.nanoTime() < deadline) {
                var running = commands.run(List.of("wsl.exe", "--list", "--running", "--quiet"), Duration.ofSeconds(5));
                if (running.success() && names(running).contains(distribution)) { ready = true; return new CommandRunner.Result(true, "", ""); }
                Thread.sleep(250);
            }
            return new CommandRunner.Result(false, "", "WSL 后台会话未能启动，请检查发行版");
        } catch (Exception e) { return new CommandRunner.Result(false, "", "WSL 启动失败：" + e.getMessage()); }
        finally {
            if (session != null && !ready) { session.destroy(); synchronized (this) { sessions.remove(distribution, session); } }
        }
    }
    public synchronized Map<String, Object> shutdownAll() {
        if (!System.getProperty("os.name").startsWith("Windows")) throw new IllegalArgumentException("当前系统不支持 WSL");
        if (shuttingDown || !operations.isEmpty()) throw new IllegalArgumentException("WSL 操作正在进行，请稍候");
        shuttingDown = true; shutdownError = "";
        Thread.ofVirtual().name("wsl-shutdown").start(() -> {
            var result = commands.run(List.of("wsl.exe", "--shutdown"), Duration.ofSeconds(45));
            synchronized (this) {
                shuttingDown = false;
                if (result.success()) { sessions.values().forEach(Process::destroy); sessions.clear(); }
                else shutdownError = result.error().isBlank() ? result.output() : result.error();
            }
        });
        return status();
    }
}
