package com.kuma.cloud.console;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WslInspector {
    private final CommandRunner commands;
    private final ConsoleProperties properties;
    private final Map<String, Map<String, Object>> cache = new ConcurrentHashMap<>();

    public WslInspector(CommandRunner commands, ConsoleProperties properties) {
        this.commands = commands; this.properties = properties;
    }

    public List<String> running() {
        var result = commands.run(List.of("wsl.exe", "--list", "--running", "--quiet"), Duration.ofSeconds(5));
        return result.success() ? result.output().lines().filter(s -> !s.isBlank()).toList() : List.of();
    }

    public synchronized Object inspect(String distribution) {
        if (!running().contains(distribution)) throw new IllegalArgumentException("请选择正在运行的 WSL 发行版");
        var cached = cache.get(distribution);
        if (cached != null && Instant.parse((String) cached.get("sampledAt")).isAfter(Instant.now().minusSeconds(15)))
            return cached;
        // All shell fragments are fixed application code; the distro is a separate argument.
        String script = "printf '@@SYSTEM\\n'; cat /etc/os-release; uname -srmo; hostname -I; uptime; "
                + "printf '\\n@@CPU\\n'; lscpu; vmstat 1 2; "
                + "printf '\\n@@MEMORY\\n'; free -h; cat /proc/meminfo; "
                + "printf '\\n@@DISKS\\n'; lsblk -o NAME,TYPE,SIZE,FSTYPE,MOUNTPOINTS; df -h -x tmpfs -x devtmpfs; "
                + "printf '\\n@@NETWORK\\n'; ip -br addr; cat /proc/net/dev; "
                + "printf '\\n@@PROCESSES\\n'; ps -eo pid,ppid,user,comm,pcpu,pmem,rss,etimes --sort=-pcpu; "
                + "printf '\\n@@PORTS\\n'; ss -lntup; "
                + "printf '\\n@@SERVICES\\n'; systemctl list-units --type=service --all --no-pager --no-legend 2>&1; true";
        List<String> command = new ArrayList<>(List.of("wsl.exe", "--distribution", distribution, "--user",
                properties.wslUser(), "--exec", "sh", "-c", script));
        var result = commands.run(command, Duration.ofSeconds(20));
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("distribution", distribution); data.put("sampledAt", Instant.now().toString());
        data.put("success", result.success()); data.put("error", result.success() ? "" : result.error());
        Map<String, String> sections = new LinkedHashMap<>();
        for (String part : result.output().split("@@")) {
            int newline = part.indexOf('\n');
            if (newline > 0) sections.put(part.substring(0, newline).strip(), part.substring(newline + 1).strip());
        }
        data.put("sections", sections); cache.put(distribution, data); return data;
    }
}
