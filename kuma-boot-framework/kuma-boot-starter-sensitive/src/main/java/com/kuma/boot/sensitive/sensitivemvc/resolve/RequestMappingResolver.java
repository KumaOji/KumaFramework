package com.kuma.boot.sensitive.sensitivemvc.resolve;

import com.kuma.boot.common.utils.log.LogUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.Optional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public class RequestMappingResolver implements HandlerMethodResolver {
   private final RequestMappingHandlerMapping requestMappingHandlerMapping;

   public RequestMappingResolver(RequestMappingHandlerMapping requestMappingHandlerMapping) {
      this.requestMappingHandlerMapping = requestMappingHandlerMapping;
   }

   public HandlerMethod resolve() {
      Optional var10000 = Optional.ofNullable(RequestContextHolder.getRequestAttributes());
      Objects.requireNonNull(ServletRequestAttributes.class);
      var10000 = var10000.filter(ServletRequestAttributes.class::isInstance);
      Objects.requireNonNull(ServletRequestAttributes.class);
      var10000 = var10000.map(ServletRequestAttributes.class::cast).map(ServletRequestAttributes::getRequest).map(this::getHandler).map(HandlerExecutionChain::getHandler);
      Objects.requireNonNull(HandlerMethod.class);
      var10000 = var10000.filter(HandlerMethod.class::isInstance);
      Objects.requireNonNull(HandlerMethod.class);
      return (HandlerMethod)var10000.map(HandlerMethod.class::cast).orElse((Object)null);
   }

   public final HandlerExecutionChain getHandler(HttpServletRequest request) {
      try {
         return this.requestMappingHandlerMapping.getHandler(request);
      } catch (Exception e) {
         LogUtils.error("Cannot get handler from current HttpServletRequest: {}", new Object[]{e.getMessage(), e});
         return null;
      }
   }
}
