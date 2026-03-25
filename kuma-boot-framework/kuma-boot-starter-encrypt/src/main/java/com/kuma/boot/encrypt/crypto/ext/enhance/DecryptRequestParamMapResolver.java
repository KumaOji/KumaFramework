package com.kuma.boot.encrypt.crypto.ext.enhance;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.encrypt.crypto.ext.annotation.Crypto;
import com.kuma.boot.encrypt.crypto.ext.processor.HttpCryptoProcessor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import java.util.Map;
import org.apache.commons.lang3.ObjectUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.RequestParamMapMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;

public class DecryptRequestParamMapResolver implements HandlerMethodArgumentResolver {
   private static final Logger log = LoggerFactory.getLogger(DecryptRequestParamMapResolver.class);
   private HttpCryptoProcessor httpCryptoProcessor;
   private RequestParamMapMethodArgumentResolver requestParamMapMethodArgumentResolver;

   public void setInterfaceCryptoProcessor(HttpCryptoProcessor httpCryptoProcessor) {
      this.httpCryptoProcessor = httpCryptoProcessor;
   }

   public void setRequestParamMapMethodArgumentResolver(RequestParamMapMethodArgumentResolver requestParamMapMethodArgumentResolver) {
      this.requestParamMapMethodArgumentResolver = requestParamMapMethodArgumentResolver;
   }

   public boolean supportsParameter(MethodParameter methodParameter) {
      String methodName = methodParameter.getMethod().getName();
      boolean isSupports = this.requestParamMapMethodArgumentResolver.supportsParameter(methodParameter);
      log.trace("Is DecryptRequestParamMapResolver supports method [{}] ? Status is [{}].", methodName, isSupports);
      return isSupports;
   }

   private boolean isConfigCrypto(MethodParameter methodParameter) {
      Crypto crypto = (Crypto)methodParameter.getMethodAnnotation(Crypto.class);
      return ObjectUtils.isNotEmpty(crypto) && crypto.requestDecrypt();
   }

   private boolean isOauthTokenRequest(String uri, String method) {
      return StringUtils.equals(uri, "/oauth/token") && StringUtils.equalsIgnoreCase(method, HttpMethod.POST.name());
   }

   private boolean isRegularMap(MethodParameter methodParameter) {
      if (!MultiValueMap.class.isAssignableFrom(methodParameter.getParameterType())) {
         ResolvableType resolvableType = ResolvableType.forMethodParameter(methodParameter);
         Class<?> valueType = resolvableType.asMap().getGeneric(new int[]{1}).resolve();
         if (valueType != MultipartFile.class && valueType != Part.class) {
            return true;
         }
      }

      return false;
   }

   public @Nullable Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
      HttpServletRequest request = (HttpServletRequest)webRequest.getNativeRequest(HttpServletRequest.class);
      String requestURI = request.getRequestURI();
      String requestMethod = request.getMethod();
      String sessionId = request.getHeader("session_key");
      if (this.isConfigCrypto(methodParameter) || this.isOauthTokenRequest(requestURI, requestMethod)) {
         if (StringUtils.isNotBlank(sessionId)) {
            if (this.isRegularMap(methodParameter)) {
               Map<String, String[]> parameterMap = webRequest.getParameterMap();
               Map<String, String> result = CollectionUtils.newLinkedHashMap(parameterMap.size());

               for(Map.Entry entry : parameterMap.entrySet()) {
                  String key = (String)entry.getKey();
                  String[] values = (String[])entry.getValue();
                  if (values.length > 0) {
                     String value = this.httpCryptoProcessor.decrypt(sessionId, values[0]);
                     result.put(key, value);
                  }
               }

               return result;
            }
         } else {
            log.warn("Cannot find Herodotus Cloud custom session header. Use interface crypto founction need add X_HERODOTUS_SESSION to request header.");
         }
      }

      log.debug("The decryption conditions are not met DecryptRequestParamMapResolver, skip! to next!");
      return this.requestParamMapMethodArgumentResolver.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);
   }
}
