package com.kuma.boot.monitor.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "kuma.boot.monitor.collect"
)
public class CollectTaskProperties {
   public static final String PREFIX = "kuma.boot.monitor.collect";
   private boolean uncatchEnabled = true;
   private boolean xxljobEnabled = true;
   private int xxljobTimeSpan = 20;
   private boolean webServerEnabled = true;
   private int webServerTimeSpan = 20;
   private boolean monitorThreadEnabled = true;
   private int monitorThreadTimeSpan = 20;
   private boolean asyncThreadEnabled = true;
   private int asyncThreadTimeSpan = 20;
   private boolean threadPollEnabled = true;
   private int threadPollTimeSpan = 20;
   private boolean networkEnabled = true;
   private int networkTimeSpan = 10;
   private boolean mybatisEnabled = true;
   private int mybatisTimeSpan = 20;
   private boolean memeryEnabled = true;
   private int memeryTimeSpan = 10;
   private boolean logStatisticEnabled = true;
   private int logStatisticTimeSpan = 20;
   private boolean jedisEnabled = true;
   private int jedisTimeSpan = 20;
   private boolean ioEnabled = true;
   private int ioTimeSpan = 20;
   private boolean httpPoolEnabled = true;
   private int httpPoolTimeSpan = 20;
   private boolean elkEnabled = true;
   private int elkTimeSpan = 20;
   private boolean doubtApiEnabled = true;
   private int doubtApiTimeSpan = 20;
   private boolean datasourceEnabled = true;
   private int datasourcTimeSpan = 20;
   private boolean cpuEnabled = true;
   private int cpuTimeSpan = 20;
   private boolean nacosEnabled = true;
   private int nacosTimeSpan = 20;
   private boolean rocketMQEnabled = true;
   private int rocketMQTimeSpan = 20;
   private boolean elasticSearchEnabled = true;
   private int elasticSearchTimeSpan = 20;

   public CollectTaskProperties() {
   }

   public boolean isThreadPollEnabled() {
      return this.threadPollEnabled;
   }

   public void setThreadPollEnabled(boolean threadPollEnabled) {
      this.threadPollEnabled = threadPollEnabled;
   }

   public int getThreadPollTimeSpan() {
      return this.threadPollTimeSpan;
   }

   public void setThreadPollTimeSpan(int threadPollTimeSpan) {
      this.threadPollTimeSpan = threadPollTimeSpan;
   }

   public boolean isCpuEnabled() {
      return this.cpuEnabled;
   }

   public void setCpuEnabled(boolean cpuEnabled) {
      this.cpuEnabled = cpuEnabled;
   }

   public int getCpuTimeSpan() {
      return this.cpuTimeSpan;
   }

   public void setCpuTimeSpan(int cpuTimeSpan) {
      this.cpuTimeSpan = cpuTimeSpan;
   }

   public boolean isDatasourceEnabled() {
      return this.datasourceEnabled;
   }

   public void setDatasourceEnabled(boolean datasourceEnabled) {
      this.datasourceEnabled = datasourceEnabled;
   }

   public int getDatasourcTimeSpan() {
      return this.datasourcTimeSpan;
   }

   public void setDatasourcTimeSpan(int datasourcTimeSpan) {
      this.datasourcTimeSpan = datasourcTimeSpan;
   }

   public boolean isDoubtApiEnabled() {
      return this.doubtApiEnabled;
   }

   public void setDoubtApiEnabled(boolean doubtApiEnabled) {
      this.doubtApiEnabled = doubtApiEnabled;
   }

   public int getDoubtApiTimeSpan() {
      return this.doubtApiTimeSpan;
   }

   public void setDoubtApiTimeSpan(int doubtApiTimeSpan) {
      this.doubtApiTimeSpan = doubtApiTimeSpan;
   }

   public boolean isElkEnabled() {
      return this.elkEnabled;
   }

   public void setElkEnabled(boolean elkEnabled) {
      this.elkEnabled = elkEnabled;
   }

   public int getElkTimeSpan() {
      return this.elkTimeSpan;
   }

   public void setElkTimeSpan(int elkTimeSpan) {
      this.elkTimeSpan = elkTimeSpan;
   }

   public boolean isHttpPoolEnabled() {
      return this.httpPoolEnabled;
   }

   public void setHttpPoolEnabled(boolean httpPoolEnabled) {
      this.httpPoolEnabled = httpPoolEnabled;
   }

   public int getHttpPoolTimeSpan() {
      return this.httpPoolTimeSpan;
   }

   public void setHttpPoolTimeSpan(int httpPoolTimeSpan) {
      this.httpPoolTimeSpan = httpPoolTimeSpan;
   }

   public boolean isIoEnabled() {
      return this.ioEnabled;
   }

   public void setIoEnabled(boolean ioEnabled) {
      this.ioEnabled = ioEnabled;
   }

   public int getIoTimeSpan() {
      return this.ioTimeSpan;
   }

   public void setIoTimeSpan(int ioTimeSpan) {
      this.ioTimeSpan = ioTimeSpan;
   }

   public boolean isJedisEnabled() {
      return this.jedisEnabled;
   }

   public void setJedisEnabled(boolean jedisEnabled) {
      this.jedisEnabled = jedisEnabled;
   }

   public int getJedisTimeSpan() {
      return this.jedisTimeSpan;
   }

   public void setJedisTimeSpan(int jedisTimeSpan) {
      this.jedisTimeSpan = jedisTimeSpan;
   }

