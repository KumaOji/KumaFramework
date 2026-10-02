package com.kuma.cloud.console;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.software.os.OSProcess;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class HostMonitor {
    private static final Logger log = LoggerFactory.getLogger(HostMonitor.class);
    private final SystemInfo info = new SystemInfo();
    private long[] ticks;
    private long[][] coreTicks;
    private long lastSampleNanos;
    private Map<Integer, OSProcess> previousProcesses = new HashMap<>();
    private Map<String, long[]> previousNetwork = new HashMap<>();
    private volatile Map<String, Object> snapshot = Map.of("ready", false);
    private volatile Map<String, Object> inventory = Map.of();
    private volatile String ports = "正在读取本机监听端口…";
    private final CommandRunner commands;
    private final tools.jackson.databind.ObjectMapper mapper;
    private volatile Object services = List.of();
    private volatile String gpuMetrics = "正在查询 GPU 驱动指标…";

    public HostMonitor(CommandRunner commands, tools.jackson.databind.ObjectMapper mapper) {
        this.commands = commands; this.mapper = mapper;
    }

    public Map<String, Object> snapshot() { return snapshot; }

    @Scheduled(fixedDelay = 2000)
    public synchronized void sample() {
        try {
            var hardware = info.getHardware();
            var os = info.getOperatingSystem();
            CentralProcessor cpu = hardware.getProcessor();
            long now = System.nanoTime();
            double seconds = lastSampleNanos == 0 ? 0 : (now - lastSampleNanos) / 1_000_000_000d;
            Double cpuPercent = ticks == null ? null : percent(cpu.getSystemCpuLoadBetweenTicks(ticks));
            double[] cores = coreTicks == null ? new double[0] : cpu.getProcessorCpuLoadBetweenTicks(coreTicks);
            ticks = cpu.getSystemCpuLoadTicks();
            coreTicks = cpu.getProcessorCpuLoadTicks();
            lastSampleNanos = now;
            var memory = hardware.getMemory();
            var virtual = memory.getVirtualMemory();
            List<Map<String, Object>> disks = new ArrayList<>();
            for (var disk : os.getFileSystem().getFileStores()) {
                disks.add(Map.of("name", disk.getName(), "mount", disk.getMount(), "type", disk.getType(),
                        "total", disk.getTotalSpace(), "free", disk.getUsableSpace()));
            }
            List<Map<String, Object>> networks = new ArrayList<>();
            Map<String, long[]> nextNetwork = new HashMap<>();
            for (var network : hardware.getNetworkIFs()) {
                network.updateAttributes();
                long received = network.getBytesRecv(), sent = network.getBytesSent();
                long[] before = previousNetwork.get(network.getName());
                double down = before == null || seconds <= 0 ? 0 : Math.max(0, received - before[0]) / seconds;
                double up = before == null || seconds <= 0 ? 0 : Math.max(0, sent - before[1]) / seconds;
                networks.add(Map.of("name", network.getName(), "displayName", network.getDisplayName(),
                        "ipv4", List.of(network.getIPv4addr()), "mac", network.getMacaddr(),
                        "received", received, "sent", sent, "download", down, "upload", up));
                nextNetwork.put(network.getName(), new long[]{received, sent});
            }
            previousNetwork = nextNetwork;
            List<Map<String, Object>> processes = new ArrayList<>();
            Map<Integer, OSProcess> nextProcesses = new HashMap<>();
            for (var process : os.getProcesses()) {
                if (process.getProcessID() == 0) continue;
                OSProcess previous = previousProcesses.get(process.getProcessID());
                Double load = previous == null || previous.getStartTime() != process.getStartTime()
                        ? null : percent(process.getProcessCpuLoadBetweenTicks(previous) / cpu.getLogicalProcessorCount());
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("pid", process.getProcessID()); row.put("name", process.getName());
                row.put("cpu", load); row.put("memory", process.getResidentSetSize());
                row.put("threads", process.getThreadCount()); row.put("state", process.getState().name());
                row.put("uptime", process.getUpTime() / 1000);
                processes.add(row); nextProcesses.put(process.getProcessID(), process);
            }
            previousProcesses = nextProcesses;
            Map<String, Object> cpuData = new LinkedHashMap<>();
            cpuData.put("name", cpu.getProcessorIdentifier().getName()); cpuData.put("percent", cpuPercent);
            cpuData.put("physicalCores", cpu.getPhysicalProcessorCount());
            cpuData.put("logicalCores", cpu.getLogicalProcessorCount());
            cpuData.put("cores", java.util.Arrays.stream(cores).map(HostMonitor::percent).toArray());
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("ready", true); data.put("sampledAt", Instant.now().toString());
            data.put("hostname", os.getNetworkParams().getHostName()); data.put("os", os.toString());
            data.put("uptime", os.getSystemUptime()); data.put("cpu", cpuData);
            data.put("memory", Map.of("total", memory.getTotal(), "available", memory.getAvailable(),
                    "swapTotal", virtual.getSwapTotal(), "swapUsed", virtual.getSwapUsed()));
            data.put("disks", disks); data.put("networks", networks); data.put("processes", processes);
            data.put("processCount", os.getProcessCount()); data.put("threadCount", os.getThreadCount());
            data.put("java", System.getProperty("java.version"));
            data.put("hardware", inventory); data.put("ports", ports);
            data.put("services", services); data.put("gpuMetrics", gpuMetrics);
            data.put("dns", List.of(os.getNetworkParams().getDnsServers()));
            data.put("error", ""); snapshot = data;
        } catch (Exception e) {
            log.warn("本机采样失败: {}", e.getMessage());
            Map<String, Object> failed = new LinkedHashMap<>(snapshot);
            failed.put("error", e.getMessage() == null ? "本机采样失败" : e.getMessage()); snapshot = failed;
        }
    }

    @Scheduled(fixedDelay = 30000)
    public void inventory() {
        try {
            var hardware = info.getHardware(); var computer = hardware.getComputerSystem();
            var board = computer.getBaseboard(); var firmware = computer.getFirmware();
            List<Object> graphics = new ArrayList<>();
            hardware.getGraphicsCards().forEach(gpu -> graphics.add(Map.of("name", gpu.getName(),
                    "vendor", gpu.getVendor(), "vram", gpu.getVRam(), "driver", gpu.getVersionInfo())));
            List<Object> modules = new ArrayList<>();
            hardware.getMemory().getPhysicalMemory().forEach(module -> modules.add(Map.of("bank", module.getBankLabel(),
                    "type", module.getMemoryType(), "capacity", module.getCapacity(), "speed", module.getClockSpeed(),
                    "manufacturer", module.getManufacturer())));
            List<Object> batteries = new ArrayList<>();
            hardware.getPowerSources().forEach(battery -> batteries.add(Map.of("name", battery.getName(),
                    "percent", percent(battery.getRemainingCapacityPercent()), "charging", battery.isCharging(),
                    "powerOnline", battery.isPowerOnLine())));
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("computer", computer.getManufacturer() + " " + computer.getModel());
            data.put("cpu", hardware.getProcessor().getProcessorIdentifier().getName());
            data.put("board", board.getManufacturer() + " " + board.getModel());
            data.put("firmware", firmware.getName() + " " + firmware.getVersion() + " · " + firmware.getReleaseDate());
            data.put("graphics", graphics); data.put("memoryModules", modules); data.put("batteries", batteries);
            double temperature = hardware.getSensors().getCpuTemperature();
            data.put("cpuTemperature", temperature > 0 && Double.isFinite(temperature) ? temperature : null);
            data.put("fans", hardware.getSensors().getFanSpeeds()); inventory = data;
        } catch (Exception e) { log.debug("硬件详细信息暂不可用: {}", e.getMessage()); }
        if (System.getProperty("os.name").startsWith("Windows")) {
            var result = commands.run(List.of("netstat", "-ano", "-p", "tcp"), java.time.Duration.ofSeconds(5));
            ports = result.success() ? result.output() : result.error();
            var windowsServices = commands.run(List.of("powershell", "-NoProfile", "-Command",
                    "[Console]::OutputEncoding = [Text.UTF8Encoding]::new($false); Get-CimInstance Win32_Service | Select-Object Name,DisplayName,State,StartMode,ProcessId | ConvertTo-Json -Compress"),
                    java.time.Duration.ofSeconds(10));
            if (windowsServices.success()) {
                try { services = mapper.readValue(windowsServices.output(), Object.class); }
                catch (Exception e) { services = List.of(); }
            }
        }
        var gpu = commands.run(List.of("nvidia-smi", "--query-gpu=name,utilization.gpu,utilization.memory,memory.total,memory.used,temperature.gpu",
                "--format=csv,noheader,nounits"), java.time.Duration.ofSeconds(5));
        gpuMetrics = gpu.success() ? "名称, GPU 使用率 %, 显存控制器使用率 %, 总显存 MiB, 已用显存 MiB, 温度 °C\n" + gpu.output()
                : "当前驱动未提供 nvidia-smi 指标；显卡型号与显存容量见硬件信息。";
    }

    static double percent(double load) {
        return Double.isFinite(load) ? Math.clamp(load * 100, 0, 100) : 0;
    }
}
