package com.kuma.boot.logger.logging.utils;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.RollingPolicy;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import com.kuma.boot.common.utils.system.SystemUtils;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.logback.RollingPolicySystemProperty;

public class LoggingUtil {
   public LoggingUtil() {
   }

   public static void detachAppender(String name) {
      LoggerContext context = (LoggerContext)LoggerFactory.getILoggerFactory();
      context.getLogger("ROOT").detachAppender(name);
   }

   public static RollingPolicy rollingPolicy(LoggerContext context, FileAppender<?> appender, String logErrorFile) {
      SizeAndTimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new SizeAndTimeBasedRollingPolicy();
      rollingPolicy.setContext(context);
      rollingPolicy.setCleanHistoryOnStart(SystemUtils.getPropToBool(RollingPolicySystemProperty.CLEAN_HISTORY_ON_START.getEnvironmentVariableName(), false));
      rollingPolicy.setFileNamePattern(logErrorFile + ".%d{yyyy-MM-dd}.%i.gz");
      rollingPolicy.setMaxFileSize(FileSize.valueOf(SystemUtils.getProp(RollingPolicySystemProperty.MAX_FILE_SIZE.getEnvironmentVariableName(), "10MB")));
      rollingPolicy.setMaxHistory(SystemUtils.getPropToInt(RollingPolicySystemProperty.MAX_HISTORY.getEnvironmentVariableName(), 7));
      rollingPolicy.setTotalSizeCap(FileSize.valueOf(SystemUtils.getProp(RollingPolicySystemProperty.TOTAL_SIZE_CAP.getEnvironmentVariableName(), "0")));
      rollingPolicy.setParent(appender);
      rollingPolicy.start();
      return rollingPolicy;
   }
}
