package com.kuma.boot.openapi.client.config;

import com.kuma.boot.openapi.common.enums.AsymmetricCryEnum;
import com.kuma.boot.openapi.common.enums.CryModeEnum;
import com.kuma.boot.openapi.common.enums.SymmetricCryEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(
   prefix = "kuma.boot.openapi.client.config"
)
public class OpenApiClientConfig {
   private String openApiRefPath;
   private String baseUrl;
   private String selfPrivateKey;
   private String remotePublicKey;
   private AsymmetricCryEnum asymmetricCryEnum;
   private boolean retDecrypt;
   private CryModeEnum cryModeEnum;
   private SymmetricCryEnum symmetricCryEnum;
   private String callerId;
   private int httpConnectionTimeout;
   private int httpReadTimeout;
   private String httpProxyHost;
   private Integer httpProxyPort;
   private boolean enableCompress;

   public OpenApiClientConfig() {
      this.asymmetricCryEnum = AsymmetricCryEnum.RSA;
      this.retDecrypt = true;
      this.cryModeEnum = CryModeEnum.SYMMETRIC_CRY;
      this.symmetricCryEnum = SymmetricCryEnum.AES;
      this.httpConnectionTimeout = 3;
      this.httpReadTimeout = 5;
      this.enableCompress = false;
   }

   public String getOpenApiRefPath() {
      return this.openApiRefPath;
   }

   public void setOpenApiRefPath(String openApiRefPath) {
      this.openApiRefPath = openApiRefPath;
   }

   public String getBaseUrl() {
      return this.baseUrl;
   }

   public void setBaseUrl(String baseUrl) {
      this.baseUrl = baseUrl;
   }

   public String getSelfPrivateKey() {
      return this.selfPrivateKey;
   }

   public void setSelfPrivateKey(String selfPrivateKey) {
      this.selfPrivateKey = selfPrivateKey;
   }

   public String getRemotePublicKey() {
      return this.remotePublicKey;
   }

   public void setRemotePublicKey(String remotePublicKey) {
      this.remotePublicKey = remotePublicKey;
   }

   public AsymmetricCryEnum getAsymmetricCryEnum() {
      return this.asymmetricCryEnum;
   }

   public void setAsymmetricCryEnum(AsymmetricCryEnum asymmetricCryEnum) {
      this.asymmetricCryEnum = asymmetricCryEnum;
   }

   public boolean isRetDecrypt() {
      return this.retDecrypt;
   }

   public void setRetDecrypt(boolean retDecrypt) {
      this.retDecrypt = retDecrypt;
   }

   public CryModeEnum getCryModeEnum() {
      return this.cryModeEnum;
   }

   public void setCryModeEnum(CryModeEnum cryModeEnum) {
      this.cryModeEnum = cryModeEnum;
   }

   public SymmetricCryEnum getSymmetricCryEnum() {
      return this.symmetricCryEnum;
   }

   public void setSymmetricCryEnum(SymmetricCryEnum symmetricCryEnum) {
      this.symmetricCryEnum = symmetricCryEnum;
   }

   public String getCallerId() {
      return this.callerId;
   }

   public void setCallerId(String callerId) {
      this.callerId = callerId;
   }

   public int getHttpConnectionTimeout() {
      return this.httpConnectionTimeout;
   }

   public void setHttpConnectionTimeout(int httpConnectionTimeout) {
      this.httpConnectionTimeout = httpConnectionTimeout;
   }

   public int getHttpReadTimeout() {
      return this.httpReadTimeout;
   }

   public void setHttpReadTimeout(int httpReadTimeout) {
      this.httpReadTimeout = httpReadTimeout;
   }

   public String getHttpProxyHost() {
      return this.httpProxyHost;
   }

   public void setHttpProxyHost(String httpProxyHost) {
      this.httpProxyHost = httpProxyHost;
   }

   public Integer getHttpProxyPort() {
      return this.httpProxyPort;
   }

   public void setHttpProxyPort(Integer httpProxyPort) {
      this.httpProxyPort = httpProxyPort;
   }

   public boolean isEnableCompress() {
      return this.enableCompress;
   }

   public void setEnableCompress(boolean enableCompress) {
      this.enableCompress = enableCompress;
   }
}
