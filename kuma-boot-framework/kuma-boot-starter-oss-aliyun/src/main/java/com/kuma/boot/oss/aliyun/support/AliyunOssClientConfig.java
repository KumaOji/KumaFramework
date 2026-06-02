package com.kuma.boot.oss.aliyun.support;

import com.kuma.boot.oss.common.constant.OssConstant;

/**
 * 阿里云 OSS 客户端连接配置
 *
 * @author kuma
 */
public class AliyunOssClientConfig {
   private Long connectTimeout;
   private Long socketTimeout;
   private Integer maxConnections;

   public AliyunOssClientConfig() {
      this.connectTimeout = OssConstant.DEFAULT_CONNECTION_TIMEOUT;
      this.socketTimeout = OssConstant.DEFAULT_CONNECTION_TIMEOUT;
      this.maxConnections = 1024;
   }

   public Long getConnectTimeout() {
      return this.connectTimeout;
   }

   public void setConnectTimeout(Long connectTimeout) {
      this.connectTimeout = connectTimeout;
   }

   public Long getSocketTimeout() {
      return this.socketTimeout;
   }

   public void setSocketTimeout(Long socketTimeout) {
      this.socketTimeout = socketTimeout;
   }

   public Integer getMaxConnections() {
      return this.maxConnections;
   }

   public void setMaxConnections(Integer maxConnections) {
      this.maxConnections = maxConnections;
   }
}
