package com.kuma.boot.oss.qiniu.support;

import com.kuma.boot.oss.common.constant.OssConstant;

/**
 * 七牛云 客户端连接配置
 *
 * @author kuma
 */
public class QiniuOssClientConfig {
   private Long connectTimeout;
   private Long readTimeout;
   private Long writeTimeout;

   public QiniuOssClientConfig() {
      this.connectTimeout = OssConstant.DEFAULT_CONNECTION_TIMEOUT;
      this.readTimeout = OssConstant.DEFAULT_CONNECTION_TIMEOUT;
      this.writeTimeout = OssConstant.DEFAULT_CONNECTION_TIMEOUT;
   }

   public Long getConnectTimeout() {
      return this.connectTimeout;
   }

   public void setConnectTimeout(Long connectTimeout) {
      this.connectTimeout = connectTimeout;
   }

   public Long getReadTimeout() {
      return this.readTimeout;
   }

   public void setReadTimeout(Long readTimeout) {
      this.readTimeout = readTimeout;
   }

   public Long getWriteTimeout() {
      return this.writeTimeout;
   }

   public void setWriteTimeout(Long writeTimeout) {
      this.writeTimeout = writeTimeout;
   }
}
