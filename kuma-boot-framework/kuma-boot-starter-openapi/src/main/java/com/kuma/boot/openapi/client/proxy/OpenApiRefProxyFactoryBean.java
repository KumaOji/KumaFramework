package com.kuma.boot.openapi.client.proxy;

import cn.hutool.core.util.StrUtil;
import com.kuma.boot.openapi.client.OpenApiClient;
import com.kuma.boot.openapi.client.OpenApiClientBuilder;
import com.kuma.boot.openapi.client.annotation.OpenApiRef;
import com.kuma.boot.openapi.client.config.OpenApiClientConfig;
import com.kuma.boot.openapi.common.exception.OpenApiClientException;
import java.lang.reflect.Proxy;
import org.springframework.beans.factory.FactoryBean;

public class OpenApiRefProxyFactoryBean implements FactoryBean {
   private final OpenApiClientConfig config;
   private final Class interClass;

   public OpenApiRefProxyFactoryBean(OpenApiClientConfig config, Class interClass) {
      this.config = config;
      this.interClass = interClass;
   }

   public Object getObject() {
      this.checkConfig();
      String api = ((OpenApiRef)this.interClass.getAnnotation(OpenApiRef.class)).value();
      if (StrUtil.isBlank(api)) {
         throw new OpenApiClientException(this.interClass.getName() + "api名称不能为空");
      } else {
         OpenApiClient apiClient = (new OpenApiClientBuilder(this.config.getBaseUrl(), this.config.getSelfPrivateKey(), this.config.getRemotePublicKey(), this.config.getCallerId(), api)).asymmetricCry(this.config.getAsymmetricCryEnum()).retDecrypt(this.config.isRetDecrypt()).cryModeEnum(this.config.getCryModeEnum()).symmetricCry(this.config.getSymmetricCryEnum()).httpConnectionTimeout(this.config.getHttpConnectionTimeout()).httpReadTimeout(this.config.getHttpReadTimeout()).httpProxyHost(this.config.getHttpProxyHost()).httpProxyPort(this.config.getHttpProxyPort()).enableCompress(this.config.isEnableCompress()).build();
         OpenApiRefProxyInvocationHandler invocationHandler = new OpenApiRefProxyInvocationHandler(apiClient, this.config);
         return Proxy.newProxyInstance(this.interClass.getClassLoader(), new Class[]{this.interClass}, invocationHandler);
      }
   }

   public Class getObjectType() {
      return this.interClass;
   }

   private void checkConfig() {
      if (StrUtil.isBlank(this.config.getBaseUrl())) {
         throw new OpenApiClientException("openapi基础路径未配置");
      } else if (StrUtil.isBlank(this.config.getSelfPrivateKey())) {
         throw new OpenApiClientException("本系统私钥未配置");
      } else if (StrUtil.isBlank(this.config.getRemotePublicKey())) {
         throw new OpenApiClientException("远程系统的公钥未配置");
      } else if (StrUtil.isBlank(this.config.getCallerId())) {
         throw new OpenApiClientException("调用者ID未配置");
      }
   }
}
