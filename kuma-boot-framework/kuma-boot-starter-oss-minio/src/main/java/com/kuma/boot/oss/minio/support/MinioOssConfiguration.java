package com.kuma.boot.oss.minio.support;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.kuma.boot.oss.common.condition.ConditionalOnOssEnabled;
import com.kuma.boot.oss.common.service.StandardOssClient;
import io.minio.MinioClient;
import io.minio.http.HttpUtils;
import java.util.Map;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnOssEnabled
@EnableConfigurationProperties({MinioOssProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.oss",
   name = {"type"},
   havingValue = "minio"
)
public class MinioOssConfiguration implements InitializingBean {
   public static final String DEFAULT_BEAN_NAME = "minioOssClient";
   private final MinioOssProperties minioOssProperties;

   public MinioOssConfiguration(MinioOssProperties minioOssProperties) {
      this.minioOssProperties = minioOssProperties;
   }

   @Bean
   @ConditionalOnMissingBean
   public StandardOssClient minioOssClient(MinioOssConfig minioOssConfig) {
      return new MinioOssClient(this.minioClient(minioOssConfig), minioOssConfig);
   }

   public MinioClient minioClient(MinioOssConfig minioOssConfig) {
      MinioOssClientConfig clientConfig = minioOssConfig.getClientConfig();
      OkHttpClient okHttpClient = HttpUtils.newDefaultHttpClient(clientConfig.getConnectTimeout(), clientConfig.getWriteTimeout(), clientConfig.getReadTimeout());
      return MinioClient.builder().endpoint(minioOssConfig.getEndpoint()).credentials(minioOssConfig.getAccessKey(), minioOssConfig.getSecretKey()).httpClient(okHttpClient).build();
   }

   public void afterPropertiesSet() throws Exception {
      Map<String, MinioOssConfig> minioOssConfigMap = this.minioOssProperties.getOssConfig();
      if (minioOssConfigMap.isEmpty()) {
         SpringUtil.registerBean("minioOssClient", this.minioOssClient(this.minioOssProperties));
      } else {
         String endpoint = this.minioOssProperties.getEndpoint();
         String accessKey = this.minioOssProperties.getAccessKey();
         String secretKey = this.minioOssProperties.getSecretKey();
         MinioOssClientConfig clientConfig = this.minioOssProperties.getClientConfig();
         minioOssConfigMap.forEach((name, minioOssConfig) -> {
            if (ObjUtil.isEmpty(minioOssConfig.getEndpoint())) {
               minioOssConfig.setEndpoint(endpoint);
            }

            if (ObjUtil.isEmpty(minioOssConfig.getAccessKey())) {
               minioOssConfig.setAccessKey(accessKey);
            }

            if (ObjUtil.isEmpty(minioOssConfig.getSecretKey())) {
               minioOssConfig.setSecretKey(secretKey);
            }

            if (ObjUtil.isEmpty(minioOssConfig.getClientConfig())) {
               minioOssConfig.setClientConfig(clientConfig);
            }

            SpringUtil.registerBean(name, this.minioOssClient(minioOssConfig));
         });
      }

   }
}
