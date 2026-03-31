package com.kuma.boot.monitor.autoconfigure;

import com.kuma.boot.common.support.thread.MDCThreadPoolExecutor;
import com.kuma.boot.common.support.thread.ThreadPoolFactory;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.core.autoconfigure.CoreAutoConfiguration;
import com.kuma.boot.core.autoconfigure.properties.AsyncProperties;
import com.kuma.boot.core.support.Collector;
import com.kuma.boot.monitor.Monitor;
import com.kuma.boot.monitor.autoconfigure.properties.MonitorProperties;
import com.kuma.boot.monitor.autoconfigure.properties.MonitorThreadPoolProperties;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@AutoConfiguration(
   after = {CoreAutoConfiguration.class}
)
@EnableConfigurationProperties({MonitorThreadPoolProperties.class, MonitorProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.monitor",
   name = {"enabled"},
   havingValue = "true",
   matchIfMissing = true
)
public class MonitorAutoConfiguration implements InitializingBean {
   public MonitorAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(MonitorAutoConfiguration.class, "kuma-boot-starter-core", new String[0]);
   }

   @Bean({"monitorThreadPoolExecutor"})
   public ThreadPoolExecutor monitorThreadPoolExecutor(MonitorThreadPoolProperties monitorThreadPoolProperties) {
      String monitorThreadName = monitorThreadPoolProperties.getThreadNamePrefix();
      MDCThreadPoolExecutor monitorThreadPoolExecutor = new MDCThreadPoolExecutor(monitorThreadPoolProperties.getCorePoolSize(), monitorThreadPoolProperties.getMaximumPoolSize(), monitorThreadPoolProperties.getKeepAliveTime(), TimeUnit.SECONDS, new SynchronousQueue(), new ThreadPoolFactory(monitorThreadName, true));
      monitorThreadPoolExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
      return monitorThreadPoolExecutor;
   }

   @Bean
   public Monitor monitor(MonitorThreadPoolProperties monitorThreadPoolProperties, @Autowired @Qualifier("asyncThreadPoolTaskExecutor") ThreadPoolTaskExecutor asyncThreadPoolTaskExecutor, @Autowired @Qualifier("monitorThreadPoolExecutor") ThreadPoolExecutor monitorThreadPoolExecutor, AsyncProperties asyncProperties, Collector collector) {
      return new Monitor(collector, asyncProperties, monitorThreadPoolProperties, asyncThreadPoolTaskExecutor, monitorThreadPoolExecutor);
   }
}
