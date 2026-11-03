package com.kuma.cloud.console;

import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/** Read-only diagnostics for the fixed local router address. */
@Service
public class RouterDiagnostics {
    private final CommandRunner commands;
    private final NetworkDiagnostics network;
    private final ObjectMapper mapper;
    private final AtomicBoolean testing = new AtomicBoolean();

    public RouterDiagnostics(CommandRunner commands, NetworkDiagnostics network, ObjectMapper mapper) {
        this.commands = commands;
        this.network = network;
        this.mapper = mapper;
    }

    public Map<String, Object> inspect() {
        if (!testing.compareAndSet(false, true)) throw new IllegalArgumentException("路由器检测正在进行，请稍后重试");
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var http = executor.submit(() -> network.probe("HTTP 管理入口", "http://192.168.0.1/", "直连", ""));
            var https = executor.submit(() -> network.probe("HTTPS 管理入口", "https://192.168.0.1/", "直连", ""));
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("address", "192.168.0.1");
            data.put("local", Map.of());
            if (System.getProperty("os.name").startsWith("Windows")) {
                var result = commands.run(NetworkDiagnostics.powershellCommand("""
                        [Console]::OutputEncoding = [Text.UTF8Encoding]::new($false)
                        $ErrorActionPreference = 'Stop'
                        $ping = [Net.NetworkInformation.Ping]::new()
                        $reply = $null
                        try { $reply = $ping.Send('192.168.0.1', 2000) } catch {} finally { $ping.Dispose() }
                        $neighbors = @(Get-NetNeighbor -IPAddress '192.168.0.1' -ErrorAction SilentlyContinue | ForEach-Object {
                          @{interface=$_.InterfaceAlias;index=$_.InterfaceIndex;mac=$_.LinkLayerAddress;state=[string]$_.State}
                        })
                        $routes = @(Get-NetRoute -AddressFamily IPv4 -ErrorAction SilentlyContinue | Where-Object { $_.NextHop -eq '192.168.0.1' } | ForEach-Object {
                          @{interface=$_.InterfaceAlias;index=$_.InterfaceIndex;destination=$_.DestinationPrefix;metric=$_.RouteMetric}
                        })
                        @{pingStatus=$(if ($reply) { [string]$reply.Status } else { 'Unavailable' });pingMs=$(if ($reply -and $reply.Status -eq 'Success') { $reply.RoundtripTime } else { $null });neighbors=$neighbors;routes=$routes} | ConvertTo-Json -Depth 6 -Compress
                        """), Duration.ofSeconds(12));
                if (result.success()) {
                    try { data.put("local", mapper.readValue(result.output(), Map.class)); }
                    catch (Exception e) { data.put("warning", "路由器本地网络信息解析失败"); }
                } else data.put("warning", "路由器本地网络信息读取失败：" + result.error());
            } else data.put("warning", "当前平台仅检测路由器管理入口；邻居表、路由及 Ping 信息仅支持 Windows。");
            try { data.put("services", List.of(http.get(), https.get())); }
            catch (Exception e) { throw new IllegalStateException("路由器检测中断", e); }
            var snapshot = network.snapshot();
            data.put("adapters", snapshot.getOrDefault("adapters", List.of()));
            data.put("adaptersSampledAt", snapshot.getOrDefault("sampledAt", ""));
            String inventoryWarning = snapshot.containsKey("error") ? String.valueOf(snapshot.get("error"))
                    : !Boolean.TRUE.equals(snapshot.get("ready")) ? "本机网卡信息尚未完成采样，请稍后刷新。" : "";
            if (!inventoryWarning.isBlank())
                data.put("warning", data.containsKey("warning") ? data.get("warning") + "；" + inventoryWarning : inventoryWarning);
            data.put("testedAt", Instant.now().toString());
            return data;
        } finally { testing.set(false); }
    }
}
