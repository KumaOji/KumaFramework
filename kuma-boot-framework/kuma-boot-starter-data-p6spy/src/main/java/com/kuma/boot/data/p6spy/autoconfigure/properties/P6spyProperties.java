package com.kuma.boot.data.p6spy.autoconfigure.properties;

import cn.hutool.core.util.StrUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

@RefreshScope
@ConfigurationProperties(
   prefix = "kuma.boot.data.p6spy"
)
public class P6spyProperties implements EnvironmentAware {
   public static final String PREFIX = "kuma.boot.data.p6spy";
   private boolean enabled = true;
   private String realdatasourceproperties;
   private String exclude;
   private String jndicontextfactory;
   private String useNanoTime;
   private String append = "true";
   private String stacktraceclass;
   private String databaseDialectTimestampFormat = "yyyy-MM-dd HH:mm:ss";
   private String dateformat = "yyyy-MM-dd HH:mm:ss";
   private String jmxPrefix;
   private String driverlist = "com.mysql.cj.jdbc.Driver";
   private String appender = "com.kuma.boot.data.p6spy.logger.P6spyLogger";
   private String excludecategories;
   private String sqlexpression;
   private String modulelist = "com.p6spy.engine.spy.P6SpyFactory,com.p6spy.engine.logging.P6LogFactory,com.p6spy.engine.outage.P6OutageFactory";
   private String include;
   private String executionThreshold;
   private String reloadpropertiesinterval;
   private String outagedetectioninterval = "2";
   private String jmx;
   private String stacktrace;
   private String autoflush = "false";
   private String reloadproperties;
   private String logMessageFormat = "com.kuma.boot.data.p6spy.logger.format.P6spyLogFormat";
   private String databaseDialectBinaryFormat;
   private String realdatasource;
   private String jndicontextcustom;
   private String customLogMessageFormat = "%(currentTime)|%(executionTime)|%(category)|connection%(connectionId)|%(sqlSingleLine)";
   private String filter;
   private String jndicontextproviderurl;
   private String outagedetection = "true";
   private String logfile;
   private String databaseDialectDateFormat = "yyyy-MM-dd HH:mm:ss";
   private String excludebinary = "true";
   private String realdatasourceclass;
   private String remoteServiceName;
   private String includeParameterValues = "true";
   private String includeAffectedRowsCount = "true";

   public P6spyProperties() {
   }

   public String getRemoteServiceName() {
      return this.remoteServiceName;
   }

   public void setRemoteServiceName(String remoteServiceName) {
      this.remoteServiceName = remoteServiceName;
   }

   public String getIncludeParameterValues() {
      return this.includeParameterValues;
   }

   public void setIncludeParameterValues(String includeParameterValues) {
      this.includeParameterValues = includeParameterValues;
   }

   public String getIncludeAffectedRowsCount() {
      return this.includeAffectedRowsCount;
   }

   public void setIncludeAffectedRowsCount(String includeAffectedRowsCount) {
      this.includeAffectedRowsCount = includeAffectedRowsCount;
   }

   public String getRealdatasourceproperties() {
      return this.realdatasourceproperties;
   }

   public void setRealdatasourceproperties(String realdatasourceproperties) {
      this.realdatasourceproperties = realdatasourceproperties;
   }

   public String getExclude() {
      return this.exclude;
   }

   public void setExclude(String exclude) {
      this.exclude = exclude;
   }

   public String getJndicontextfactory() {
      return this.jndicontextfactory;
   }

   public void setJndicontextfactory(String jndicontextfactory) {
      this.jndicontextfactory = jndicontextfactory;
   }

   public String getUseNanoTime() {
      return this.useNanoTime;
   }

   public void setUseNanoTime(String useNanoTime) {
      this.useNanoTime = useNanoTime;
   }

   public String getAppend() {
      return this.append;
   }

   public void setAppend(String append) {
      this.append = append;
   }

   public String getStacktraceclass() {
      return this.stacktraceclass;
   }

   public void setStacktraceclass(String stacktraceclass) {
      this.stacktraceclass = stacktraceclass;
   }

   public String getDatabaseDialectTimestampFormat() {
      return this.databaseDialectTimestampFormat;
   }

   public void setDatabaseDialectTimestampFormat(String databaseDialectTimestampFormat) {
      this.databaseDialectTimestampFormat = databaseDialectTimestampFormat;
   }

   public String getDateformat() {
      return this.dateformat;
   }

   public void setDateformat(String dateformat) {
      this.dateformat = dateformat;
   }

   public String getJmxPrefix() {
      return this.jmxPrefix;
   }

   public void setJmxPrefix(String jmxPrefix) {
      this.jmxPrefix = jmxPrefix;
   }

   public String getDriverlist() {
      return this.driverlist;
   }

