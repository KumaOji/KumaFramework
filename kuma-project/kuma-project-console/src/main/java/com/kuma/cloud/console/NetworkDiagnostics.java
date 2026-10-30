package com.kuma.cloud.console;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executors;

/** Read-only network inventory; external probes run only on explicit request. */
@Service
public class NetworkDiagnostics {
    private final CommandRunner commands;
    private final ObjectMapper mapper;
    private volatile Map<String, Object> snapshot = Map.of("ready", false);
    private volatile Map<String, String> probeProxies = Map.of();
    private final java.util.concurrent.atomic.AtomicBoolean testing = new java.util.concurrent.atomic.AtomicBoolean();
    static final Map<String, String> TARGETS = Collections.unmodifiableMap(new LinkedHashMap<>(Map.of(
            "百度", "https://www.baidu.com/", "GitHub", "https://github.com/",
            "Google", "https://www.google.com/", "Cloudflare", "https://www.cloudflare.com/")));

    public NetworkDiagnostics(CommandRunner commands, ObjectMapper mapper) {
        this.commands = commands; this.mapper = mapper;
    }

    public Map<String, Object> snapshot() { return snapshot; }

    @Scheduled(fixedDelay = 30000)
    public void sample() {
        Map<String, Object> data = new LinkedHashMap<>();
        Map<String, String> proxies = new LinkedHashMap<>();
        List<Map<String, String>> environment = new ArrayList<>();
        for (String key : List.of("HTTP_PROXY", "HTTPS_PROXY", "ALL_PROXY", "NO_PROXY", "http_proxy", "https_proxy", "all_proxy", "no_proxy")) {
            String value = System.getenv(key);
            if (value == null || value.isBlank()) continue;
            environment.add(Map.of("name", key, "value", key.equalsIgnoreCase("NO_PROXY") ? value : redact(value)));
            if (!key.equalsIgnoreCase("NO_PROXY") && validProxy(value)) proxies.put("环境变量 " + key, value);
        }
        data.put("environment", environment);
        data.put("adapters", List.of());
        data.put("system", Map.of());
        if (System.getProperty("os.name").startsWith("Windows")) {
            var result = commands.run(powershellCommand("""
                    [Console]::OutputEncoding = [Text.UTF8Encoding]::new($false)
                    $ErrorActionPreference = 'Stop'
                    $p = Get-ItemProperty 'HKCU:\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings'
                    $adapters = @(Get-NetAdapter -IncludeHidden | ForEach-Object {
                      $a = $_
                      $ip = @(Get-NetIPAddress -InterfaceIndex $a.ifIndex -ErrorAction SilentlyContinue)
                      $dns = @(Get-DnsClientServerAddress -InterfaceIndex $a.ifIndex -ErrorAction SilentlyContinue | ForEach-Object { $_.ServerAddresses })
                      $routes = @(Get-NetRoute -InterfaceIndex $a.ifIndex -ErrorAction SilentlyContinue | Where-Object { $_.DestinationPrefix -in @('0.0.0.0/0','::/0') } | ForEach-Object { $_.NextHop })
                      @{name=$a.Name;description=$a.InterfaceDescription;index=$a.ifIndex;status=[string]$a.Status;virtual=(!$a.HardwareInterface);hidden=[bool]$a.Hidden;mac=$a.MacAddress;speed=[string]$a.LinkSpeed;mtu=$a.MtuSize;ipv4=@($ip | Where-Object AddressFamily -eq IPv4 | ForEach-Object { "$($_.IPAddress)/$($_.PrefixLength)" });ipv6=@($ip | Where-Object AddressFamily -eq IPv6 | ForEach-Object { "$($_.IPAddress)/$($_.PrefixLength)" });dns=$dns;gateways=$routes}
                    })
                    @{adapters=$adapters;system=@{enabled=([int]$p.ProxyEnable -eq 1);server=[string]$p.ProxyServer;bypass=[string]$p.ProxyOverride;pac=[string]$p.AutoConfigURL};winhttp=(netsh winhttp show proxy | Out-String)} | ConvertTo-Json -Depth 7 -Compress
                    """), Duration.ofSeconds(20));
            if (result.success()) {
                try {
                    var root = mapper.readTree(result.output());
                    data.put("adapters", mapper.convertValue(root.path("adapters"), Object.class));
                    var system = root.path("system");
                    String server = system.path("server").asString("");
                    data.put("system", Map.of("enabled", system.path("enabled").asBoolean(), "server", redact(server),
                            "bypass", system.path("bypass").asString(""), "pac", redact(system.path("pac").asString(""))));
                    data.put("winhttp", root.path("winhttp").asString(""));
                    String proxy = windowsHttpsProxy(server);
                    if (system.path("enabled").asBoolean() && validProxy(proxy)) proxies.put("Windows 静态代理", proxy);
                } catch (Exception e) { data.put("error", "Windows 网络详情解析失败"); }
            } else data.put("error", "Windows 网络详情读取失败：" + result.error());
        } else data.put("note", "当前系统展示通用网卡信息及环境变量代理；Windows 代理与路由详情不可用。");
        data.put("proxyRoutes", proxies.keySet().stream().toList());
        data.put("ready", true); data.put("sampledAt", Instant.now().toString());
        probeProxies = Collections.unmodifiableMap(proxies); snapshot = Collections.unmodifiableMap(data);
    }

