package com.kuma.boot.encrypt.crypto.ext.enhance;

import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.encrypt.crypto.ext.annotation.Crypto;
import com.kuma.boot.encrypt.crypto.ext.processor.HttpCryptoProcessor;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@Component("cryptoEncryptResponseBodyAdvice")
@RestControllerAdvice
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice {
   private static final Logger log = LoggerFactory.getLogger(EncryptResponseBodyAdvice.class);
   private HttpCryptoProcessor httpCryptoProcessor;

   public void setInterfaceCryptoProcessor(HttpCryptoProcessor httpCryptoProcessor) {
      this.httpCryptoProcessor = httpCryptoProcessor;
   }

   public boolean supports(MethodParameter methodParameter, Class converterType) {
      String methodName = methodParameter.getMethod().getName();
      Crypto crypto = (Crypto)methodParameter.getMethodAnnotation(Crypto.class);
      boolean isSupports = ObjectUtils.isNotEmpty(crypto) && crypto.responseEncrypt();
      log.trace("Is EncryptResponseBodyAdvice supports method [{}] ? Status is [{}].", methodName, isSupports);
      return isSupports;
   }

   public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
      String sessionKey = (String)request.getHeaders().get("session_key").get(0);
      if (StringUtils.isBlank(sessionKey)) {
         log.warn("Cannot find Herodotus Cloud custom session header. Use interface crypto founction need add X_HERODOTUS_SESSION to request header.");
         return body;
      } else {
         log.info("EncryptResponseBodyAdvice begin encrypt data.");
         String methodName = methodParameter.getMethod().getName();
         String className = methodParameter.getDeclaringClass().getName();
         String bodyString = JacksonUtils.toJson(body);
         String result = this.httpCryptoProcessor.encrypt(sessionKey, bodyString);
         if (StringUtils.isNotBlank(result)) {
            log.debug("Encrypt response body for rest method [{}] in [{}] finished.", methodName, className);
            return result;
         } else {
            return body;
         }
      }
   }
}
