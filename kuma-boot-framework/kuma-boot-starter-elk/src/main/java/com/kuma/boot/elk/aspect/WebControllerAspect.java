package com.kuma.boot.elk.aspect;

import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.elk.autoconfigure.properties.ElkHealthLogStatisticProperties;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
public class WebControllerAspect {
   private static final String[] TOKEN_KEYS = new String[]{"BaseAuthorized", "Authorized"};
   private final ElkHealthLogStatisticProperties logStatisticProperties;

   public WebControllerAspect(ElkHealthLogStatisticProperties logStatisticProperties) {
      this.logStatisticProperties = logStatisticProperties;
   }

   @Pointcut("@within(org.springframework.stereotype.Controller) || @within(org.springframework.web.bind.annotation.RestController)")
   public void pointcut() {
   }

   @Around("pointcut()")
   public Object handle(ProceedingJoinPoint joinPoint) throws Throwable {
      Exception exception = null;
      Object result = null;
      long timeSpan = 0L;
      HttpServletRequest request = RequestContextHolder.getRequestAttributes() == null ? null : ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();

      try {
         long start = System.currentTimeMillis();
         result = joinPoint.proceed();
         timeSpan = System.currentTimeMillis() - start;
      } catch (Exception e) {
         exception = e;
         throw e;
      } finally {
         if (this.logStatisticProperties.getEnabled() && request != null) {
            String uri = request.getRequestURI().replace(request.getContextPath(), "");
            String inPutParam = this.preHandle(joinPoint, request);
            String outPutParam = this.postHandle(result);
            String ip = this.getRemoteHost(request);
            LogUtils.info("\u3010\u8fdc\u7a0bip\u3011{},\u3010url\u3011{},\u3010\u8f93\u5165\u3011{},\u3010\u8f93\u51fa\u3011{},\u3010\u5f02\u5e38\u3011{},\u3010\u8017\u65f6\u3011{}ms", new Object[]{ip, uri, inPutParam, outPutParam, exception == null ? "\u65e0" : exception.getMessage(), timeSpan});
         }

      }

      return result;
   }

   private String preHandle(ProceedingJoinPoint joinPoint, HttpServletRequest request) {
      Signature signature = joinPoint.getSignature();
      MethodSignature methodSignature = (MethodSignature)signature;
      Method targetMethod = methodSignature.getMethod();
      Annotation[] annotations = targetMethod.getAnnotations();
      StringBuilder sb = new StringBuilder();

      for(String tokenKey : TOKEN_KEYS) {
         String token = request.getHeader(tokenKey);
         if (StringUtils.isNotBlank(token)) {
            sb.append("token:").append(token).append(",");
            break;
         }
      }

      for(Annotation annotation : annotations) {
         if (annotation.annotationType().toString().contains("org.springframework.web.bind.annotation")) {
            sb.append(JacksonUtils.toJSONString(request.getParameterMap()));
         }
      }

      return sb.toString();
   }

   private String postHandle(Object retVal) {
      return null == retVal ? "" : JacksonUtils.toJSONString(retVal);
   }

   private String getRemoteHost(HttpServletRequest request) {
      String unknown = "unknown";
      String ip = request.getHeader("x-forwarded-for");
      if (StringUtils.isBlank(ip) || unknown.equalsIgnoreCase(ip)) {
         ip = request.getHeader("Proxy-Client-IP");
      }

      if (StringUtils.isBlank(ip) || unknown.equalsIgnoreCase(ip)) {
         ip = request.getHeader("WL-Proxy-Client-IP");
      }

      if (StringUtils.isBlank(ip) || unknown.equalsIgnoreCase(ip)) {
         ip = request.getRemoteAddr();
      }

      return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
   }
}
