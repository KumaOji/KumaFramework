package com.kuma.boot.jmeter.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * Apache JMeter 嵌入式压测配置。
 *
 * @author kuma
 */
@RefreshScope
@ConfigurationProperties(prefix = JmeterProperties.PREFIX)
public class JmeterProperties {

    public static final String PREFIX = "kuma.boot.jmeter";

    /** 是否启用 JMeter 模块。 */
    private boolean enabled = true;

    /** JMeter HOME 目录（包含 bin/、lib/ 等），嵌入式运行时用于定位资源与函数。 */
    private String home;

    /** JMeter 主配置文件（jmeter.properties）路径，为空时使用 HOME 下默认配置。 */
    private String propertiesFile;

    /** .jmx 测试计划所在目录，{@code runTestPlan(name)} 将以此为基准解析相对路径。 */
    private String jmxDir = "jmeter";

    /** 压测结果（.jtl）输出目录。 */
    private String resultDir = "build/jmeter/results";

    /** HTML 报告输出目录。 */
    private String reportDir = "build/jmeter/report";

    /** 执行完成后是否生成 HTML 报告。 */
    private boolean generateReport = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getPropertiesFile() {
        return propertiesFile;
    }

    public void setPropertiesFile(String propertiesFile) {
        this.propertiesFile = propertiesFile;
    }

    public String getJmxDir() {
        return jmxDir;
    }

    public void setJmxDir(String jmxDir) {
        this.jmxDir = jmxDir;
    }

    public String getResultDir() {
        return resultDir;
    }

    public void setResultDir(String resultDir) {
        this.resultDir = resultDir;
    }

    public String getReportDir() {
        return reportDir;
    }

    public void setReportDir(String reportDir) {
        this.reportDir = reportDir;
    }

    public boolean isGenerateReport() {
        return generateReport;
    }

    public void setGenerateReport(boolean generateReport) {
        this.generateReport = generateReport;
    }
}
