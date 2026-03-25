package com.kuma.boot.openapi.client;

import com.kuma.boot.openapi.common.enums.AsymmetricCryEnum;
import com.kuma.boot.openapi.common.enums.CryModeEnum;
import com.kuma.boot.openapi.common.enums.SymmetricCryEnum;

public class OpenApiClientBuilder {
   private final String baseUrl;
   private final String selfPrivateKey;
   private final String remotePublicKey;
   private AsymmetricCryEnum asymmetricCryEnum;
   private boolean retDecrypt;
   private CryModeEnum cryModeEnum;
   private SymmetricCryEnum symmetricCryEnum;
   private final String callerId;
   private String api;
   private int httpConnectionTimeout;
   private int httpReadTimeout;
   private String httpProxyHost;
   private Integer httpProxyPort;
   private boolean enableCompress;

   public OpenApiClientBuilder(String baseUrl, String selfPrivateKey, String remotePublicKey, String callerId) {
      this.asymmetricCryEnum = AsymmetricCryEnum.RSA;
      this.retDecrypt = true;
      this.cryModeEnum = CryModeEnum.SYMMETRIC_CRY;
      this.symmetricCryEnum = SymmetricCryEnum.AES;
      this.httpConnectionTimeout = 3;
      this.httpReadTimeout = 5;
      this.enableCompress = false;
      this.baseUrl = baseUrl;
      this.selfPrivateKey = selfPrivateKey;
      this.remotePublicKey = remotePublicKey;
      this.callerId = callerId;
   }

   public OpenApiClientBuilder(String baseUrl, String selfPrivateKey, String remotePublicKey, String callerId, String api) {
      this.asymmetricCryEnum = AsymmetricCryEnum.RSA;
      this.retDecrypt = true;
      this.cryModeEnum = CryModeEnum.SYMMETRIC_CRY;
      this.symmetricCryEnum = SymmetricCryEnum.AES;
      this.httpConnectionTimeout = 3;
      this.httpReadTimeout = 5;
      this.enableCompress = false;
      this.baseUrl = baseUrl;
      this.selfPrivateKey = selfPrivateKey;
      this.remotePublicKey = remotePublicKey;
      this.callerId = callerId;
      this.api = api;
   }

   public OpenApiClientBuilder api(String api) {
      this.api = api;
      return this;
   }

   public OpenApiClientBuilder asymmetricCry(AsymmetricCryEnum asymmetricCryEnum) {
      this.asymmetricCryEnum = asymmetricCryEnum;
      return this;
   }

   public OpenApiClientBuilder retDecrypt(boolean retDecrypt) {
      this.retDecrypt = retDecrypt;
      return this;
   }

   public OpenApiClientBuilder cryModeEnum(CryModeEnum cryModeEnum) {
      this.cryModeEnum = cryModeEnum;
      return this;
   }

   public OpenApiClientBuilder symmetricCry(SymmetricCryEnum symmetricCryEnum) {
      this.symmetricCryEnum = symmetricCryEnum;
      return this;
   }

   public OpenApiClientBuilder httpConnectionTimeout(int httpConnectionTimeout) {
      this.httpConnectionTimeout = httpConnectionTimeout;
      return this;
   }

   public OpenApiClientBuilder httpReadTimeout(int httpReadTimeout) {
      this.httpReadTimeout = httpReadTimeout;
      return this;
   }

   public OpenApiClientBuilder httpProxyHost(String httpProxyHost) {
      this.httpProxyHost = httpProxyHost;
      return this;
   }

   public OpenApiClientBuilder httpProxyPort(Integer httpProxyPort) {
      this.httpProxyPort = httpProxyPort;
      return this;
   }

   public OpenApiClientBuilder enableCompress(boolean enableCompress) {
      this.enableCompress = enableCompress;
      return this;
   }

   public OpenApiClient build() {
      OpenApiClient client = new OpenApiClient(this.baseUrl, this.selfPrivateKey, this.remotePublicKey, this.asymmetricCryEnum, this.retDecrypt, this.cryModeEnum, this.symmetricCryEnum, this.callerId, this.api, this.httpConnectionTimeout, this.httpReadTimeout, this.httpProxyHost, this.httpProxyPort, this.enableCompress);
      return client;
   }
}
