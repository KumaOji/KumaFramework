package com.kuma.boot.data.elasticsearch.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties(
   prefix = "kuma.boot.data.elasticsearch.rest-pool"
)
public class RestClientPoolProperties {
   public static final String PREFIX = "kuma.boot.data.elasticsearch.rest-pool";
   private Integer connectTimeOut = 1000;
   private Integer socketTimeOut = 30000;
   private Integer connectionRequestTimeOut = 500;
   private Integer maxConnectNum = 30;
   private Integer maxConnectPerRoute = 10;

   public RestClientPoolProperties() {
   }

   public Integer getConnectTimeOut() {
      return this.connectTimeOut;
   }

   public void setConnectTimeOut(Integer connectTimeOut) {
      this.connectTimeOut = connectTimeOut;
   }

   public Integer getSocketTimeOut() {
      return this.socketTimeOut;
   }

   public void setSocketTimeOut(Integer socketTimeOut) {
      this.socketTimeOut = socketTimeOut;
   }

   public Integer getConnectionRequestTimeOut() {
      return this.connectionRequestTimeOut;
   }

   public void setConnectionRequestTimeOut(Integer connectionRequestTimeOut) {
      this.connectionRequestTimeOut = connectionRequestTimeOut;
   }

   public Integer getMaxConnectNum() {
      return this.maxConnectNum;
   }

   public void setMaxConnectNum(Integer maxConnectNum) {
      this.maxConnectNum = maxConnectNum;
   }

   public Integer getMaxConnectPerRoute() {
      return this.maxConnectPerRoute;
   }

   public void setMaxConnectPerRoute(Integer maxConnectPerRoute) {
      this.maxConnectPerRoute = maxConnectPerRoute;
   }
}
