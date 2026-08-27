package com.kuma.cloud.project30.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import org.springframework.stereotype.Component;

/**
 * 系统资源指标注册器
 *
 * <p>使用 OSHI 采集四类系统指标并注册为 Micrometer Gauge，
 * Prometheus 每次抓取时实时读取，无需额外轮询线程。
 *
 * <h3>指标列表</h3>
 * <pre>
 * ── CPU ──────────────────────────────────────────────────────────
 *   system.cpu.usage           系统整体 CPU 使用率 [0,1]
 *   system.cpu.load.avg.1m     1  分钟负载均值
 *   system.cpu.load.avg.5m     5  分钟负载均值
 *   system.cpu.load.avg.15m    15 分钟负载均值
 *   system.cpu.logical.count   逻辑核数
 *   system.cpu.physical.count  物理核数
 *
 * ── 内存 ──────────────────────────────────────────────────────────
 *   system.memory.total        物理内存总量（字节）
 *   system.memory.available    可用物理内存（字节）
 *   system.memory.used         已用物理内存（字节）
 *   system.memory.usage        内存使用率 [0,1]
 *
 * ── 硬盘（每挂载点带 mount 标签）─────────────────────────────────────
 *   system.disk.total          总容量（字节）
 *   system.disk.free           剩余空间（字节）
 *   system.disk.used           已用空间（字节）
 *   system.disk.usage          使用率 [0,1]
 *
 * ── 网络（每网卡带 interface 标签）────────────────────────────────────
 *   system.network.bytes.recv  累计接收字节数
 *   system.network.bytes.sent  累计发送字节数
 *   system.network.packets.recv 累计接收包数
 *   system.network.packets.sent 累计发送包数
 * </pre>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SystemMetricsRegistrar {

    private final MeterRegistry registry;

    // OSHI 入口（线程安全，复用同一实例）
    private final SystemInfo si = new SystemInfo();

    @PostConstruct
    public void register() {
        HardwareAbstractionLayer hal = si.getHardware();
        OperatingSystem os = si.getOperatingSystem();

        registerCpu(hal.getProcessor());
        registerMemory(hal.getMemory());
        registerDisk(os.getFileSystem().getFileStores());
        registerNetwork(hal.getNetworkIFs(true)); // true = 只返回操作系统可见网卡
        log.info("[project30] 系统指标注册完成：CPU / 内存 / 硬盘 / 网络");
    }

    // ---------------------------------------------------------------- //
    //  CPU
    // ---------------------------------------------------------------- //
    private void registerCpu(CentralProcessor cpu) {
        // oshi. 前缀避免与 Micrometer 内置 ProcessorMetrics 的 system.cpu.usage 冲突
        Gauge.builder("oshi.cpu.usage", cpu,
                        p -> p.getSystemCpuLoad(500))
                .description("系统整体 CPU 使用率 [0,1]（OSHI 采集）")
                .register(registry);

        double[] load = cpu.getSystemLoadAverage(3);
        Gauge.builder("oshi.cpu.load.avg.1m", load, a -> a[0] < 0 ? 0 : a[0])
                .description("1 分钟负载均值")
                .register(registry);
        Gauge.builder("oshi.cpu.load.avg.5m", load, a -> a[1] < 0 ? 0 : a[1])
                .description("5 分钟负载均值")
                .register(registry);
        Gauge.builder("oshi.cpu.load.avg.15m", load, a -> a[2] < 0 ? 0 : a[2])
                .description("15 分钟负载均值")
                .register(registry);

        Gauge.builder("oshi.cpu.logical.count", cpu,
                        p -> (double) p.getLogicalProcessorCount())
                .description("逻辑核数")
                .register(registry);
        Gauge.builder("oshi.cpu.physical.count", cpu,
                        p -> (double) p.getPhysicalProcessorCount())
                .description("物理核数")
                .register(registry);
    }

    // ---------------------------------------------------------------- //
    //  内存
    // ---------------------------------------------------------------- //
    private void registerMemory(GlobalMemory mem) {
        Gauge.builder("system.memory.total", mem,
                        m -> (double) m.getTotal())
                .description("物理内存总量（字节）")
                .baseUnit("bytes")
                .register(registry);

        Gauge.builder("system.memory.available", mem,
                        m -> (double) m.getAvailable())
                .description("可用物理内存（字节）")
                .baseUnit("bytes")
                .register(registry);

        Gauge.builder("system.memory.used", mem,
                        m -> (double) (m.getTotal() - m.getAvailable()))
                .description("已用物理内存（字节）")
                .baseUnit("bytes")
                .register(registry);

        Gauge.builder("system.memory.usage", mem,
                        m -> m.getTotal() == 0 ? 0.0
                                : (double) (m.getTotal() - m.getAvailable()) / m.getTotal())
                .description("内存使用率 [0,1]")
                .register(registry);
    }

    // ---------------------------------------------------------------- //
    //  硬盘（每挂载点）
    // ---------------------------------------------------------------- //
    private void registerDisk(List<OSFileStore> stores) {
        for (OSFileStore store : stores) {
            String mount = store.getMount();

            Gauge.builder("system.disk.total", store,
                            s -> (double) s.getTotalSpace())
                    .description("磁盘总容量（字节）")
                    .tag("mount", mount)
                    .baseUnit("bytes")
                    .register(registry);

            Gauge.builder("system.disk.free", store,
                            s -> (double) s.getFreeSpace())
                    .description("磁盘剩余空间（字节）")
                    .tag("mount", mount)
                    .baseUnit("bytes")
                    .register(registry);

            Gauge.builder("system.disk.used", store,
                            s -> (double) (s.getTotalSpace() - s.getFreeSpace()))
                    .description("磁盘已用空间（字节）")
                    .tag("mount", mount)
                    .baseUnit("bytes")
                    .register(registry);

            Gauge.builder("system.disk.usage", store,
                            s -> s.getTotalSpace() == 0 ? 0.0
                                    : (double) (s.getTotalSpace() - s.getFreeSpace()) / s.getTotalSpace())
                    .description("磁盘使用率 [0,1]")
                    .tag("mount", mount)
                    .register(registry);
        }
    }

    // ---------------------------------------------------------------- //
    //  网络（每物理网卡）
    // ---------------------------------------------------------------- //
    private void registerNetwork(List<NetworkIF> nics) {
        for (NetworkIF nic : nics) {
            String name = nic.getName();

            Gauge.builder("system.network.bytes.recv", nic, n -> {
                        n.updateAttributes();
                        return (double) n.getBytesRecv();
                    })
                    .description("网卡累计接收字节数")
                    .tag("interface", name)
                    .baseUnit("bytes")
                    .register(registry);

            Gauge.builder("system.network.bytes.sent", nic, n -> {
                        n.updateAttributes();
                        return (double) n.getBytesSent();
                    })
                    .description("网卡累计发送字节数")
                    .tag("interface", name)
                    .baseUnit("bytes")
                    .register(registry);

            Gauge.builder("system.network.packets.recv", nic, n -> {
                        n.updateAttributes();
                        return (double) n.getPacketsRecv();
                    })
                    .description("网卡累计接收包数")
                    .tag("interface", name)
                    .register(registry);

            Gauge.builder("system.network.packets.sent", nic, n -> {
                        n.updateAttributes();
                        return (double) n.getPacketsSent();
                    })
                    .description("网卡累计发送包数")
                    .tag("interface", name)
                    .register(registry);
        }
    }
}