   public boolean isLogStatisticEnabled() {
      return this.logStatisticEnabled;
   }

   public void setLogStatisticEnabled(boolean logStatisticEnabled) {
      this.logStatisticEnabled = logStatisticEnabled;
   }

   public int getLogStatisticTimeSpan() {
      return this.logStatisticTimeSpan;
   }

   public void setLogStatisticTimeSpan(int logStatisticTimeSpan) {
      this.logStatisticTimeSpan = logStatisticTimeSpan;
   }

   public boolean isMemeryEnabled() {
      return this.memeryEnabled;
   }

   public void setMemeryEnabled(boolean memeryEnabled) {
      this.memeryEnabled = memeryEnabled;
   }

   public int getMemeryTimeSpan() {
      return this.memeryTimeSpan;
   }

   public void setMemeryTimeSpan(int memeryTimeSpan) {
      this.memeryTimeSpan = memeryTimeSpan;
   }

   public boolean isMybatisEnabled() {
      return this.mybatisEnabled;
   }

   public void setMybatisEnabled(boolean mybatisEnabled) {
      this.mybatisEnabled = mybatisEnabled;
   }

   public int getMybatisTimeSpan() {
      return this.mybatisTimeSpan;
   }

   public void setMybatisTimeSpan(int mybatisTimeSpan) {
      this.mybatisTimeSpan = mybatisTimeSpan;
   }

   public boolean isNetworkEnabled() {
      return this.networkEnabled;
   }

   public void setNetworkEnabled(boolean networkEnabled) {
      this.networkEnabled = networkEnabled;
   }

   public int getNetworkTimeSpan() {
      return this.networkTimeSpan;
   }

   public void setNetworkTimeSpan(int networkTimeSpan) {
      this.networkTimeSpan = networkTimeSpan;
   }

   public boolean isMonitorThreadEnabled() {
      return this.monitorThreadEnabled;
   }

   public void setMonitorThreadEnabled(boolean monitorThreadEnabled) {
      this.monitorThreadEnabled = monitorThreadEnabled;
   }

   public int getMonitorThreadTimeSpan() {
      return this.monitorThreadTimeSpan;
   }

   public void setMonitorThreadTimeSpan(int monitorThreadTimeSpan) {
      this.monitorThreadTimeSpan = monitorThreadTimeSpan;
   }

   public boolean isUncatchEnabled() {
      return this.uncatchEnabled;
   }

   public void setUncatchEnabled(boolean uncatchEnabled) {
      this.uncatchEnabled = uncatchEnabled;
   }

   public boolean isXxljobEnabled() {
      return this.xxljobEnabled;
   }

   public void setXxljobEnabled(boolean xxljobEnabled) {
      this.xxljobEnabled = xxljobEnabled;
   }

   public int getXxljobTimeSpan() {
      return this.xxljobTimeSpan;
   }

   public void setXxljobTimeSpan(int xxljobTimeSpan) {
      this.xxljobTimeSpan = xxljobTimeSpan;
   }

   public boolean getWebServerEnabled() {
      return this.webServerEnabled;
   }

   public void setWebServerEnabled(boolean webServerEnabled) {
      this.webServerEnabled = webServerEnabled;
   }

   public int getWebServerTimeSpan() {
      return this.webServerTimeSpan;
   }

   public void setWebServerTimeSpan(int webServerTimeSpan) {
      this.webServerTimeSpan = webServerTimeSpan;
   }

   public boolean isNacosEnabled() {
      return this.nacosEnabled;
   }

   public void setNacosEnabled(boolean nacosEnabled) {
      this.nacosEnabled = nacosEnabled;
   }

   public int getNacosTimeSpan() {
      return this.nacosTimeSpan;
   }

   public void setNacosTimeSpan(int nacosTimeSpan) {
      this.nacosTimeSpan = nacosTimeSpan;
   }

   public boolean getAsyncThreadEnabled() {
      return this.asyncThreadEnabled;
   }

   public void setAsyncThreadEnabled(boolean asyncThreadEnabled) {
      this.asyncThreadEnabled = asyncThreadEnabled;
   }

   public int getAsyncThreadTimeSpan() {
      return this.asyncThreadTimeSpan;
   }

   public void setAsyncThreadTimeSpan(int asyncThreadTimeSpan) {
      this.asyncThreadTimeSpan = asyncThreadTimeSpan;
   }

   public boolean isWebServerEnabled() {
      return this.webServerEnabled;
   }

   public boolean isAsyncThreadEnabled() {
      return this.asyncThreadEnabled;
   }

   public boolean isRocketMQEnabled() {
      return this.rocketMQEnabled;
   }

   public void setRocketMQEnabled(boolean rocketMQEnabled) {
      this.rocketMQEnabled = rocketMQEnabled;
   }

   public int getRocketMQTimeSpan() {
      return this.rocketMQTimeSpan;
   }

   public void setRocketMQTimeSpan(int rocketMQTimeSpan) {
      this.rocketMQTimeSpan = rocketMQTimeSpan;
   }

   public boolean isElasticSearchEnabled() {
      return this.elasticSearchEnabled;
   }

   public void setElasticSearchEnabled(boolean elasticSearchEnabled) {
      this.elasticSearchEnabled = elasticSearchEnabled;
   }

   public int getElasticSearchTimeSpan() {
      return this.elasticSearchTimeSpan;
   }

   public void setElasticSearchTimeSpan(int elasticSearchTimeSpan) {
      this.elasticSearchTimeSpan = elasticSearchTimeSpan;
   }
}
