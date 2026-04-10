package com.kuma.boot.logger.logback.encoder;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.spi.ContextAware;
import java.io.IOException;
import net.logstash.logback.composite.AbstractCompositeJsonFormatter;
import net.logstash.logback.composite.loggingevent.LoggingEventCompositeJsonFormatter;
import net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder;
import tools.jackson.core.JsonGenerator;

public class LogstashLoggingEventCompositeJsonEncoder extends LoggingEventCompositeJsonEncoder {
   public LogstashLoggingEventCompositeJsonEncoder() {
   }

   protected AbstractCompositeJsonFormatter<ILoggingEvent> createFormatter() {
      return new LogstashLoggingEventCompositeJsonFormatter(this);
   }

   public static class LogstashLoggingEventCompositeJsonFormatter extends LoggingEventCompositeJsonFormatter {
      protected void writeEventToGenerator(JsonGenerator generator, ILoggingEvent event) throws IOException {
         try {
            String loggerName = event.getLoggerName();
            if ("org.apache.zookeeper.Environment".equals(loggerName)) {
               return;
            }

            generator.writeStartObject();
            super.getProviders().writeTo(generator, event);
            generator.writeEndObject();
            generator.flush();
         } catch (Exception var4) {
         }

      }

      public LogstashLoggingEventCompositeJsonFormatter(ContextAware declaredOrigin) {
         super(declaredOrigin);
      }
   }
}
