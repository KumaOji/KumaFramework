package com.kuma.cloud.console;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Pattern;

@Service
public class ProjectDiscovery {
    private static final Pattern MAIN = Pattern.compile("(?:^|\\s)(com\\.kuma\\.[\\w.]+Application)(?=\\s|$)");
    private static final Pattern JAR = Pattern.compile("(?:^|\\s)-jar\\s+\"?([^\"\\r\\n]+?\\.jar)(?=\"|\\s|$)");
    private final HostMonitor host;
    private final CommandRunner commands;
    private final tools.jackson.databind.ObjectMapper mapper;
    private volatile long sampledAt;
    private volatile List<Map<String, Object>> cached = List.of();

    public ProjectDiscovery(HostMonitor host, CommandRunner commands, tools.jackson.databind.ObjectMapper mapper) {
        this.host = host; this.commands = commands; this.mapper = mapper;
    }

    public synchronized List<Map<String, Object>> runningProjects() {
        if (System.currentTimeMillis() - sampledAt < 10000) return cached;
        Map<Long, TreeSet<Integer>> ports = listeningPorts(String.valueOf(host.snapshot().getOrDefault("ports", "")));
        List<Map<String, Object>> result = new ArrayList<>();
        if (System.getProperty("os.name").startsWith("Windows")) {
            // JDK ProcessHandle.Info doesn't expose command lines on Windows; CIM does.
            var query = commands.run(List.of("powershell", "-NoProfile", "-Command",
                    "[Console]::OutputEncoding = [Text.UTF8Encoding]::new($false); "
                    + "Get-CimInstance Win32_Process | Where-Object { $_.Name -in @('java.exe','javaw.exe') } "
                    + "| Select-Object ProcessId,ExecutablePath,CommandLine | ConvertTo-Json -Compress"), java.time.Duration.ofSeconds(8));
            if (query.success() && !query.output().isBlank()) {
                try {
                    var root = mapper.readTree(query.output());
                    var rows = root.isArray() ? root : mapper.createArrayNode().add(root);
                    for (var process : rows) {
                        String name = identify(process.path("CommandLine").asString(""));
                        if (name == null) continue;
                        long pid = process.path("ProcessId").asLong();
                        Map<String, Object> row = new LinkedHashMap<>();
                        row.put("name", name); row.put("pid", pid);
                        var info = ProcessHandle.of(pid).map(ProcessHandle::info);
                        row.put("startTime", info.flatMap(ProcessHandle.Info::startInstant).map(Object::toString).orElse(""));
                        row.put("cpuTime", info.flatMap(ProcessHandle.Info::totalCpuDuration).map(d -> d.toSeconds()).orElse(0L));
                        row.put("ports", ports.getOrDefault(pid, new TreeSet<>()));
                        row.put("executable", process.path("ExecutablePath").asString("")); result.add(row);
                    }
                } catch (Exception e) { throw new IllegalArgumentException("无法读取本机项目进程：" + e.getMessage()); }
            } else if (!query.success()) { throw new IllegalArgumentException("本机项目发现失败：" + query.error()); }
            cached = result; sampledAt = System.currentTimeMillis(); return cached;
        }
        ProcessHandle.allProcesses().forEach(process -> {
            var info = process.info();
            String command = info.commandLine().orElse("");
            String name = identify(command);
            if (name == null) return;
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("name", name); row.put("pid", process.pid());
            row.put("startTime", info.startInstant().map(Object::toString).orElse(""));
            row.put("cpuTime", info.totalCpuDuration().map(d -> d.toSeconds()).orElse(0L));
            row.put("ports", ports.getOrDefault(process.pid(), new TreeSet<>()));
            row.put("executable", info.command().orElse(""));
            result.add(row);
        });
        cached = result.stream().sorted(java.util.Comparator.comparing(x -> x.get("name").toString())).toList();
        sampledAt = System.currentTimeMillis(); return cached;
    }

    static String identify(String command) {
        var main = MAIN.matcher(command);
        if (main.find()) return main.group(1);
        var jar = JAR.matcher(command);
        if (jar.find()) {
            String path = jar.group(1).replace('\\', '/');
            String file = path.substring(path.lastIndexOf('/') + 1);
            if (path.toLowerCase(Locale.ROOT).contains("kumaframework") || file.startsWith("kuma-")) return file;
        }
        return null;
    }

    static Map<Long, TreeSet<Integer>> listeningPorts(String text) {
        Map<Long, TreeSet<Integer>> result = new HashMap<>();
        for (String line : text.lines().toList()) {
            String[] fields = line.strip().split("\\s+");
            if (fields.length < 5 || !fields[0].equalsIgnoreCase("TCP") || !fields[3].equals("LISTENING")) continue;
            try {
                int port = Integer.parseInt(fields[1].substring(fields[1].lastIndexOf(':') + 1));
                long pid = Long.parseLong(fields[4]);
                result.computeIfAbsent(pid, ignored -> new TreeSet<>()).add(port);
            } catch (NumberFormatException ignored) { }
        }
        return result;
    }
}
