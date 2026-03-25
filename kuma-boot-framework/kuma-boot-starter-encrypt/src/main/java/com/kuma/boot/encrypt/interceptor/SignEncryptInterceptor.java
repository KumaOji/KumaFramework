package com.kuma.boot.encrypt.interceptor;

import com.kuma.boot.encrypt.annotation.SignEncrypt;
import com.kuma.boot.encrypt.handler.SignEncryptHandler;
import com.kuma.boot.encrypt.wrapper.CacheRequestWrapper;
import com.kuma.boot.encrypt.wrapper.EncryptRequestWrapperFactory;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.swing.text.html.FormSubmitEvent.MethodType;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tools.jackson.databind.json.JsonMapper;

public class SignEncryptInterceptor implements MethodInterceptor {
   private final String signSecret;
   private final SignEncryptHandler signEncryptHandler;

   public SignEncryptInterceptor(String signSecret, SignEncryptHandler signEncryptHandler) {
      this.signSecret = signSecret;
      this.signEncryptHandler = signEncryptHandler;
   }

   public Object invoke(MethodInvocation methodInvocation) throws Throwable {
      Object proceed = methodInvocation.proceed();
      CacheRequestWrapper request = (CacheRequestWrapper)((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
      if (MethodType.POST.name().equalsIgnoreCase(request.getMethod()) && EncryptRequestWrapperFactory.contentIsJson(request.getContentType())) {
         SignEncrypt annotation = (SignEncrypt)methodInvocation.getMethod().getAnnotation(SignEncrypt.class);
         long timeout = annotation.timeout();
         TimeUnit timeUnit = annotation.timeUnit();
         if (request.getBody().length < 1) {
            return proceed;
         } else {
            Map<Object, Object> jsonMap = (Map)JsonMapper.builder().build().readValue(request.getBody(), Map.class);
            return this.signEncryptHandler.handle(proceed, timeout, timeUnit, this.signSecret, jsonMap);
         }
      } else {
         return proceed;
      }
   }
}
