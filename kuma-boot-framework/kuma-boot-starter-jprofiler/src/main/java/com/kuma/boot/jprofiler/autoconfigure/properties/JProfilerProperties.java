package com.kuma.boot.jprofiler.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * JProfiler 性能剖析配置。
 *
 * <p>本模块提供编程式的 {@code JProfilerService}，用于在运行期触发 CPU/内存录制与快照保存。
 * 真正的剖析能力依赖应用以 JProfiler agent 启动：
 * {@code -agentpath:/path/to/jprofiler/bin/linux-x64/libjprofilerti.so=port=8849}，
 * 否则相关操作为空操作（no-op）。</p>
 */
@RefreshScope
@ConfigurationProperties(prefix = JProfilerProperties.PREFIX)
public class JProfilerProperties {

    public static final String PREFIX = "kuma.boot.jprofiler";

    /** 是否启用 JProfiler 集成（注册 JProfilerService），默认关闭。 */
    private boolean enabled = false;

    /** 快照保存目录，saveSnapshot() 不指定文件名时使用，默认当前工作目录。 */
    private String snapshotDir = ".";

    /** 保存的快照文件名前缀。 */
    private String snapshotPrefix = "kuma";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getSnapshotDir() {
        return snapshotDir;
    }

    public void setSnapshotDir(String snapshotDir) {
        this.snapshotDir = snapshotDir;
    }

    public String getSnapshotPrefix() {
        return snapshotPrefix;
    }

    public void setSnapshotPrefix(String snapshotPrefix) {
        this.snapshotPrefix = snapshotPrefix;
    }
}