   public void setDriverlist(String driverlist) {
      this.driverlist = driverlist;
   }

   public String getAppender() {
      return this.appender;
   }

   public void setAppender(String appender) {
      this.appender = appender;
   }

   public String getExcludecategories() {
      return this.excludecategories;
   }

   public void setExcludecategories(String excludecategories) {
      this.excludecategories = excludecategories;
   }

   public String getSqlexpression() {
      return this.sqlexpression;
   }

   public void setSqlexpression(String sqlexpression) {
      this.sqlexpression = sqlexpression;
   }

   public String getModulelist() {
      return this.modulelist;
   }

   public void setModulelist(String modulelist) {
      this.modulelist = modulelist;
   }

   public String getInclude() {
      return this.include;
   }

   public void setInclude(String include) {
      this.include = include;
   }

   public String getExecutionThreshold() {
      return this.executionThreshold;
   }

   public void setExecutionThreshold(String executionThreshold) {
      this.executionThreshold = executionThreshold;
   }

   public String getReloadpropertiesinterval() {
      return this.reloadpropertiesinterval;
   }

   public void setReloadpropertiesinterval(String reloadpropertiesinterval) {
      this.reloadpropertiesinterval = reloadpropertiesinterval;
   }

   public String getOutagedetectioninterval() {
      return this.outagedetectioninterval;
   }

   public void setOutagedetectioninterval(String outagedetectioninterval) {
      this.outagedetectioninterval = outagedetectioninterval;
   }

   public String getJmx() {
      return this.jmx;
   }

   public void setJmx(String jmx) {
      this.jmx = jmx;
   }

   public String getStacktrace() {
      return this.stacktrace;
   }

   public void setStacktrace(String stacktrace) {
      this.stacktrace = stacktrace;
   }

   public String getAutoflush() {
      return this.autoflush;
   }

   public void setAutoflush(String autoflush) {
      this.autoflush = autoflush;
   }

   public String getReloadproperties() {
      return this.reloadproperties;
   }

   public void setReloadproperties(String reloadproperties) {
      this.reloadproperties = reloadproperties;
   }

   public String getLogMessageFormat() {
      return this.logMessageFormat;
   }

   public void setLogMessageFormat(String logMessageFormat) {
      this.logMessageFormat = logMessageFormat;
   }

   public String getDatabaseDialectBinaryFormat() {
      return this.databaseDialectBinaryFormat;
   }

   public void setDatabaseDialectBinaryFormat(String databaseDialectBinaryFormat) {
      this.databaseDialectBinaryFormat = databaseDialectBinaryFormat;
   }

   public String getRealdatasource() {
      return this.realdatasource;
   }

   public void setRealdatasource(String realdatasource) {
      this.realdatasource = realdatasource;
   }

   public String getJndicontextcustom() {
      return this.jndicontextcustom;
   }

   public void setJndicontextcustom(String jndicontextcustom) {
      this.jndicontextcustom = jndicontextcustom;
   }

   public String getCustomLogMessageFormat() {
      return this.customLogMessageFormat;
   }

   public void setCustomLogMessageFormat(String customLogMessageFormat) {
      this.customLogMessageFormat = customLogMessageFormat;
   }

   public String getFilter() {
      return this.filter;
   }

   public void setFilter(String filter) {
      this.filter = filter;
   }

   public String getJndicontextproviderurl() {
      return this.jndicontextproviderurl;
   }

   public void setJndicontextproviderurl(String jndicontextproviderurl) {
      this.jndicontextproviderurl = jndicontextproviderurl;
   }

   public String getOutagedetection() {
      return this.outagedetection;
   }

   public void setOutagedetection(String outagedetection) {
      this.outagedetection = outagedetection;
   }

   public String getLogfile() {
      return this.logfile;
   }

   public void setLogfile(String logfile) {
      this.logfile = logfile;
   }

   public String getDatabaseDialectDateFormat() {
      return this.databaseDialectDateFormat;
   }

   public void setDatabaseDialectDateFormat(String databaseDialectDateFormat) {
      this.databaseDialectDateFormat = databaseDialectDateFormat;
   }

   public String getExcludebinary() {
      return this.excludebinary;
   }

   public void setExcludebinary(String excludebinary) {
      this.excludebinary = excludebinary;
   }

   public String getRealdatasourceclass() {
      return this.realdatasourceclass;
   }

   public void setRealdatasourceclass(String realdatasourceclass) {
      this.realdatasourceclass = realdatasourceclass;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public void setEnvironment(Environment environment) {
      if (StrUtil.isBlank(this.logfile)) {
         String userHome = environment.getProperty("user.home");
         String springApplicationName = environment.getProperty("spring.application.name");
         this.logfile = userHome + "/logs/" + springApplicationName + "/p6spy/spy.log";
      }

   }
}
