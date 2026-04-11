package com.kuma.boot.oss.minio.support;

import com.kuma.boot.oss.common.model.SliceConfig;
import com.kuma.boot.oss.common.util.OssPathUtil;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

public class MinioOssConfig {
   private String basePath;
   private String endpoint;
   private String accessKey;
   private String secretKey;
   private String bucketName;
   @NestedConfigurationProperty
   private MinioOssClientConfig clientConfig = new MinioOssClientConfig();
   @NestedConfigurationProperty
   private SliceConfig sliceConfig = new SliceConfig();

   public MinioOssConfig() {
   }

   public void init() {
      this.sliceConfig.init();
      this.basePath = OssPathUtil.valid(this.basePath);
   }

   public String getBasePath() {
      return this.basePath;
   }

   public void setBasePath(String basePath) {
      this.basePath = basePath;
   }

   public String getEndpoint() {
      return this.endpoint;
   }

   public void setEndpoint(String endpoint) {
      this.endpoint = endpoint;
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

   public String getBucketName() {
      return this.bucketName;
   }

   public void setBucketName(String bucketName) {
      this.bucketName = bucketName;
   }

   public MinioOssClientConfig getClientConfig() {
      return this.clientConfig;
   }

   public void setClientConfig(MinioOssClientConfig clientConfig) {
      this.clientConfig = clientConfig;
   }

   public SliceConfig getSliceConfig() {
      return this.sliceConfig;
   }

   public void setSliceConfig(SliceConfig sliceConfig) {
      this.sliceConfig = sliceConfig;
   }
}
