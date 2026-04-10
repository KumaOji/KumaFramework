package com.kuma.boot.logger.logging.utils;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import net.logstash.logback.composite.ContextJsonProvider;
import net.logstash.logback.composite.GlobalCustomFieldsJsonProvider;
import net.logstash.logback.composite.loggingevent.ArgumentsJsonProvider;
import net.logstash.logback.composite.loggingevent.LogLevelJsonProvider;
import net.logstash.logback.composite.loggingevent.LoggerNameJsonProvider;
import net.logstash.logback.composite.loggingevent.LoggingEventFormattedTimestampJsonProvider;
import net.logstash.logback.composite.loggingevent.LoggingEventJsonProviders;
import net.logstash.logback.composite.loggingevent.LoggingEventPatternJsonProvider;
import net.logstash.logback.composite.loggingevent.LoggingEventThreadNameJsonProvider;
import net.logstash.logback.composite.loggingevent.MdcJsonProvider;
import net.logstash.logback.composite.loggingevent.MessageJsonProvider;
import net.logstash.logback.composite.loggingevent.StackTraceJsonProvider;
import net.logstash.logback.stacktrace.ShortenedThrowableConverter;

public class LogStashUtil {
   public LogStashUtil() {
   }

   public static LoggingEventJsonProviders jsonProviders(LoggerContext context, String customFields) {
      LoggingEventJsonProviders jsonProviders = new LoggingEventJsonProviders();
      jsonProviders.addArguments(new ArgumentsJsonProvider());
      jsonProviders.addContext(new ContextJsonProvider());
      jsonProviders.addGlobalCustomFields(customFieldsJsonProvider(customFields));
      jsonProviders.addLogLevel(new LogLevelJsonProvider());
      jsonProviders.addLoggerName(loggerNameJsonProvider());
      jsonProviders.addMdc(new MdcJsonProvider());
      jsonProviders.addMessage(new MessageJsonProvider());
      jsonProviders.addPattern(new LoggingEventPatternJsonProvider());
      jsonProviders.addStackTrace(stackTraceJsonProvider());
      jsonProviders.addThreadName(new LoggingEventThreadNameJsonProvider());
      jsonProviders.addTimestamp(timestampJsonProvider());
      jsonProviders.setContext(context);
      return jsonProviders;
   }

   private static GlobalCustomFieldsJsonProvider<ILoggingEvent> customFieldsJsonProvider(String customFields) {
      GlobalCustomFieldsJsonProvider<ILoggingEvent> customFieldsJsonProvider = new GlobalCustomFieldsJsonProvider();
      customFieldsJsonProvider.setCustomFields(customFields);
      return customFieldsJsonProvider;
   }

   private static LoggerNameJsonProvider loggerNameJsonProvider() {
      LoggerNameJsonProvider loggerNameJsonProvider = new LoggerNameJsonProvider();
      loggerNameJsonProvider.setShortenedLoggerNameLength(20);
      return loggerNameJsonProvider;
   }

   private static StackTraceJsonProvider stackTraceJsonProvider() {
      StackTraceJsonProvider stackTraceJsonProvider = new StackTraceJsonProvider();
      stackTraceJsonProvider.setThrowableConverter(throwableConverter());
      return stackTraceJsonProvider;
   }

   public static ShortenedThrowableConverter throwableConverter() {
      ShortenedThrowableConverter throwableConverter = new ShortenedThrowableConverter();
      throwableConverter.setRootCauseFirst(true);
      return throwableConverter;
   }

   private static LoggingEventFormattedTimestampJsonProvider timestampJsonProvider() {
      LoggingEventFormattedTimestampJsonProvider timestampJsonProvider = new LoggingEventFormattedTimestampJsonProvider();
      timestampJsonProvider.setTimeZone("UTC");
      return timestampJsonProvider;
   }
}
