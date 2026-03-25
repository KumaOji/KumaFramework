package com.kuma.boot.encrypt.crypto.ext.enhance;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.encrypt.crypto.ext.annotation.Crypto;
import com.kuma.boot.encrypt.crypto.ext.processor.HttpCryptoProcessor;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartRequest;

public class DecryptRequestParamResolver implements HandlerMethodArgumentResolver {
   private static final Logger log = LoggerFactory.getLogger(DecryptRequestParamResolver.class);
   private HttpCryptoProcessor httpCryptoProcessor;
   private RequestParamMethodArgumentResolver requestParamMethodArgumentResolver;

   public void setRequestParamMethodArgumentResolver(RequestParamMethodArgumentResolver requestParamMethodArgumentResolver) {
      this.requestParamMethodArgumentResolver = requestParamMethodArgumentResolver;
   }

   public void setInterfaceCryptoProcessor(HttpCryptoProcessor httpCryptoProcessor) {
      this.httpCryptoProcessor = httpCryptoProcessor;
   }

   public boolean supportsParameter(MethodParameter methodParameter) {
      String methodName = methodParameter.getMethod().getName();
      boolean isSupports = this.isConfigCrypto(methodParameter) && this.requestParamMethodArgumentResolver.supportsParameter(methodParameter);
      log.trace("Is DecryptRequestParamResolver supports method [{}] ? Status is [{}].", methodName, isSupports);
      return isSupports;
   }

   private boolean isConfigCrypto(MethodParameter methodParameter) {
      Crypto crypto = (Crypto)methodParameter.getMethodAnnotation(Crypto.class);
      return ObjectUtils.isNotEmpty(crypto) && crypto.requestDecrypt();
   }

   private boolean isRegularRequest(NativeWebRequest webRequest) {
      MultipartRequest multipartRequest = (MultipartRequest)webRequest.getNativeRequest(MultipartRequest.class);
      return ObjectUtils.isEmpty(multipartRequest);
   }

   private String[] decrypt(String sessionId, String[] paramValues) {
      List<String> values = new ArrayList();

      for(String paramValue : paramValues) {
         String value = this.httpCryptoProcessor.decrypt(sessionId, paramValue);
         if (StringUtils.isNotBlank(value)) {
            values.add(value);
         }
      }

      String[] result = new String[values.size()];
      return (String[])values.toArray(result);
   }

   public @Nullable Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
      if (this.isRegularRequest(webRequest)) {
         HttpServletRequest request = (HttpServletRequest)webRequest.getNativeRequest(HttpServletRequest.class);
         String sessionId = request.getHeader("session_key");
         if (StringUtils.isNotBlank(sessionId)) {
            String[] paramValues = request.getParameterValues(methodParameter.getParameterName());
            if (ArrayUtils.isNotEmpty(paramValues)) {
               String[] values = this.decrypt(sessionId, paramValues);
               return values.length == 1 ? values[0] : values;
            }
         } else {
            log.warn("Cannot find Herodotus Cloud custom session header. Use interface crypto founction need add X_HERODOTUS_SESSION to request header.");
         }
      }

      log.debug("The decryption conditions are not met DecryptRequestParamResolver, skip! to next!");
      return this.requestParamMethodArgumentResolver.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);
   }
}
