package com.kuma.boot.oss.cos.support;

import com.kuma.boot.oss.common.model.SliceConfig;
import com.kuma.boot.oss.common.util.OssPathUtil;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 腾讯云 COS 配置
 *
 * @author kuma
 */
public class CosOssConfig {
   private String basePath;
   private String region;
   private String accessKey;
   private String secretKey;
   private String bucketName;
   @NestedConfigurationProperty
   private CosOssClientConfig clientConfig = new CosOssClientConfig();
   @NestedConfigurationProperty
   private SliceConfig sliceConfig = new SliceConfig();

   public CosOssConfig() {
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

   public String getRegion() {
      return this.region;
   }

   public void setRegion(String region) {
      this.region = region;
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

   public CosOssClientConfig getClientConfig() {
      return this.clientConfig;
   }

   public void setClientConfig(CosOssClientConfig clientConfig) {
      this.clientConfig = clientConfig;
   }

   public SliceConfig getSliceConfig() {
      return this.sliceConfig;
   }

   public void setSliceConfig(SliceConfig sliceConfig) {
      this.sliceConfig = sliceConfig;
   }
}
