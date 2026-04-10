package com.kuma.boot.logger.logging.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.rolling.RollingFileAppender;
import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.system.SystemUtils;
import com.kuma.boot.logger.logging.config.LoggingProperties;
import com.kuma.boot.logger.logging.utils.LoggingUtil;
import java.nio.charset.Charset;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LoggingSystemProperty;
import org.springframework.core.env.Environment;

public class LoggingFileAppender implements ILoggingAppender {
   private final LoggingProperties properties;
   private final String logAllFile;
   private final String logErrorFile;

   public LoggingFileAppender(Environment environment, LoggingProperties properties) {
      this.properties = properties;
      String appName = environment.getRequiredProperty(CommonConstants.SPRING_APP_NAME_KEY);
      String fileLogPattern = environment.resolvePlaceholders("%d{yyyy-MM-dd HH:mm:ss.SSS} %level ${PID} [${APP_NAME}:${SERVER_IP}:${SERVER_PORT}] [${KMC_VERSION}] [%thread] [${SPRING_PROFILES_ACTIVE:-}] [${OS_NAME:-}:${OS_VERSION:-}:${USER_TIMEZONE:-}:${JAVA_VERSION:-}] Tlog:[%X{tl}] TraceId:[%X{kmc-trace-id:-}] TenantId:[%X{kmc-tenant-id:-}] RequestVersion:[%X{kmc-request-version:-}] OtlpTraceId:[%X{traceId:-}:%X{spanId:-}] [%tid] %class{360}:%M:%L : %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}\n");
      System.setProperty(LoggingSystemProperty.FILE_PATTERN.getEnvironmentVariableName(), fileLogPattern);
      String logDir = environment.getProperty("logging.file.path", "logs");
      this.logAllFile = logDir + "/" + appName + "/all.log";
      this.logErrorFile = logDir + "/" + appName + "/error.log";
      LoggerContext context = (LoggerContext)LoggerFactory.getILoggerFactory();
      this.start(context);
   }

   public void start(LoggerContext context) {
      LogUtils.info("File logging start.", new Object[0]);
      this.reload(context);
   }

   public void reset(LoggerContext context) {
      LogUtils.info("File logging reset.", new Object[0]);
      this.reload(context);
   }

   private void reload(LoggerContext context) {
      LoggingProperties.Files files = this.properties.getFiles();
      if (files.isEnabled() && !files.isUseJsonFormat()) {
         addAllFileAppender(context, this.logAllFile);
         addErrorFileAppender(context, this.logErrorFile);
      }

   }

   private static void addAllFileAppender(LoggerContext context, String logFile) {
      RollingFileAppender<ILoggingEvent> allFileAppender = new RollingFileAppender();
      allFileAppender.setContext(context);
      allFileAppender.setEncoder(patternLayoutEncoder(context));
      allFileAppender.setName("FILE");
      allFileAppender.setFile(logFile);
      allFileAppender.setRollingPolicy(LoggingUtil.rollingPolicy(context, allFileAppender, logFile));
      allFileAppender.start();
      context.getLogger("ROOT").detachAppender("FILE");
      context.getLogger("ROOT").addAppender(allFileAppender);
   }

   private static void addErrorFileAppender(LoggerContext context, String logErrorFile) {
      RollingFileAppender<ILoggingEvent> errorFileAppender = new RollingFileAppender();
      errorFileAppender.setContext(context);
      errorFileAppender.addFilter(errorLevelFilter(context));
      errorFileAppender.setEncoder(patternLayoutEncoder(context));
      errorFileAppender.setName("FILE_ERROR");
      errorFileAppender.setFile(logErrorFile);
      errorFileAppender.setRollingPolicy(LoggingUtil.rollingPolicy(context, errorFileAppender, logErrorFile));
      errorFileAppender.start();
      context.getLogger("ROOT").detachAppender("FILE_ERROR");
      context.getLogger("ROOT").addAppender(errorFileAppender);
   }

   private static Encoder<ILoggingEvent> patternLayoutEncoder(LoggerContext context) {
      PatternLayoutEncoder encoder = new PatternLayoutEncoder();
      encoder.setContext(context);
      encoder.setPattern(SystemUtils.getProp(LoggingSystemProperty.FILE_PATTERN.getEnvironmentVariableName()));
      String charsetName = SystemUtils.getProp(LoggingSystemProperty.FILE_CHARSET.getEnvironmentVariableName(), "default");
      encoder.setCharset(Charset.forName(charsetName));
      encoder.start();
      return encoder;
   }

   private static ThresholdFilter errorLevelFilter(LoggerContext context) {
      ThresholdFilter filter = new ThresholdFilter();
      filter.setContext(context);
      filter.setLevel(Level.ERROR.levelStr);
      filter.start();
      return filter;
   }
}
