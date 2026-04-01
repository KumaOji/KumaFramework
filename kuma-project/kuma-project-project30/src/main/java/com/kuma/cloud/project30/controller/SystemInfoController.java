package com.kuma.cloud.project30.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.NetworkIF;
import oshi.software.os.OSFileStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统信息查询接口
 *
 * <pre>
 *   GET /sys/cpu      CPU 使用率 + 负载均值
 *   GET /sys/memory   物理内存使用情况
 *   GET /sys/disk     各挂载点磁盘使用情况
 *   GET /sys/network  各网卡流量统计
 *   GET /sys/all      汇总以上全部
 * </pre>
 *
 * <p>Prometheus 格式指标请访问：{@code GET /actuator/prometheus}
 */
@RestController
@RequestMapping("/sys")
@RequiredArgsConstructor
public class SystemInfoController {

    private final SystemInfo si = new SystemInfo();

    @GetMapping("/cpu")
    public Map<String, Object> cpu() {
        CentralProcessor cpu = si.getHardware().getProcessor();
        double usage = cpu.getSystemCpuLoad(500);
        double[] loadAvg = cpu.getSystemLoadAverage(3);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("usagePercent", pct(usage));
        result.put("loadAvg1m",  round(loadAvg[0]));
        result.put("loadAvg5m",  round(loadAvg[1]));
        result.put("loadAvg15m", round(loadAvg[2]));
        result.put("logicalCores",  cpu.getLogicalProcessorCount());
        result.put("physicalCores", cpu.getPhysicalProcessorCount());
        return result;
    }

    @GetMapping("/memory")
    public Map<String, Object> memory() {
        GlobalMemory mem = si.getHardware().getMemory();
        long total = mem.getTotal();
        long available = mem.getAvailable();
        long used = total - available;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalMB",     mb(total));
        result.put("usedMB",      mb(used));
        result.put("availableMB", mb(available));
        result.put("usagePercent", total == 0 ? 0 : pct((double) used / total));
        return result;
    }

    @GetMapping("/disk")
    public List<Map<String, Object>> disk() {
        List<OSFileStore> stores = si.getOperatingSystem().getFileSystem().getFileStores();
        List<Map<String, Object>> list = new ArrayList<>();
        for (OSFileStore store : stores) {
            long total = store.getTotalSpace();
            long free  = store.getFreeSpace();
            long used  = total - free;
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("mount",        store.getMount());
            item.put("type",         store.getType());
            item.put("totalGB",      gb(total));
            item.put("usedGB",       gb(used));
            item.put("freeGB",       gb(free));
            item.put("usagePercent", total == 0 ? 0 : pct((double) used / total));
            list.add(item);
        }
        return list;
    }

    @GetMapping("/network")
    public List<Map<String, Object>> network() {
        List<NetworkIF> nics = si.getHardware().getNetworkIFs(true);
        List<Map<String, Object>> list = new ArrayList<>();
        for (NetworkIF nic : nics) {
            nic.updateAttributes();
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name",         nic.getName());
            item.put("displayName",  nic.getDisplayName());
            item.put("macAddress",   nic.getMacaddr());
            item.put("recvMB",       mb(nic.getBytesRecv()));
            item.put("sentMB",       mb(nic.getBytesSent()));
            item.put("packetsRecv",  nic.getPacketsRecv());
            item.put("packetsSent",  nic.getPacketsSent());
            item.put("speed",        nic.getSpeed());
            list.add(item);
        }
        return list;
    }

    @GetMapping("/all")
    public Map<String, Object> all() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("cpu",     cpu());
        result.put("memory",  memory());
        result.put("disk",    disk());
        result.put("network", network());
        return result;
    }

    // ---- 单位转换工具 ----

    private static double pct(double ratio) {
        return Math.round(ratio * 10000.0) / 100.0;
    }

    private static double mb(long bytes) {
        return Math.round(bytes / 1024.0 / 1024.0 * 100) / 100.0;
    }

    private static double gb(long bytes) {
        return Math.round(bytes / 1024.0 / 1024.0 / 1024.0 * 100) / 100.0;
    }

    private static double round(double v) {
        return v < 0 ? 0 : Math.round(v * 100.0) / 100.0;
    }
}
