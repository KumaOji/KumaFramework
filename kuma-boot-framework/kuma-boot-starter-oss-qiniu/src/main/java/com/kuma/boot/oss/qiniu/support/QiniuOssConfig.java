package com.kuma.boot.oss.qiniu.support;

import com.kuma.boot.oss.common.model.SliceConfig;
import com.kuma.boot.oss.common.util.OssPathUtil;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 七牛云 配置
 *
 * @author kuma
 */
public class QiniuOssConfig {
   private String basePath;
   private String accessKey;
   private String secretKey;
   private String bucketName;
   /** 存储空间绑定的访问域名（如 http://xxx.bkt.clouddn.com） */
   private String domain;
   /** 是否使用 https 访问 */
   private Boolean useHttps = false;
   @NestedConfigurationProperty
   private QiniuOssClientConfig clientConfig = new QiniuOssClientConfig();
   @NestedConfigurationProperty
   private SliceConfig sliceConfig = new SliceConfig();

   public QiniuOssConfig() {
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

   public String getDomain() {
      return this.domain;
   }

   public void setDomain(String domain) {
      this.domain = domain;
   }

   public Boolean getUseHttps() {
      return this.useHttps;
   }

   public void setUseHttps(Boolean useHttps) {
      this.useHttps = useHttps;
   }

   public QiniuOssClientConfig getClientConfig() {
      return this.clientConfig;
   }

   public void setClientConfig(QiniuOssClientConfig clientConfig) {
      this.clientConfig = clientConfig;
   }

   public SliceConfig getSliceConfig() {
      return this.sliceConfig;
   }

   public void setSliceConfig(SliceConfig sliceConfig) {
      this.sliceConfig = sliceConfig;
   }
}
