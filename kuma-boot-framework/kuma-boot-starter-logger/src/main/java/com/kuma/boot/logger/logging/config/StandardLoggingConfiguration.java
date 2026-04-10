package com.kuma.boot.logger.logging.config;

import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.logger.logging.appender.Appender;
import com.kuma.boot.logger.logging.appender.ILoggingAppender;
import com.kuma.boot.logger.logging.appender.LoggingFileAppender;
import com.kuma.boot.logger.logging.appender.LoggingJsonFileAppender;
import com.kuma.boot.logger.logging.appender.LoggingLogStashAppender;
import com.kuma.boot.logger.logging.listener.LogbackLoggerContextListener;
import com.kuma.boot.logger.logging.listener.LoggingStartedEventListener;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.ClassUtils;

@AutoConfiguration
@Order(Integer.MIN_VALUE)
@EnableConfigurationProperties({LoggingProperties.class})
public class StandardLoggingConfiguration implements InitializingBean {
   public StandardLoggingConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(StandardLoggingConfiguration.class, "kuma-boot-starter-logger", new String[0]);
   }

   @Bean
   public LoggingStartedEventListener loggingStartedEventListener(LoggingProperties loggingProperties) {
      return new LoggingStartedEventListener(loggingProperties);
   }

   @Bean
   public LogbackLoggerContextListener logbackLoggerContextListener(ObjectProvider<ILoggingAppender> loggingAppenderObjectProvider) {
      List<ILoggingAppender> loggingAppenderList = loggingAppenderObjectProvider.orderedStream().toList();
      return new LogbackLoggerContextListener(loggingAppenderList);
   }

   @Configuration(
      proxyBeanMethods = false
   )
   @StandardLoggingConfiguration.ConditionalOnAppender(Appender.FILE)
   public static class LoggingFileConfiguration {
      public LoggingFileConfiguration() {
      }

      @Bean
      public LoggingFileAppender loggingFileAppender(Environment environment, LoggingProperties properties) {
         return new LoggingFileAppender(environment, properties);
      }
   }

   @Configuration(
      proxyBeanMethods = false
   )
   @StandardLoggingConfiguration.ConditionalOnAppender(Appender.FILE_JSON)
   public static class LoggingJsonFileConfiguration {
      public LoggingJsonFileConfiguration() {
      }

      @Bean
      public LoggingJsonFileAppender loggingJsonFileAppender(Environment environment, LoggingProperties properties) {
         return new LoggingJsonFileAppender(environment, properties);
      }
   }

   @Configuration(
      proxyBeanMethods = false
   )
   @StandardLoggingConfiguration.ConditionalOnAppender(Appender.LOG_STASH)
   public static class LoggingLogStashConfiguration {
      public LoggingLogStashConfiguration() {
      }

      @Bean
      public LoggingLogStashAppender loggingLogStashAppender(Environment environment, LoggingProperties properties) {
         return new LoggingLogStashAppender(environment, properties);
      }
   }

   @Configuration(
      proxyBeanMethods = false
   )
   @StandardLoggingConfiguration.ConditionalOnAppender(Appender.LOKI)
   public static class LoggingLokiConfiguration {
      public LoggingLokiConfiguration() {
      }
   }

   @Order(Integer.MIN_VALUE)
   private static class LoggingCondition extends SpringBootCondition {
      private static final String LOG_STASH_CLASS_NAME = "net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder";
      private static final String LOKI_CLASS_NAME = "com.github.loki4j.logback.Loki4jAppender";

      private LoggingCondition() {
      }

      public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
         Map<String, Object> attributes = metadata.getAnnotationAttributes(ConditionalOnAppender.class.getName());
         Object value = ((Map)Objects.requireNonNull(attributes)).get("value");
         Appender appender = Appender.valueOf(value.toString());
         Environment environment = context.getEnvironment();
         ClassLoader classLoader = context.getClassLoader();
         Boolean fileEnabled = (Boolean)environment.getProperty("kuma.boot.logger.logging.files.enabled", Boolean.class, Boolean.TRUE);
         Boolean logStashEnabled = (Boolean)environment.getProperty("kuma.boot.logger.logging.logstash.enabled", Boolean.class, Boolean.FALSE);
         Boolean lokiEnabled = (Boolean)environment.getProperty("kuma.boot.logger.logging.loki.enabled", Boolean.class, Boolean.FALSE);
         if (Appender.LOKI == appender) {
            if (ObjectUtils.isFalse(lokiEnabled)) {
               return ConditionOutcome.noMatch("Logging loki is not enabled.");
            } else if (hasLokiDependencies(classLoader)) {
               return ConditionOutcome.match();
            } else {
               throw new IllegalStateException("Logging loki is enabled, please add com.github.loki4j loki-logback-appender dependencies.");
            }
         } else if (Appender.LOG_STASH == appender) {
            if (ObjectUtils.isFalse(logStashEnabled)) {
               return ConditionOutcome.noMatch("Logging logstash is not enabled.");
            } else if (hasLogStashDependencies(classLoader)) {
               return ConditionOutcome.match();
            } else {
               throw new IllegalStateException("Logging logstash is enabled, please add logstash-logback-encoder dependencies.");
            }
         } else if (Appender.FILE_JSON == appender) {
            Boolean isUseJsonFormat = (Boolean)environment.getProperty("kuma.boot.logger.logging.files.use-json-format", Boolean.class, Boolean.FALSE);
            if (!ObjectUtils.isFalse(fileEnabled) && !ObjectUtils.isFalse(isUseJsonFormat)) {
               if (hasLogStashDependencies(classLoader)) {
                  return ConditionOutcome.match();
               } else {
                  throw new IllegalStateException("Logging file json format is enabled, please add logstash-logback-encoder dependencies.");
               }
            } else {
               return ConditionOutcome.noMatch("Logging json file is not enabled.");
            }
         } else if (Appender.FILE == appender) {
            return ObjectUtils.isFalse(fileEnabled) ? ConditionOutcome.noMatch("Logging logstash is not enabled.") : ConditionOutcome.match();
         } else {
            return ConditionOutcome.match();
         }
      }

      private static boolean hasLogStashDependencies(ClassLoader classLoader) {
         return ClassUtils.isPresent("net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder", classLoader);
      }

      private static boolean hasLokiDependencies(ClassLoader classLoader) {
         return ClassUtils.isPresent("com.github.loki4j.logback.Loki4jAppender", classLoader);
      }
   }

   @Target({ElementType.TYPE, ElementType.METHOD})
   @Retention(RetentionPolicy.RUNTIME)
   @Documented
   @Conditional({LoggingCondition.class})
   public @interface ConditionalOnAppender {
      Appender value();
   }
}
