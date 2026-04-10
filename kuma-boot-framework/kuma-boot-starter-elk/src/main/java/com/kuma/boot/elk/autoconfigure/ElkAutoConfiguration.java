package com.kuma.boot.elk.autoconfigure;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.Encoder;
import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.exception.BaseException;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.elk.autoconfigure.properties.ElkHealthLogStatisticProperties;
import com.kuma.boot.elk.autoconfigure.properties.ElkProperties;
import com.kuma.boot.elk.autoconfigure.properties.ElkWebAspectProperties;
import com.kuma.boot.elk.autoconfigure.properties.ElkWebProperties;
import com.kuma.boot.elk.filter.LogStatisticsFilter;
import jakarta.annotation.Resource;
import net.logstash.logback.appender.LogstashTcpSocketAppender;
import net.logstash.logback.encoder.LogstashEncoder;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties({ElkProperties.class, ElkWebAspectProperties.class, ElkHealthLogStatisticProperties.class, ElkWebProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.elk",
   name = {"enabled"},
   havingValue = "true"
)
public class ElkAutoConfiguration implements InitializingBean {
   @Resource
   private ElkProperties elkProperties;
   @Resource
   private LogStatisticsFilter logStatisticsFilter;

   public ElkAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(ElkAutoConfiguration.class, "kuma-boot-starter-elk", new String[0]);
   }

   @Bean(
      initMethod = "start",
      destroyMethod = "stop"
   )
   public LogstashTcpSocketAppender logstashTcpSocketAppender() {
      LogstashTcpSocketAppender appender = new LogstashTcpSocketAppender();
      String[] destinations = this.elkProperties.getDestinations();
      if (this.elkProperties.getDestinations() != null && this.elkProperties.getDestinations().length != 0) {
         for(String destination : destinations) {
            appender.addDestination(destination);
         }

         appender.setEncoder(this.createEncoder());
         ILoggerFactory factory = LoggerFactory.getILoggerFactory();
         if (factory instanceof LoggerContext) {
            LoggerContext context = (LoggerContext)factory;
            appender.setContext(context);
            context.getLogger("ROOT").addAppender(appender);
         }

         if (this.logStatisticsFilter != null) {
            appender.addFilter(this.logStatisticsFilter);
         }

         return appender;
      } else {
         throw new BaseException("\u672a\u8bbe\u7f6eelk\u5730\u5740");
      }
   }

   @Bean
   @ConditionalOnProperty(
      prefix = "kuma.boot.elk.log.statistic",
      name = {"enabled"},
      havingValue = "true"
   )
   LogStatisticsFilter getLogStatisticsFilter() {
      return new LogStatisticsFilter();
   }

   private Encoder<ILoggingEvent> createEncoder() {
      LogstashEncoder encoder = new LogstashEncoder();
      String appName = this.elkProperties.getAppName();
      if (StrUtil.isBlank(appName)) {
         appName = this.elkProperties.getSpringAppName();
      }

      if (StrUtil.isBlank(appName)) {
         throw new BaseException("\u7f3a\u5c11appName\u914d\u7f6e");
      } else {
         encoder.setCustomFields("{\"appname\":\"" + appName + "\",\"appindex\":\"applog\"}");
         encoder.setEncoding("UTF-8");
         return encoder;
      }
   }
}
