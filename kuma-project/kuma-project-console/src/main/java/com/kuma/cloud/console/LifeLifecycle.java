package com.kuma.cloud.console;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.nio.file.Path;
import java.time.Duration;
import java.util.*;

@Service
public class LifeLifecycle {
    private final CommandRunner commands;
    private final ObjectMapper mapper;
    private final String distribution, directory, qinglongUrl, rsshubUrl;
    private String operation = "", error = "";
    private static final List<String> DEPLOYMENTS = List.of("qinglong", "rsshub", "redis");

    public LifeLifecycle(CommandRunner commands, ObjectMapper mapper,
                         @Value("${console.life.distribution:Ubuntu-24.04}") String distribution,
                         @Value("${console.life.directory:D:/Vscode_project/life}") String directory,
                         @Value("${console.life.qinglong-port:5700}") int qinglongPort,
                         @Value("${console.life.rsshub-port:1200}") int rsshubPort) {
        this.commands = commands; this.mapper = mapper; this.distribution = distribution;
        this.directory = Path.of(directory).toAbsolutePath().normalize().toString();
        if (qinglongPort < 1 || qinglongPort > 65535 || rsshubPort < 1 || rsshubPort > 65535)
            throw new IllegalArgumentException("Life 面板端口无效");
        qinglongUrl = "http://localhost:" + qinglongPort;
        rsshubUrl = "http://localhost:" + rsshubPort;
    }

    private List<String> wsl(String... args) {
        var command = new ArrayList<>(List.of("wsl.exe", "--distribution", distribution, "--user", "root", "--exec"));
        command.addAll(List.of(args)); return command;
    }

    private CommandRunner.Result running() {
        return commands.run(List.of("wsl.exe", "--list", "--running", "--quiet"), Duration.ofSeconds(5));
    }

    private boolean includesDistribution(CommandRunner.Result result) {
        return result.success() && result.output().lines().map(String::strip).anyMatch(distribution::equals);
    }

    public synchronized Map<String, Object> status() {
        var data = new LinkedHashMap<String, Object>();
        data.put("directory", directory); data.put("distribution", distribution); data.put("namespace", "life-deals");
        data.put("qinglongUrl", qinglongUrl); data.put("rsshubUrl", rsshubUrl); data.put("error", error);
        data.put("canStart", false); data.put("canStop", false); data.put("deployments", List.of());
        if (!operation.isEmpty()) { data.put("state", operation); data.put("message", "正在" + (operation.equals("STARTING") ? "启动" : "停止") + " Life 服务…"); return data; }
        var running = running();
        if (!running.success()) { data.put("state", "UNKNOWN"); data.put("message", "无法读取 WSL 状态：" + running.error()); return data; }
        if (!includesDistribution(running)) {
            data.put("state", "WSL_STOPPED"); data.put("message", distribution + " 未运行"); data.put("canStart", true); return data;
        }
        var result = commands.run(wsl("k3s", "kubectl", "-n", "life-deals", "get", "deployment", "qinglong", "rsshub", "redis", "-o", "json"), Duration.ofSeconds(8));
        if (!result.success()) { data.put("state", "UNKNOWN"); data.put("message", "无法读取 life-deals 部署：" + result.error()); return data; }
        try {
            var rows = new ArrayList<Map<String, Object>>(); int desired = 0, ready = 0;
            for (var item : mapper.readTree(result.output()).path("items")) {
                String name = item.path("metadata").path("name").asString();
                if (!DEPLOYMENTS.contains(name)) continue;
                int replicas = item.path("spec").path("replicas").asInt(0), available = item.path("status").path("readyReplicas").asInt(0);
                desired += replicas; ready += Math.min(replicas, available);
                rows.add(Map.of("name", name, "replicas", replicas, "ready", available));
            }
            if (rows.size() != DEPLOYMENTS.size()) throw new IllegalArgumentException("Life 部署不完整");
            boolean forwarding = commands.run(wsl("/bin/sh", "-c", "systemctl is-active --quiet life-deals-qinglong && systemctl is-active --quiet life-deals-rsshub"), Duration.ofSeconds(5)).success();
            String state = desired == 0 ? "STOPPED" : ready == desired && forwarding ? "RUNNING" : "DEGRADED";
            data.put("state", state); data.put("deployments", rows);
            data.put("message", "life-deals · 就绪 " + ready + "/" + desired + " · 访问入口" + (forwarding ? "运行中" : "未就绪"));
            data.put("canStart", !state.equals("RUNNING")); data.put("canStop", desired > 0 || forwarding);
        } catch (Exception e) { data.put("state", "UNKNOWN"); data.put("message", "无法解析 Life 部署状态"); }
        return data;
    }

    public synchronized Map<String, Object> action(String action) {
        if (!Set.of("start", "stop").contains(action)) throw new IllegalArgumentException("不支持的 Life 操作");
        if (!operation.isEmpty()) throw new IllegalArgumentException("Life 操作正在进行");
        var state = status();
        if (!Boolean.TRUE.equals(state.get(action.equals("start") ? "canStart" : "canStop")))
            throw new IllegalArgumentException("当前无法" + (action.equals("start") ? "启动" : "停止") + " Life，请检查部署状态");
        operation = action.equals("start") ? "STARTING" : "STOPPING"; error = "";
        Thread.ofVirtual().name("life-" + action).start(() -> {
            String script = action.equals("start")
                    ? "systemctl start k3s && k3s kubectl -n life-deals scale deployment/qinglong deployment/rsshub deployment/redis --replicas=1 && systemctl start life-deals-qinglong life-deals-rsshub"
                    : "systemctl stop life-deals-qinglong life-deals-rsshub && k3s kubectl -n life-deals scale deployment/qinglong deployment/rsshub deployment/redis --replicas=0";
            var result = commands.run(wsl("/bin/sh", "-c", script), Duration.ofSeconds(90));
            synchronized (this) { operation = ""; if (!result.success()) error = result.error().isBlank() ? result.output() : result.error(); }
        });
        return status();
    }

    public Map<String, String> details() {
        if (!includesDistribution(running())) return Map.of("body", distribution + " 未运行，未执行 WSL 查询");
        var result = commands.run(wsl("k3s", "kubectl", "-n", "life-deals", "get", "deployments,pods,pvc", "-o", "wide"), Duration.ofSeconds(8));
        return Map.of("body", result.success() ? result.output() : result.error());
    }
}
