package com.kuma.boot.jprofiler;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.jprofiler.autoconfigure.properties.JProfilerProperties;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 对 JProfiler {@code com.jprofiler.api.controller.Controller} 的编程式封装。
 *
 * <p>Controller 类随 JProfiler 安装目录的 bootstrap.jar 发布，不在 Maven 中央仓库，故全部通过反射调用：
 * 当 Controller 不在 classpath（未引入 bootstrap.jar）或 JVM 未以 JProfiler agent 启动时，
 * 所有方法均为安全的空操作并返回 {@code false}，不会影响业务。</p>
 *
 * <p>启动应用时挂载 agent 示例：</p>
 * <pre>-agentpath:/opt/jprofiler/bin/linux-x64/libjprofilerti.so=port=8849,nowait</pre>
 */
public class JProfilerService {

    private static final String CONTROLLER_CLASS = "com.jprofiler.api.controller.Controller";
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

    private final JProfilerProperties properties;
    private final Class<?> controller;

    public JProfilerService(JProfilerProperties properties) {
        this.properties = properties;
        this.controller = resolveController();
        if (this.controller == null) {
            LogUtils.warn("JProfiler Controller 未找到（缺少 bootstrap.jar 或未挂载 agent），"
                    + "JProfilerService 将以空操作模式运行。");
        }
    }

    private static Class<?> resolveController() {
        try {
            return Class.forName(CONTROLLER_CLASS);
        } catch (Throwable t) {
            return null;
        }
    }

    /** Controller 是否可用（bootstrap.jar 在 classpath）。 */
    public boolean isAvailable() {
        return controller != null;
    }

    /** 开始 CPU 录制。 */
    public boolean startCPURecording(boolean reset) {
        return invoke("startCPURecording", new Class<?>[]{boolean.class}, reset);
    }

    /** 停止 CPU 录制。 */
    public boolean stopCPURecording() {
        return invoke("stopCPURecording", new Class<?>[]{});
    }

    /** 开始内存分配录制。 */
    public boolean startAllocRecording(boolean reset) {
        return invoke("startAllocRecording", new Class<?>[]{boolean.class}, reset);
    }

    /** 停止内存分配录制。 */
    public boolean stopAllocRecording() {
        return invoke("stopAllocRecording", new Class<?>[]{});
    }

    /** 开始监视器（锁/线程阻塞）录制。 */
    public boolean startMonitorRecording() {
        return invoke("startMonitorRecording", new Class<?>[]{});
    }

    /** 停止监视器录制。 */
    public boolean stopMonitorRecording() {
        return invoke("stopMonitorRecording", new Class<?>[]{});
    }

    /** 触发一次堆内存 dump（用于内存分析）。 */
    public boolean triggerHeapDump() {
        return invoke("triggerHeapDump", new Class<?>[]{});
    }

    /** 在时间轴上添加书签，便于在快照中定位关键时刻。 */
    public boolean addBookmark(String description) {
        return invoke("addBookmark", new Class<?>[]{String.class}, description);
    }

    /**
     * 保存快照到指定文件（建议以 .jps 结尾）。
     *
     * @param file 目标文件
     * @return 是否成功调用
     */
    public boolean saveSnapshot(File file) {
        return invoke("saveSnapshot", new Class<?>[]{File.class}, file);
    }

    /**
     * 将快照保存到配置的 {@code snapshotDir}，文件名自动按前缀+时间戳生成。
     *
     * @return 保存的文件，未生效时返回 {@code null}
     */
    public File saveSnapshot() {
        String name = properties.getSnapshotPrefix() + "-" + LocalDateTime.now().format(TS) + ".jps";
        File file = Paths.get(properties.getSnapshotDir(), name).toFile();
        File dir = file.getParentFile();
        if (dir != null && !dir.exists() && !dir.mkdirs()) {
            LogUtils.warn("JProfiler 快照目录创建失败：{}", dir.getAbsolutePath());
        }
        return saveSnapshot(file) ? file : null;
    }

    private boolean invoke(String method, Class<?>[] paramTypes, Object... args) {
        if (controller == null) {
            return false;
        }
        try {
            Method m = controller.getMethod(method, paramTypes);
            m.invoke(null, args);
            return true;
        } catch (Throwable t) {
            LogUtils.warn("JProfiler Controller.{} 调用失败：{}", method, t.getMessage());
            return false;
        }
    }
}
