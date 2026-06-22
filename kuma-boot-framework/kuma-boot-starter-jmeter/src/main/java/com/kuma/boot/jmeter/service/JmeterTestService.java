package com.kuma.boot.jmeter.service;

import com.kuma.boot.jmeter.autoconfigure.properties.JmeterProperties;
import java.io.File;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.report.dashboard.ReportGenerator;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.springframework.util.StringUtils;

/**
 * Apache JMeter 嵌入式压测服务。
 *
 * <p>以编程方式加载 {@code .jmx} 测试计划并通过 {@link StandardJMeterEngine} 执行，输出 {@code .jtl}
 * 结果文件，并可选生成 HTML 报告。
 *
 * <p>运行依赖一个有效的 JMeter HOME（含 bin/jmeter.properties 等资源）或显式指定的属性文件，
 * 可通过 {@code kuma.boot.jmeter.home} / {@code kuma.boot.jmeter.properties-file} 配置。
 *
 * @author kuma
 */
public class JmeterTestService {

    private final JmeterProperties properties;

    public JmeterTestService(JmeterProperties properties) {
        this.properties = properties;
    }

    /**
     * 执行指定的 {@code .jmx} 测试计划。
     *
     * @param jmxFileName 测试计划文件名或路径；相对路径以 {@link JmeterProperties#getJmxDir()} 为基准解析
     * @return 生成的 {@code .jtl} 结果文件
     */
    public File runTestPlan(String jmxFileName) {
        initJMeter();

        File jmxFile = resolveJmx(jmxFileName);
        try {
            HashTree testPlanTree = SaveService.loadTree(jmxFile);

            String baseName = stripExtension(jmxFile.getName());
            File resultFile = new File(properties.getResultDir(), baseName + ".jtl");
            ensureParent(resultFile);
            // 删除历史结果，避免追加写入导致报告数据混淆
            if (resultFile.exists() && !resultFile.delete()) {
                throw new IllegalStateException("无法清理历史结果文件: " + resultFile.getAbsolutePath());
            }

            Summariser summariser = new Summariser("summary");
            ResultCollector resultCollector = new ResultCollector(summariser);
            resultCollector.setFilename(resultFile.getAbsolutePath());
            testPlanTree.add(testPlanTree.getArray()[0], resultCollector);

            StandardJMeterEngine engine = new StandardJMeterEngine();
            engine.configure(testPlanTree);
            engine.run();

            if (properties.isGenerateReport()) {
                generateReport(resultFile);
            }
            return resultFile;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("执行 JMeter 测试计划失败: " + e.getMessage(), e);
        }
    }

    /**
     * 基于结果文件生成 HTML 报告。
     *
     * @param resultFile {@code .jtl} 结果文件
     */
    public void generateReport(File resultFile) {
        try {
            File reportDir = new File(properties.getReportDir());
            ensureDir(reportDir);
            JMeterUtils.setProperty(
                    "jmeter.reportgenerator.exporter.html.property.output_dir", reportDir.getAbsolutePath());

            ReportGenerator generator = new ReportGenerator(resultFile.getAbsolutePath(), null);
            generator.generate();
        } catch (Exception e) {
            throw new IllegalStateException("生成 JMeter HTML 报告失败: " + e.getMessage(), e);
        }
    }

    private void initJMeter() {
        String home = properties.getHome();
        if (StringUtils.hasText(home)) {
            JMeterUtils.setJMeterHome(home);
        }

        String propsFile = properties.getPropertiesFile();
        if (StringUtils.hasText(propsFile)) {
            JMeterUtils.loadJMeterProperties(propsFile);
        } else if (StringUtils.hasText(JMeterUtils.getJMeterHome())) {
            JMeterUtils.loadJMeterProperties(JMeterUtils.getJMeterHome() + "/bin/jmeter.properties");
        } else {
            throw new IllegalStateException(
                    "未配置 JMeter HOME 或属性文件，请设置 kuma.boot.jmeter.home 或 kuma.boot.jmeter.properties-file");
        }
        JMeterUtils.initLocale();
    }

    private File resolveJmx(String jmxFileName) {
        File file = new File(jmxFileName);
        if (!file.isAbsolute() && StringUtils.hasText(properties.getJmxDir())) {
            file = new File(properties.getJmxDir(), jmxFileName);
        }
        if (!file.exists()) {
            throw new IllegalStateException("JMX 测试计划不存在: " + file.getAbsolutePath());
        }
        return file;
    }

    private static String stripExtension(String name) {
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(0, dot) : name;
    }

    private static void ensureParent(File file) {
        File parent = file.getAbsoluteFile().getParentFile();
        ensureDir(parent);
    }

    private static void ensureDir(File dir) {
        if (dir != null && !dir.exists() && !dir.mkdirs()) {
            throw new IllegalStateException("无法创建目录: " + dir.getAbsolutePath());
        }
    }
}