    static List<String> powershellCommand(String script) {
        // Windows command-line parsing can strip quotes inside a -Command argument.
        return List.of("powershell", "-NoProfile", "-EncodedCommand",
                Base64.getEncoder().encodeToString(script.getBytes(StandardCharsets.UTF_16LE)));
    }

    static String windowsHttpsProxy(String server) {
        if (server.isBlank()) return "";
        if (!server.contains("=")) return server.contains("://") ? server : "http://" + server;
        for (String entry : server.split(";")) {
            String[] pair = entry.trim().split("=", 2);
            if (pair.length == 2 && pair[0].equalsIgnoreCase("https"))
                return pair[1].contains("://") ? pair[1] : "http://" + pair[1];
        }
        return "";
    }

    static boolean validProxy(String value) {
        try {
            URI uri = URI.create(value);
            return Set.of("http", "https", "socks5", "socks5h", "socks4", "socks4a").contains(uri.getScheme())
                    && uri.getHost() != null && uri.getRawUserInfo() == null && uri.getRawQuery() == null && uri.getRawFragment() == null;
        } catch (Exception e) { return false; }
    }

    static String redact(String value) {
        return value.replaceAll("(?i)(://)[^/@;\\s]+@", "$1[已隐藏]@")
                .replaceAll("[?#].*", "[参数已隐藏]");
    }

    public Map<String, Object> test() {
        if (!testing.compareAndSet(false, true)) throw new IllegalArgumentException("网络测试正在进行，请稍后重试");
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Map<String, String> routes = new LinkedHashMap<>(); routes.put("直连", ""); routes.putAll(probeProxies);
            var tasks = new ArrayList<java.util.concurrent.Future<Map<String, Object>>>();
            // Identical proxy endpoints need only one comparison probe.
            Set<String> endpoints = new HashSet<>();
            routes.forEach((route, proxy) -> {
                if (endpoints.add(proxy)) TARGETS.forEach((name, url) -> tasks.add(executor.submit(() -> probe(name, url, route, proxy))));
            });
            List<Map<String, Object>> results = new ArrayList<>();
            for (var task : tasks) {
                try { results.add(task.get()); }
                catch (Exception e) { throw new IllegalStateException("网络测试中断", e); }
            }
            return Map.of("testedAt", Instant.now().toString(), "results", results);
        } finally { testing.set(false); }
    }

    Map<String, Object> probe(String name, String url, String route, String proxy) {
        boolean windows = System.getProperty("os.name").startsWith("Windows");
        var result = commands.run(List.of(windows ? "curl.exe" : "curl", "--disable", "--silent", "--show-error", "--head",
                "--output", windows ? "NUL" : "/dev/null", "--connect-timeout", "4", "--max-time", "8",
                "--proxy", proxy, "--noproxy", proxy.isEmpty() ? "*" : "", "--write-out", "%{json}", url), Duration.ofSeconds(10));
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("name", name); row.put("url", url); row.put("route", route); row.put("reachable", false);
        try {
            var json = mapper.readTree(result.output());
            int status = json.path("http_code").asInt(0);
            row.put("status", status); row.put("reachable", result.success() && status > 0);
            row.put("dnsMs", json.path("time_namelookup").asDouble(0) * 1000);
            row.put("connectMs", json.path("time_connect").asDouble(0) * 1000);
            row.put("totalMs", json.path("time_total").asDouble(0) * 1000);
            row.put("remoteIp", json.path("remote_ip").asString(""));
        } catch (Exception ignored) { /* Missing curl or an unsupported version is a failed probe. */ }
        row.put("error", result.success() ? "" : redact(result.error()));
        return row;
    }
}
