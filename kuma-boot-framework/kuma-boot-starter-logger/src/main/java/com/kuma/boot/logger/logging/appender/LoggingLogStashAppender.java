package com.kuma.boot.logger.logging.appender;

import ch.qos.logback.classic.LoggerContext;
import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.logger.logging.config.LoggingProperties;
import com.kuma.boot.logger.logging.utils.LogStashUtil;
import java.util.HashMap;
import java.util.Map;
import net.logstash.logback.appender.LogstashTcpSocketAppender;
import net.logstash.logback.encoder.LogstashEncoder;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

public class LoggingLogStashAppender implements ILoggingAppender {
   private static final String ASYNC_LOG_STASH_APPENDER_NAME = "ASYNC_LOG_STASH";
   private final LoggingProperties properties;
   private final String customFieldsJson;

   public LoggingLogStashAppender(Environment environment, LoggingProperties properties) {
      this.properties = properties;
      String appName = environment.getRequiredProperty(CommonConstants.SPRING_APP_NAME_KEY);
      String profile = environment.getRequiredProperty(CommonConstants.ACTIVE_PROFILES_PROPERTY);
      Map<String, Object> customFields = new HashMap(4);
      customFields.put("appName", appName);
      customFields.put("profile", profile);
      customFields.putAll(properties.getLogstash().getCustomFieldMap());
      this.customFieldsJson = JacksonUtils.toJson(customFields);
      LoggerContext context = (LoggerContext)LoggerFactory.getILoggerFactory();
      this.start(context);
   }

   public void start(LoggerContext context) {
      LogUtils.info("LogStash logging start.", new Object[0]);
      this.reload(context);
   }

   public void reset(LoggerContext context) {
      LogUtils.info("LogStash logging reset.", new Object[0]);
      this.reload(context);
   }

   private void reload(LoggerContext context) {
      LoggingProperties.Logstash logStash = this.properties.getLogstash();
      if (logStash.isEnabled()) {
         addLogStashTcpSocketAppender(context, this.customFieldsJson, logStash);
      }

   }

   private static void addLogStashTcpSocketAppender(LoggerContext context, String customFields, LoggingProperties.Logstash logStashProperties) {
      LogstashTcpSocketAppender logStashAppender = new LogstashTcpSocketAppender();
      logStashAppender.addDestination(logStashProperties.getDestinations());
      logStashAppender.setContext(context);
      logStashAppender.setEncoder(logstashEncoder(customFields));
      logStashAppender.setName("ASYNC_LOG_STASH");
      logStashAppender.setRingBufferSize(logStashProperties.getRingBufferSize());
      logStashAppender.start();
      context.getLogger("ROOT").detachAppender("ASYNC_LOG_STASH");
      context.getLogger("ROOT").addAppender(logStashAppender);
   }

   private static LogstashEncoder logstashEncoder(String customFields) {
      LogstashEncoder logstashEncoder = new LogstashEncoder();
      logstashEncoder.setThrowableConverter(LogStashUtil.throwableConverter());
      logstashEncoder.setCustomFields(customFields);
      return logstashEncoder;
   }
}
