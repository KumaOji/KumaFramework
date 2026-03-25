package com.kuma.boot.openapi.client.proxy;

import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.openapi.client.OpenApiClient;
import com.kuma.boot.openapi.client.OpenApiClientBuilder;
import com.kuma.boot.openapi.client.annotation.OpenApiMethod;
import com.kuma.boot.openapi.client.annotation.OpenApiRef;
import com.kuma.boot.openapi.client.config.OpenApiClientConfig;
import com.kuma.boot.openapi.common.enums.CryModeEnum;
import com.kuma.boot.openapi.common.exception.OpenApiClientException;
import com.kuma.boot.openapi.common.model.Binary;
import com.kuma.boot.openapi.common.model.OutParams;
import com.kuma.boot.openapi.common.util.StrObjectConvert;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class OpenApiRefProxyInvocationHandler implements InvocationHandler {
   private final OpenApiClient openApiClient;
   private final OpenApiClientConfig config;

   public OpenApiRefProxyInvocationHandler(OpenApiClient openApiClient, OpenApiClientConfig config) {
      this.openApiClient = openApiClient;
      this.config = config;
   }

   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if (Object.class.equals(method.getDeclaringClass())) {
         return method.invoke(this, args);
      } else if (method.isAnnotationPresent(OpenApiMethod.class)) {
         OpenApiMethod openApiMethod = (OpenApiMethod)method.getAnnotation(OpenApiMethod.class);
         String methodName = openApiMethod.value();
         if (StrUtil.isBlank(methodName)) {
            throw new OpenApiClientException(method.getName() + "api方法名称不能为空");
         } else {
            OpenApiClient apiClient = this.openApiClient;
            boolean configDif = this.methodConfigDif(openApiMethod);
            if (configDif) {
               String api = ((OpenApiRef)method.getDeclaringClass().getAnnotation(OpenApiRef.class)).value();
               boolean retDecrypt = this.retDecrypt(openApiMethod);
               CryModeEnum cryModeEnum = this.getCryModeEnum(openApiMethod);
               int httpConnectionTimeout = this.httpConnectionTimeout(openApiMethod);
               int httpReadTimeout = this.httpReadTimeout(openApiMethod);
               boolean enableCompress = this.enableCompress(openApiMethod);
               apiClient = (new OpenApiClientBuilder(this.config.getBaseUrl(), this.config.getSelfPrivateKey(), this.config.getRemotePublicKey(), this.config.getCallerId(), api)).asymmetricCry(this.config.getAsymmetricCryEnum()).retDecrypt(retDecrypt).cryModeEnum(cryModeEnum).symmetricCry(this.config.getSymmetricCryEnum()).httpConnectionTimeout(httpConnectionTimeout).httpReadTimeout(httpReadTimeout).httpProxyHost(this.config.getHttpProxyHost()).httpProxyPort(this.config.getHttpProxyPort()).enableCompress(enableCompress).build();
            }

            OutParams outParams = apiClient.callOpenApi(methodName, args);
            Class<?> returnClass = method.getReturnType();
            if (OutParams.isSuccess(outParams)) {
               Object obj = StrObjectConvert.strToObj(outParams.getData(), returnClass);
               if (Binary.class.isAssignableFrom(returnClass)) {
                  Binary binary = (Binary)obj;
                  binary.setData(outParams.getBinaryData());
               }

               return obj;
            } else {
               throw new OpenApiClientException("返回失败：" + outParams.getMessage());
            }
         }
      } else {
         LogUtils.warn("{}非OpenApiMethod,不进行代理", new Object[]{method.getName()});
         return null;
      }
   }

   private boolean methodConfigDif(OpenApiMethod openApiMethod) {
      boolean retDecryptDif = StrUtil.isNotBlank(openApiMethod.retDecrypt()) && Boolean.parseBoolean(openApiMethod.retDecrypt()) != this.config.isRetDecrypt();
      if (retDecryptDif) {
         return true;
      } else {
         boolean cryModeDif = openApiMethod.cryModeEnum() != CryModeEnum.UNKNOWN && openApiMethod.cryModeEnum() != this.config.getCryModeEnum();
         if (cryModeDif) {
            return true;
         } else {
            boolean httpConnectionTimeoutDif = openApiMethod.httpConnectionTimeout() != -1 && openApiMethod.httpConnectionTimeout() != this.config.getHttpConnectionTimeout();
            if (httpConnectionTimeoutDif) {
               return true;
            } else {
               boolean httpReadTimeoutDif = openApiMethod.httpReadTimeout() != -1 && openApiMethod.httpReadTimeout() != this.config.getHttpReadTimeout();
               if (httpReadTimeoutDif) {
                  return true;
               } else {
                  return StrUtil.isNotBlank(openApiMethod.enableCompress()) && Boolean.parseBoolean(openApiMethod.enableCompress()) != this.config.isEnableCompress();
               }
            }
         }
      }
   }

   private boolean retDecrypt(OpenApiMethod openApiMethod) {
      boolean retDecrypt = this.config.isRetDecrypt();
      if (StrUtil.isNotBlank(openApiMethod.retDecrypt())) {
         retDecrypt = Boolean.parseBoolean(openApiMethod.retDecrypt());
      }

      return retDecrypt;
   }

   private CryModeEnum getCryModeEnum(OpenApiMethod openApiMethod) {
      CryModeEnum cryModeEnum = this.config.getCryModeEnum();
      if (CryModeEnum.UNKNOWN != openApiMethod.cryModeEnum()) {
         cryModeEnum = openApiMethod.cryModeEnum();
      }

      return cryModeEnum;
   }

   private int httpConnectionTimeout(OpenApiMethod openApiMethod) {
      int timeout = this.config.getHttpConnectionTimeout();
      if (openApiMethod.httpConnectionTimeout() != -1) {
         timeout = openApiMethod.httpConnectionTimeout();
      }

      return timeout;
   }

   private int httpReadTimeout(OpenApiMethod openApiMethod) {
      int timeout = this.config.getHttpReadTimeout();
      if (openApiMethod.httpReadTimeout() != -1) {
         timeout = openApiMethod.httpReadTimeout();
      }

      return timeout;
   }

   private boolean enableCompress(OpenApiMethod openApiMethod) {
      boolean enableCompress = this.config.isEnableCompress();
      if (StrUtil.isNotBlank(openApiMethod.enableCompress())) {
         enableCompress = Boolean.parseBoolean(openApiMethod.enableCompress());
      }

      return enableCompress;
   }
}
