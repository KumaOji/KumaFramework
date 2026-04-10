package com.kuma.boot.logger.logging.appender;

import ch.qos.logback.classic.LoggerContext;
import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.logger.logging.config.LoggingProperties;
import de.siegmar.logbackgelf.GelfEncoder;
import de.siegmar.logbackgelf.GelfUdpAppender;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

public class LoggingGraylogAppender implements ILoggingAppender {
   private final LoggingProperties properties;

   public LoggingGraylogAppender(Environment environment, LoggingProperties properties) {
      this.properties = properties;
      String appName = environment.getRequiredProperty(CommonConstants.SPRING_APP_NAME_KEY);
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
      LoggingProperties.Console console = this.properties.getConsole();
      if (console.isEnabled()) {
         addConsoleAppender(context);
      }

   }

   private static void addConsoleAppender(LoggerContext context) {
      GelfUdpAppender gelfUdpAppender = new GelfUdpAppender();
      gelfUdpAppender.setContext(context);
      gelfUdpAppender.setName("GRAYLOG");
      gelfUdpAppender.setGraylogHost("127.0.0.1");
      gelfUdpAppender.setGraylogPort(12201);
      gelfUdpAppender.setMaxChunkSize(508);
      gelfUdpAppender.setEncoder(layoutWrappingEncoder(context));
      gelfUdpAppender.start();
      context.getLogger("ROOT").detachAppender("GRAYLOG");
      context.getLogger("ROOT").addAppender(gelfUdpAppender);
   }

   private static GelfEncoder layoutWrappingEncoder(LoggerContext context) {
      GelfEncoder gelfEncoder = new GelfEncoder();
      gelfEncoder.setContext(context);
      gelfEncoder.setIncludeRawMessage(false);
      gelfEncoder.setIncludeMarker(true);
      gelfEncoder.setIncludeMdcData(true);
      gelfEncoder.setIncludeCallerData(false);
      gelfEncoder.setIncludeRootCauseData(false);
      gelfEncoder.setIncludeLevelName(true);
      gelfEncoder.addStaticField("app_name:${APP_NAME}");
      gelfEncoder.addStaticField("server_ip:${SERVER_IP}");
      gelfEncoder.start();
      return gelfEncoder;
   }
}
