package com.kuma.boot.oss.aliyun.support;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.kuma.boot.oss.common.condition.ConditionalOnOssEnabled;
import com.kuma.boot.oss.common.service.StandardOssClient;
import java.util.Map;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 阿里云 OSS 自动配置
 *
 * @author kuma
 */
@AutoConfiguration
@ConditionalOnOssEnabled
@EnableConfigurationProperties({AliyunOssProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.oss",
   name = {"type"},
   havingValue = "aliyun"
)
public class AliyunOssConfiguration implements InitializingBean {
   public static final String DEFAULT_BEAN_NAME = "aliyunOssClient";
   private final AliyunOssProperties aliyunOssProperties;

   public AliyunOssConfiguration(AliyunOssProperties aliyunOssProperties) {
      this.aliyunOssProperties = aliyunOssProperties;
   }

   @Bean
   @ConditionalOnMissingBean
   public StandardOssClient aliyunOssClient(AliyunOssConfig aliyunOssConfig) {
      return new AliyunOssClient(this.ossClient(aliyunOssConfig), aliyunOssConfig);
   }

   public OSS ossClient(AliyunOssConfig aliyunOssConfig) {
      AliyunOssClientConfig clientConfig = aliyunOssConfig.getClientConfig();
      ClientBuilderConfiguration configuration = new ClientBuilderConfiguration();
      if (ObjUtil.isNotEmpty(clientConfig)) {
         configuration.setConnectionTimeout(clientConfig.getConnectTimeout().intValue());
         configuration.setSocketTimeout(clientConfig.getSocketTimeout().intValue());
         configuration.setMaxConnections(clientConfig.getMaxConnections());
      }

      return new OSSClientBuilder().build(aliyunOssConfig.getEndpoint(), aliyunOssConfig.getAccessKey(), aliyunOssConfig.getSecretKey(), configuration);
   }

   @Override
   public void afterPropertiesSet() {
      Map<String, AliyunOssConfig> aliyunOssConfigMap = this.aliyunOssProperties.getOssConfig();
      if (aliyunOssConfigMap.isEmpty()) {
         SpringUtil.registerBean(DEFAULT_BEAN_NAME, this.aliyunOssClient(this.aliyunOssProperties));
      } else {
         String endpoint = this.aliyunOssProperties.getEndpoint();
         String accessKey = this.aliyunOssProperties.getAccessKey();
         String secretKey = this.aliyunOssProperties.getSecretKey();
         AliyunOssClientConfig clientConfig = this.aliyunOssProperties.getClientConfig();
         aliyunOssConfigMap.forEach((name, aliyunOssConfig) -> {
            if (ObjUtil.isEmpty(aliyunOssConfig.getEndpoint())) {
               aliyunOssConfig.setEndpoint(endpoint);
            }

            if (ObjUtil.isEmpty(aliyunOssConfig.getAccessKey())) {
               aliyunOssConfig.setAccessKey(accessKey);
            }

            if (ObjUtil.isEmpty(aliyunOssConfig.getSecretKey())) {
               aliyunOssConfig.setSecretKey(secretKey);
            }

            if (ObjUtil.isEmpty(aliyunOssConfig.getClientConfig())) {
               aliyunOssConfig.setClientConfig(clientConfig);
            }

            SpringUtil.registerBean(name, this.aliyunOssClient(aliyunOssConfig));
         });
      }
   }
}
