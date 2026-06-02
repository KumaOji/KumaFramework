package com.kuma.boot.oss.qiniu.support;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.kuma.boot.oss.common.condition.ConditionalOnOssEnabled;
import com.kuma.boot.oss.common.service.StandardOssClient;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import java.util.Map;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 七牛云 OSS 自动配置
 *
 * @author kuma
 */
@AutoConfiguration
@ConditionalOnOssEnabled
@EnableConfigurationProperties({QiniuOssProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.oss",
   name = {"type"},
   havingValue = "qiniu"
)
public class QiniuOssConfiguration implements InitializingBean {
   public static final String DEFAULT_BEAN_NAME = "qiniuOssClient";
   private final QiniuOssProperties qiniuOssProperties;

   public QiniuOssConfiguration(QiniuOssProperties qiniuOssProperties) {
      this.qiniuOssProperties = qiniuOssProperties;
   }

   @Bean
   @ConditionalOnMissingBean
   public StandardOssClient qiniuOssClient(QiniuOssConfig qiniuOssConfig) {
      Auth auth = Auth.create(qiniuOssConfig.getAccessKey(), qiniuOssConfig.getSecretKey());
      Configuration configuration = this.configuration(qiniuOssConfig);
      UploadManager uploadManager = new UploadManager(configuration);
      BucketManager bucketManager = new BucketManager(auth, configuration);
      return new QiniuOssClient(auth, uploadManager, bucketManager, qiniuOssConfig);
   }

   public Configuration configuration(QiniuOssConfig qiniuOssConfig) {
      Configuration configuration = new Configuration(Region.autoRegion());
      QiniuOssClientConfig clientConfig = qiniuOssConfig.getClientConfig();
      if (ObjUtil.isNotEmpty(clientConfig)) {
         // 七牛 Configuration 超时单位为秒
         configuration.connectTimeout = (int) (clientConfig.getConnectTimeout() / 1000L);
         configuration.readTimeout = (int) (clientConfig.getReadTimeout() / 1000L);
         configuration.writeTimeout = (int) (clientConfig.getWriteTimeout() / 1000L);
      }

      return configuration;
   }

   @Override
   public void afterPropertiesSet() {
      Map<String, QiniuOssConfig> qiniuOssConfigMap = this.qiniuOssProperties.getOssConfig();
      if (qiniuOssConfigMap.isEmpty()) {
         SpringUtil.registerBean(DEFAULT_BEAN_NAME, this.qiniuOssClient(this.qiniuOssProperties));
      } else {
         String accessKey = this.qiniuOssProperties.getAccessKey();
         String secretKey = this.qiniuOssProperties.getSecretKey();
         String domain = this.qiniuOssProperties.getDomain();
         QiniuOssClientConfig clientConfig = this.qiniuOssProperties.getClientConfig();
         qiniuOssConfigMap.forEach((name, qiniuOssConfig) -> {
            if (ObjUtil.isEmpty(qiniuOssConfig.getAccessKey())) {
               qiniuOssConfig.setAccessKey(accessKey);
            }

            if (ObjUtil.isEmpty(qiniuOssConfig.getSecretKey())) {
               qiniuOssConfig.setSecretKey(secretKey);
            }

            if (ObjUtil.isEmpty(qiniuOssConfig.getDomain())) {
               qiniuOssConfig.setDomain(domain);
            }

            if (ObjUtil.isEmpty(qiniuOssConfig.getClientConfig())) {
               qiniuOssConfig.setClientConfig(clientConfig);
            }

            SpringUtil.registerBean(name, this.qiniuOssClient(qiniuOssConfig));
         });
      }
   }
}
