package com.kuma.boot.logger.logging.appender;

import ch.qos.logback.classic.LoggerContext;

public interface ILoggingAppender {
   void start(LoggerContext context);

   void reset(LoggerContext context);
}
