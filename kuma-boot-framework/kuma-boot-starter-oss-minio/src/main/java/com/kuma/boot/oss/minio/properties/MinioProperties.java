package com.kuma.boot.oss.minio.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties(
   prefix = "kuma.boot.oss.platform.minio"
)
public class MinioProperties {
   public static final String PREFIX = "kuma.boot.oss.platform.minio";
   private String accessKey;
   private String secretKey;
   private String url;
   private String bucketName;

   public MinioProperties() {
   }

   public String getAccessKey() {
      return this.accessKey;
   }

   public void setAccessKey(String accessKey) {
      this.accessKey = accessKey;
   }

   public String getSecretKey() {
      return this.secretKey;
   }

   public void setSecretKey(String secretKey) {
      this.secretKey = secretKey;
   }

   public String getUrl() {
      return this.url;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public String getBucketName() {
      return this.bucketName;
   }

   public void setBucketName(String bucketName) {
      this.bucketName = bucketName;
   }
}
