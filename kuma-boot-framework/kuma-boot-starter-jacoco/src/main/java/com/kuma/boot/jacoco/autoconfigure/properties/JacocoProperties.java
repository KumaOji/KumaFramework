package com.kuma.boot.jacoco.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * JaCoCo 代码覆盖率配置。
 *
 * @author kuma
 */
@RefreshScope
@ConfigurationProperties(prefix = JacocoProperties.PREFIX)
public class JacocoProperties {

    public static final String PREFIX = "kuma.boot.jacoco";

    /** 是否启用 JaCoCo 模块。 */
    private boolean enabled = true;

    /** 覆盖率执行数据文件（.exec）路径，运行期 dump 及离线报告生成均以此为准。 */
    private String execFile = "build/jacoco/jacoco.exec";

    /** 已编译的 class 目录，用于离线报告分析。 */
    private String classesDir = "build/classes/java/main";

    /** 源码目录，用于离线报告关联源代码行。 */
    private String sourceDir = "src/main/java";

    /** HTML 报告输出目录。 */
    private String reportDir = "build/reports/jacoco";

    /** 报告标题。 */
    private String title = "Kuma Coverage Report";

    /** 应用关闭时是否自动 dump 运行期覆盖率数据到 {@link #execFile}。 */
    private boolean dumpOnShutdown = false;

    /** dump 运行期数据后是否重置 JaCoCo agent 内部计数。 */
    private boolean resetAfterDump = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getExecFile() {
        return execFile;
    }

    public void setExecFile(String execFile) {
        this.execFile = execFile;
    }

    public String getClassesDir() {
        return classesDir;
    }

    public void setClassesDir(String classesDir) {
        this.classesDir = classesDir;
    }

    public String getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
    }

    public String getReportDir() {
        return reportDir;
    }

    public void setReportDir(String reportDir) {
        this.reportDir = reportDir;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isDumpOnShutdown() {
        return dumpOnShutdown;
    }

    public void setDumpOnShutdown(boolean dumpOnShutdown) {
        this.dumpOnShutdown = dumpOnShutdown;
    }

    public boolean isResetAfterDump() {
        return resetAfterDump;
    }

    public void setResetAfterDump(boolean resetAfterDump) {
        this.resetAfterDump = resetAfterDump;
    }
}
