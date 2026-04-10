package com.kuma.boot.logger.logging.appender;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.logger.logging.config.LoggingProperties;
import com.kuma.boot.logger.logging.utils.LogStashUtil;
import com.kuma.boot.logger.logging.utils.LoggingUtil;
import java.util.HashMap;
import java.util.Map;
import net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LoggingSystemProperty;
import org.springframework.core.env.Environment;

public class LoggingJsonFileAppender implements ILoggingAppender {
   private final LoggingProperties properties;
   private final String logAllFile;
   private final String customFieldsJson;

   public LoggingJsonFileAppender(Environment environment, LoggingProperties properties) {
      this.properties = properties;
      String appName = environment.getRequiredProperty(CommonConstants.SPRING_APP_NAME_KEY);
      String profile = environment.getRequiredProperty(CommonConstants.ACTIVE_PROFILES_PROPERTY);
      String fileLogPattern = environment.resolvePlaceholders("%d{yyyy-MM-dd HH:mm:ss.SSS} %level ${PID} [${APP_NAME}:${SERVER_IP}:${SERVER_PORT}] [${KMC_VERSION}] [%thread] [${SPRING_PROFILES_ACTIVE:-}] [${OS_NAME:-}:${OS_VERSION:-}:${USER_TIMEZONE:-}:${JAVA_VERSION:-}] Tlog:[%X{tl}] TraceId:[%X{kmc-trace-id:-}] TenantId:[%X{kmc-tenant-id:-}] RequestVersion:[%X{kmc-request-version:-}] OtlpTraceId:[%X{traceId:-}:%X{spanId:-}] [%tid] %class{360}:%M:%L : %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}\n");
      System.setProperty(LoggingSystemProperty.FILE_PATTERN.getEnvironmentVariableName(), fileLogPattern);
      String logDir = environment.getProperty("logging.file.path", "logs");
      this.logAllFile = logDir + "/" + appName + "/all.log";
      Map<String, Object> customFields = new HashMap(4);
      customFields.put("appName", appName);
      customFields.put("profile", profile);
      this.customFieldsJson = JacksonUtils.toJson(customFields);
      LoggerContext context = (LoggerContext)LoggerFactory.getILoggerFactory();
      this.start(context);
   }

   public void start(LoggerContext context) {
      LogUtils.info("JsonFile logging start.", new Object[0]);
      this.reload(context);
   }

   public void reset(LoggerContext context) {
      LogUtils.info("JsonFile logging start.", new Object[0]);
      this.reload(context);
   }

   private void reload(LoggerContext context) {
      LoggingProperties.Files files = this.properties.getFiles();
      if (files.isEnabled() && files.isUseJsonFormat()) {
         addAllFileAppender(context, this.logAllFile, this.customFieldsJson);
      }

   }

   private static void addAllFileAppender(LoggerContext context, String logFile, String customFields) {
      RollingFileAppender<ILoggingEvent> allFileAppender = new RollingFileAppender();
      allFileAppender.setContext(context);
      allFileAppender.setEncoder(compositeJsonEncoder(context, customFields));
      allFileAppender.setName("FILE");
      allFileAppender.setFile(logFile);
      allFileAppender.setRollingPolicy(LoggingUtil.rollingPolicy(context, allFileAppender, logFile));
      allFileAppender.start();
      context.getLogger("ROOT").detachAppender("FILE");
      context.getLogger("ROOT").addAppender(allFileAppender);
   }

   private static LoggingEventCompositeJsonEncoder compositeJsonEncoder(LoggerContext context, String customFields) {
      LoggingEventCompositeJsonEncoder compositeJsonEncoder = new LoggingEventCompositeJsonEncoder();
      compositeJsonEncoder.setContext(context);
      compositeJsonEncoder.setProviders(LogStashUtil.jsonProviders(context, customFields));
      compositeJsonEncoder.start();
      return compositeJsonEncoder;
   }
}
