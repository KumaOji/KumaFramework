package com.kuma.boot.oss.minio.support;

import com.kuma.boot.oss.common.constant.OssConstant;

public class MinioOssClientConfig {
   private Long connectTimeout;
   private Long writeTimeout;
   private Long readTimeout;

   public MinioOssClientConfig() {
      this.connectTimeout = OssConstant.DEFAULT_CONNECTION_TIMEOUT;
      this.writeTimeout = OssConstant.DEFAULT_CONNECTION_TIMEOUT;
      this.readTimeout = OssConstant.DEFAULT_CONNECTION_TIMEOUT;
   }

   public Long getConnectTimeout() {
      return this.connectTimeout;
   }

   public void setConnectTimeout(Long connectTimeout) {
      this.connectTimeout = connectTimeout;
   }

   public Long getWriteTimeout() {
      return this.writeTimeout;
   }

   public void setWriteTimeout(Long writeTimeout) {
      this.writeTimeout = writeTimeout;
   }

   public Long getReadTimeout() {
      return this.readTimeout;
   }

   public void setReadTimeout(Long readTimeout) {
      this.readTimeout = readTimeout;
   }
}
