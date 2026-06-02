package com.kuma.boot.oss.aliyun.support;

import com.kuma.boot.oss.common.model.SliceConfig;
import com.kuma.boot.oss.common.util.OssPathUtil;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 阿里云 OSS 配置
 *
 * @author kuma
 */
public class AliyunOssConfig {
   private String basePath;
   private String endpoint;
   private String accessKey;
   private String secretKey;
   private String bucketName;
   @NestedConfigurationProperty
   private AliyunOssClientConfig clientConfig = new AliyunOssClientConfig();
   @NestedConfigurationProperty
   private SliceConfig sliceConfig = new SliceConfig();

   public AliyunOssConfig() {
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

   public AliyunOssClientConfig getClientConfig() {
      return this.clientConfig;
   }

   public void setClientConfig(AliyunOssClientConfig clientConfig) {
      this.clientConfig = clientConfig;
   }

   public SliceConfig getSliceConfig() {
      return this.sliceConfig;
   }

   public void setSliceConfig(SliceConfig sliceConfig) {
      this.sliceConfig = sliceConfig;
   }
}
