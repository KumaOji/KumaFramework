package com.kuma.boot.job.xxl.autoconfigure;

import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.core.utils.common.PropertyUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.job.xxl.aspect.XxlJobMdcInspector;
import com.kuma.boot.job.xxl.autoconfigure.properties.XxlAdminProperties;
import com.kuma.boot.job.xxl.autoconfigure.properties.XxlExecutorProperties;
import com.kuma.boot.job.xxl.autoconfigure.properties.XxlJobProperties;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

@AutoConfiguration
@EnableConfigurationProperties({XxlJobProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.job.xxl",
   name = {"enabled"},
   havingValue = "true",
   matchIfMissing = true
)
public class XxlJobAutoConfiguration implements InitializingBean {
   private final XxlJobProperties xxlJobProperties;

   public XxlJobAutoConfiguration(XxlJobProperties xxlJobProperties) {
      this.xxlJobProperties = xxlJobProperties;
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(XxlJobAutoConfiguration.class, "kuma-boot-starter-job-xxl", new String[0]);
   }

   @Bean
   public XxlJobSpringExecutor xxlJobSpringExecutor(ObjectProvider<XxlJobServerList> xxlJobServerListProvider, Environment environment) {
      XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
      XxlExecutorProperties executor = this.xxlJobProperties.getExecutor();
      String appName = executor.getAppname();
      if (!StringUtils.hasText(appName)) {
         appName = PropertyUtils.getProperty(CommonConstants.SPRING_APP_NAME_KEY);
      }

      xxlJobSpringExecutor.setAppname(appName);
      xxlJobSpringExecutor.setAddress(executor.getAddress());
      xxlJobSpringExecutor.setIp(executor.getIp());
      xxlJobSpringExecutor.setPort(executor.getPort());
      xxlJobSpringExecutor.setAccessToken(executor.getAccessToken());
      xxlJobSpringExecutor.setLogPath(environment.resolvePlaceholders(executor.getLogPath()));
      xxlJobSpringExecutor.setLogRetentionDays(executor.getLogRetentionDays());
      if (!StringUtils.hasText(this.xxlJobProperties.getAdmin().getAddresses())) {
         XxlJobServerList xxlJobServerList = (XxlJobServerList)xxlJobServerListProvider.getIfAvailable();
         if (xxlJobServerList == null) {
            throw new BusinessException("\u8bf7\u914d\u7f6exxljob admin address");
         }

         String serverList = xxlJobServerList.getXxlJobServerList();
         xxlJobSpringExecutor.setAdminAddresses(serverList);
         XxlAdminProperties admin = this.xxlJobProperties.getAdmin();
         admin.setAddresses(serverList);
         this.xxlJobProperties.setAdmin(admin);
      } else {
         xxlJobSpringExecutor.setAdminAddresses(this.xxlJobProperties.getAdmin().getAddresses());
      }

      return xxlJobSpringExecutor;
   }

   @Bean
   public XxlJobMdcInspector xxlJobMdcInspector() {
      return new XxlJobMdcInspector();
   }
}
