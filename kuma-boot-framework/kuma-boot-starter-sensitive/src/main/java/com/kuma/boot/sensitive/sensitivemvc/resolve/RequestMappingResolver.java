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
      return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
            .filter(ServletRequestAttributes.class::isInstance)
            .map(ServletRequestAttributes.class::cast)
            .map(ServletRequestAttributes::getRequest)
            .map(this::getHandler)
            .map(HandlerExecutionChain::getHandler)
            .filter(HandlerMethod.class::isInstance)
            .map(HandlerMethod.class::cast)
            .orElse(null);
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
