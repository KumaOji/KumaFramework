package com.kuma.boot.logger.logging.listener;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.ContextAwareBase;
import com.kuma.boot.logger.logging.appender.ILoggingAppender;
import java.util.List;

public class LogbackLoggerContextListener extends ContextAwareBase implements LoggerContextListener {
   private final List<ILoggingAppender> appenderList;

   public LogbackLoggerContextListener(List<ILoggingAppender> appenderList) {
      this.appenderList = appenderList;
   }

   public LogbackLoggerContextListener(ContextAware declaredOrigin, List<ILoggingAppender> appenderList) {
      super(declaredOrigin);
      this.appenderList = appenderList;
   }

   public boolean isResetResistant() {
      return true;
   }

   public void onStart(LoggerContext context) {
      for(ILoggingAppender appender : this.appenderList) {
         appender.start(context);
      }

   }

   public void onReset(LoggerContext context) {
      for(ILoggingAppender appender : this.appenderList) {
         appender.reset(context);
      }

   }

   public void onStop(LoggerContext context) {
   }

   public void onLevelChange(Logger logger, Level level) {
   }
}
