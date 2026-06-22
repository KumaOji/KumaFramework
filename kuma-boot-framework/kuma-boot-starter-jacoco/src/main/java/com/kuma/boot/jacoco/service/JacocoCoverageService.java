package com.kuma.boot.jacoco.service;

import com.kuma.boot.jacoco.autoconfigure.properties.JacocoProperties;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.tools.ExecFileLoader;
import org.jacoco.report.DirectorySourceFileLocator;
import org.jacoco.report.FileMultiReportOutput;
import org.jacoco.report.IReportVisitor;
import org.jacoco.report.html.HTMLFormatter;
import org.springframework.beans.factory.DisposableBean;

/**
 * JaCoCo 覆盖率服务。
 *
 * <p>提供两类能力：
 *
 * <ul>
 *   <li>运行期采集：通过 JaCoCo agent 运行时 API（{@code org.jacoco.agent.rt.RT}）抓取当前进程的执行数据并写入
 *       {@code .exec} 文件。需要应用启动时挂载 {@code -javaagent:jacocoagent.jar}，否则会抛出异常。
 *   <li>离线报告：基于 {@code .exec} 执行数据、已编译 class 与源码目录生成 HTML 覆盖率报告。
 * </ul>
 *
 * @author kuma
 */
public class JacocoCoverageService implements DisposableBean {

    private final JacocoProperties properties;

    public JacocoCoverageService(JacocoProperties properties) {
        this.properties = properties;
    }

    /**
     * 应用关闭时，若配置开启 {@link JacocoProperties#isDumpOnShutdown()}，自动 dump 运行期覆盖率数据。
     *
     * <p>未挂载 agent 时静默跳过，避免影响应用正常关闭流程。
     */
    @Override
    public void destroy() {
        if (!properties.isDumpOnShutdown()) {
            return;
        }
        try {
            dumpRuntimeCoverage();
        } catch (RuntimeException ignored) {
            // 关闭阶段尽力而为：未挂载 agent 或采集失败时不阻塞关闭流程
        }
    }

    /**
     * 抓取当前运行进程的 JaCoCo 执行数据，写入配置的 {@link JacocoProperties#getExecFile()}。
     *
     * <p>通过反射访问 JaCoCo agent 运行时 API，从而无需在编译期依赖 agent runtime jar；仅当应用以
     * {@code -javaagent} 方式挂载了 JaCoCo agent 时才可用。
     *
     * @return 写出的 exec 文件
     * @throws IllegalStateException 当 JaCoCo agent 未挂载或不可用时
     */
    public File dumpRuntimeCoverage() {
        return dumpRuntimeCoverage(new File(properties.getExecFile()), properties.isResetAfterDump());
    }

    /**
     * 抓取当前运行进程的 JaCoCo 执行数据并写入指定文件。
     *
     * @param target 目标 exec 文件
     * @param reset 抓取后是否重置 agent 内部计数
     * @return 写出的 exec 文件
     * @throws IllegalStateException 当 JaCoCo agent 未挂载或不可用时
     */
    public File dumpRuntimeCoverage(File target, boolean reset) {
        try {
            Class<?> rtClass = Class.forName("org.jacoco.agent.rt.RT");
            Object agent = rtClass.getMethod("getAgent").invoke(null);
            Method getExecutionData = agent.getClass().getMethod("getExecutionData", boolean.class);
            byte[] data = (byte[]) getExecutionData.invoke(agent, reset);

            File parent = target.getAbsoluteFile().getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                throw new IOException("无法创建目录: " + parent);
            }
            Files.write(target.toPath(), data);
            return target;
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                    "未检测到 JaCoCo agent，请在启动参数中加入 -javaagent:jacocoagent.jar 后再采集运行期覆盖率", e);
        } catch (Exception e) {
            throw new IllegalStateException("抓取 JaCoCo 运行期覆盖率失败: " + e.getMessage(), e);
        }
    }

    /**
     * 基于配置生成 HTML 覆盖率报告。
     *
     * @return 报告输出根目录
     */
    public File generateReport() {
        return generateReport(
                new File(properties.getExecFile()),
                new File(properties.getClassesDir()),
                new File(properties.getSourceDir()),
                new File(properties.getReportDir()),
                properties.getTitle());
    }

    /**
     * 基于指定的执行数据、class 目录、源码目录生成 HTML 覆盖率报告。
     *
     * @param execFile 执行数据文件（.exec）
     * @param classesDir 已编译 class 目录
     * @param sourceDir 源码目录
     * @param reportDir 报告输出目录
     * @param title 报告标题
     * @return 报告输出根目录
     */
    public File generateReport(File execFile, File classesDir, File sourceDir, File reportDir, String title) {
        try {
            ExecFileLoader loader = new ExecFileLoader();
            loader.load(execFile);

            CoverageBuilder coverageBuilder = new CoverageBuilder();
            Analyzer analyzer = new Analyzer(loader.getExecutionDataStore(), coverageBuilder);
            analyzer.analyzeAll(classesDir);
            IBundleCoverage bundle = coverageBuilder.getBundle(title);

            HTMLFormatter htmlFormatter = new HTMLFormatter();
            IReportVisitor visitor = htmlFormatter.createVisitor(new FileMultiReportOutput(reportDir));
            visitor.visitInfo(
                    loader.getSessionInfoStore().getInfos(),
                    loader.getExecutionDataStore().getContents());
            visitor.visitBundle(bundle, new DirectorySourceFileLocator(sourceDir, "utf-8", 4));
            visitor.visitEnd();
            return reportDir;
        } catch (IOException e) {
            throw new IllegalStateException("生成 JaCoCo 覆盖率报告失败: " + e.getMessage(), e);
        }
    }
}
