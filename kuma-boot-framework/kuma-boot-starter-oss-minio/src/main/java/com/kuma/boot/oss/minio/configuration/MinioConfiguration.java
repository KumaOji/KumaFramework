package com.kuma.boot.oss.minio.configuration;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.oss.common.condition.ConditionalOnOssEnabled;
import com.kuma.boot.oss.common.service.UploadFileService;
import com.kuma.boot.oss.minio.properties.MinioProperties;
import com.kuma.boot.oss.minio.service.MinioUploadFileServiceImpl;
import io.minio.MinioClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnOssEnabled
@EnableConfigurationProperties({MinioProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.oss",
   name = {"type"},
   havingValue = "minio"
)
public class MinioConfiguration implements InitializingBean {
   private final MinioProperties properties;

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(MinioConfiguration.class, "kuma-boot-starter-oss-minio", new String[0]);
   }

   public MinioConfiguration(MinioProperties properties) {
      this.properties = properties;
   }

   @Bean
   public MinioClient minioClient() {
      return MinioClient.builder().endpoint(this.properties.getUrl()).credentials(this.properties.getAccessKey(), this.properties.getSecretKey()).build();
   }

   @Bean
   @ConditionalOnMissingBean
   public UploadFileService fileUpload(MinioProperties properties, MinioClient minioClient) {
      return new MinioUploadFileServiceImpl(properties, minioClient);
   }
}
