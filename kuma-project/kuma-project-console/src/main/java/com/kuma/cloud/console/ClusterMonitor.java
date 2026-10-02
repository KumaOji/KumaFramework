package com.kuma.cloud.console;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class ClusterMonitor {
    private static final String OVERVIEW_RESOURCES = "nodes,namespaces,pods,services,deployments,statefulsets,"
            + "daemonsets,replicasets,jobs,cronjobs,ingresses,persistentvolumes,persistentvolumeclaims,events";
    private final CommandRunner commands;
    private final ConsoleProperties properties;
    private final ObjectMapper mapper;
    private volatile Map<String, Object> snapshot = Map.of("ready", false);
    private volatile Set<String> resourceTypes = Set.of();

    public ClusterMonitor(CommandRunner commands, ConsoleProperties properties, ObjectMapper mapper) {
        this.commands = commands; this.properties = properties; this.mapper = mapper;
    }

    public Map<String, Object> snapshot() { return snapshot; }

    @Scheduled(fixedDelay = 15000)
    public synchronized void sample() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("ready", true); data.put("sampledAt", Instant.now().toString());
        data.put("distribution", properties.wslDistribution());
        data.put("items", List.of()); data.put("resourceTypes", resourceTypes.stream().sorted().toList());
        data.put("error", "");
        var distributions = commands.run(List.of("wsl.exe", "--list", "--verbose"), Duration.ofSeconds(5));
        data.put("distributions", distributions.success() ? distributions.output() : distributions.error());
        var running = commands.run(List.of("wsl.exe", "--list", "--running", "--quiet"), Duration.ofSeconds(5));
        data.put("runningDistributions", running.success() ? running.output().lines().filter(s -> !s.isBlank()).toList() : List.of());
        if (!running.success() || running.output().lines().noneMatch(properties.wslDistribution()::equals)) {
            data.put("error", "WSL 发行版未运行：" + properties.wslDistribution());
            snapshot = data; return;
        }
        var summary = linux("sh", "-c", "printf 'SYSTEM\\n'; uname -srmo; hostname -I; "
                + "printf '\\nUPTIME\\n'; uptime; printf '\\nMEMORY\\n'; free -b; "
                + "printf '\\nFILESYSTEMS\\n'; df -h -x tmpfs -x devtmpfs; "
                + "printf '\\nPROCESSES (CPU)\\n'; ps -eo pid,ppid,user,comm,pcpu,pmem,rss,etimes --sort=-pcpu; "
                + "printf '\\nLISTENING PORTS\\n'; ss -lntup");
        data.put("system", summary.success() ? summary.output() : summary.error());
        var resources = kubectl("get", OVERVIEW_RESOURCES, "--all-namespaces", "-o", "json");
        if (resources.success()) {
            try { data.put("items", sanitized(mapper.readTree(resources.output()).path("items"))); }
            catch (Exception e) { data.put("error", "无法解析 k3s 数据：" + e.getMessage()); }
        } else { data.put("error", resources.error().isBlank() ? resources.output() : resources.error()); }
        if (resourceTypes.isEmpty() && resources.success()) {
            var discovery = kubectl("api-resources", "--verbs=list", "-o", "name");
            if (discovery.success()) resourceTypes = Set.copyOf(discovery.output().lines()
                    .filter(s -> s.matches("[a-z0-9.-]+" )).toList());
            data.put("resourceTypes", resourceTypes.stream().sorted().toList());
        }
        data.put("nodeMetrics", text(kubectl("top", "nodes")));
        data.put("podMetrics", text(kubectl("top", "pods", "--all-namespaces", "--sort-by=cpu")));
        snapshot = data;
    }

    public Object resources(String resource, String namespace) {
        if (!resourceTypes.contains(resource)) throw new IllegalArgumentException("请选择已发现的资源类型");
        List<String> args = new ArrayList<>(List.of("get", resource, "-o", "json"));
        if (namespace == null || namespace.isBlank()) args.add("--all-namespaces");
        else { name(namespace); args.addAll(List.of("--namespace", namespace)); }
        var result = kubectl(args.toArray(String[]::new));
        if (!result.success()) return Map.of("error", text(result));
        try { return sanitized(mapper.readTree(result.output())); }
        catch (Exception e) { return Map.of("error", "无法解析资源数据"); }
    }

    public Map<String, Object> logs(String namespace, String pod, String container) {
        name(namespace); name(pod);
        List<String> args = new ArrayList<>(List.of("logs", pod, "--namespace", namespace,
                "--tail=200", "--timestamps=true", "--limit-bytes=131072"));
        if (container != null && !container.isBlank()) { name(container); args.addAll(List.of("--container", container)); }
        var result = kubectl(args.toArray(String[]::new));
        return Map.of("success", result.success(), "text", text(result));
    }

    static void name(String name) {
        if (name == null || !name.matches("[a-z0-9][a-z0-9.-]{0,252}"))
            throw new IllegalArgumentException("无效的 Kubernetes 名称");
    }

    private CommandRunner.Result linux(String... args) {
        List<String> command = new ArrayList<>(List.of("wsl.exe", "--distribution", properties.wslDistribution(),
                "--user", properties.wslUser(), "--exec"));
        command.addAll(List.of(args)); return commands.run(command, Duration.ofSeconds(12));
    }

    private CommandRunner.Result kubectl(String... args) {
        List<String> command = new ArrayList<>(List.of("kubectl", "--request-timeout=8s"));
        command.addAll(List.of(args)); return linux(command.toArray(String[]::new));
    }

    private static String text(CommandRunner.Result result) {
        return result.success() ? result.output() : (result.error().isBlank() ? result.output() : result.error());
    }

    /** 不把 Secret 值、明文凭据或 kubectl 保存的原始清单发送给浏览器。 */
    Object sanitized(JsonNode node) {
        if (node.isArray()) {
            List<Object> list = new ArrayList<>(); node.forEach(n -> list.add(sanitized(n))); return list;
        }
        if (node.isObject()) {
            Map<String, Object> map = new LinkedHashMap<>();
            boolean secret = "Secret".equals(node.path("kind").asString(""));
            boolean credential = sensitive(node.path("name").asString(""));
            node.properties().forEach(entry -> {
                String key = entry.getKey();
                if (key.equals("managedFields") || key.equals("kubectl.kubernetes.io/last-applied-configuration")) return;
                boolean mask = sensitive(key) || (secret && (key.equals("data") || key.equals("stringData")))
                        || (credential && key.equals("value"));
                map.put(key, mask ? "[已隐藏]" : sanitized(entry.getValue()));
            }); return map;
        }
        if (node.isNull()) return null;
        if (node.isBoolean()) return node.asBoolean();
        if (node.isNumber()) return node.numberValue();
        return node.asString();
    }

    private static boolean sensitive(String name) {
        String key = name.toLowerCase(Locale.ROOT);
        return key.contains("password") || key.contains("secret") || key.contains("token")
                || key.contains("credential") || key.contains("api-key") || key.contains("apikey")
                || key.contains("private-key") || key.contains("privatekey");
    }
}
