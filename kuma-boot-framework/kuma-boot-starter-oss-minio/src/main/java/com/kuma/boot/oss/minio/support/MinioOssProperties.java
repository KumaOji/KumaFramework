package com.kuma.boot.oss.minio.support;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties("kuma.boot.oss.platform.miniooss")
public class MinioOssProperties extends MinioOssConfig implements InitializingBean {
   public static final String PREFIX = "kuma.boot.oss.platform.miniooss";
   private Map<String, MinioOssConfig> ossConfig = new HashMap();

   public MinioOssProperties() {
   }

   public void afterPropertiesSet() {
      if (this.ossConfig.isEmpty()) {
         this.init();
      } else {
         this.ossConfig.values().forEach(MinioOssConfig::init);
      }

   }

   public Map<String, MinioOssConfig> getOssConfig() {
      return this.ossConfig;
   }

   public void setOssConfig(Map<String, MinioOssConfig> ossConfig) {
      this.ossConfig = ossConfig;
   }
}
