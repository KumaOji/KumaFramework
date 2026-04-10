package com.kuma.boot.logger.logging.appender;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import com.github.rahulsinghai.logback.kafka.delivery.AsynchronousDeliveryStrategy;
import com.github.rahulsinghai.logback.kafka.keying.HostNameKeyingStrategy;
import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.logger.logback.appender.StandardKafkaAppender;
import com.kuma.boot.logger.logback.layout.TraceIdPatternLogbackLayout;
import com.kuma.boot.logger.logging.config.LoggingProperties;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

public class LoggingKafkaAppender implements ILoggingAppender {
   private final LoggingProperties properties;

   public LoggingKafkaAppender(Environment environment, LoggingProperties properties) {
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
      StandardKafkaAppender<ILoggingEvent> standardKafkaAppender = new StandardKafkaAppender<ILoggingEvent>();
      standardKafkaAppender.setContext(context);
      standardKafkaAppender.setName("KAFKA");
      standardKafkaAppender.setTopic("sys-log-${APP_NAME}");
      standardKafkaAppender.setKeyingStrategy(new HostNameKeyingStrategy());
      standardKafkaAppender.setDeliveryStrategy(new AsynchronousDeliveryStrategy());
      standardKafkaAppender.addProducerConfig("bootstrap.servers=${BOOTSTRAP_SERVERS}");
      standardKafkaAppender.addProducerConfig("key.serializer=org.apache.kafka.common.serialization.StringSerializer");
      standardKafkaAppender.addProducerConfig("value.serializer=org.apache.kafka.common.serialization.StringSerializer");
      standardKafkaAppender.addProducerConfig("partitioner.class=org.apache.kafka.clients.producer.RoundRobinPartitioner<");
      standardKafkaAppender.addProducerConfig("acks=0");
      standardKafkaAppender.addProducerConfig("linger.ms=100");
      standardKafkaAppender.addProducerConfig("retries=0");
      standardKafkaAppender.addProducerConfig("batch.size=16384");
      standardKafkaAppender.addProducerConfig("buffer.memory=33554432");
      standardKafkaAppender.addProducerConfig("max.request.size=1048576");
      standardKafkaAppender.addProducerConfig("max.block.ms=60000");
      standardKafkaAppender.addProducerConfig("compression.type=gzip");
      standardKafkaAppender.setEncoder(layoutWrappingEncoder(context));
      standardKafkaAppender.start();
      context.getLogger("ROOT").detachAppender("KAFKA");
      context.getLogger("ROOT").addAppender(standardKafkaAppender);
   }

   private static LayoutWrappingEncoder<ILoggingEvent> layoutWrappingEncoder(LoggerContext context) {
      LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder();
      encoder.setContext(context);
      TraceIdPatternLogbackLayout traceIdPatternLogbackLayout = new TraceIdPatternLogbackLayout();
      traceIdPatternLogbackLayout.setContext(context);
      traceIdPatternLogbackLayout.setPattern("{\n\t\t\t\t\t\"app_name\": \"${APP_NAME:-}\",\n\t\t\t\t\t\"server_ip\": \"${SERVER_IP:-}\",\n\t\t\t\t\t\"server_port\": \"${SERVER_PORT:-}\",\n\t\t\t\t\t\"env\": \"${SPRING_PROFILES_ACTIVE:-}\",\n\t\t\t\t\t\"version\": \"${KMC_VERSION:-}\",\n\t\t\t\t\t\"timestamp\": \"%d{yyyy-MM-dd HH:mm:ss.SSS}\",\n\t\t\t\t\t\"logday\": \"%d{yyyy-MM-dd}\",\n\t\t\t\t\t\"thread\": \"%thread\",\n\t\t\t\t\t\"pid\": \"${PID:-}\",\n\t\t\t\t\t\"logger\": \"%logger{360}\",\n\t\t\t\t\t\"tlog\": \"%X{tl}\",\n\t\t\t\t\t\"class\": \"%class{360}\",\n\t\t\t\t\t\"level\": \"%p\",\n\t\t\t\t\t\"file\": \"%F\",\n\t\t\t\t\t\"method\": \"%M\",\n\t\t\t\t\t\"line\": \"%L\",\n\t\t\t\t\t\"message\": \"%replace(%msg){'\\\"', '\\''}\",\n\t\t\t\t\t\"stack_trace\": \"%replace(%ex){'\\\"', '\\''}\",\n\t\t\t\t\t\"host\": \"${SERVER_IP:-}\",\n\t\t\t\t\t\"otlp_parent_span_id\": \"%X{X-B3-ParentSpanId:-}\",\n\t\t\t\t\t\"otlp_span_id\": \"%X{X-B3-SpanId:-}\",\n\t\t\t\t\t\"otlp_trace_id\": \"%X{X-B3-TraceId:-}\",\n\t\t\t\t\t\"otlp_span_export\": \"%X{X-Span-Export:-}\",\n\t\t\t\t\t\"skywalking_trace_id\": \"%X{tid:-}\",\n\t\t\t\t\t\"trace_id\": \"%X{kmc-trace-id:-}\",\n\t\t\t\t\t\"tenant_id\": \"%X{kmc-tenant-id:-}\",\n\t\t\t\t\t\"request_version\": \"%X{kmc-request-version:-}\",\n\t\t\t\t\t\"source\": \"sys-log-${APP_NAME:-}\"\n\t\t\t\t\t}\n");
      traceIdPatternLogbackLayout.start();
      encoder.setLayout(traceIdPatternLogbackLayout);
      encoder.start();
      return encoder;
   }
}
