package com.kuma.boot.useragent.servlet;

import com.kuma.boot.common.utils.servlet.RequestUtils;
import com.kuma.boot.useragent.annotation.UserAgentInfo;
import com.kuma.boot.useragent.domain.UserAgent;
import com.kuma.boot.useragent.helper.UserAgentHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class UserAgentResolver implements HandlerMethodArgumentResolver {
   public UserAgentResolver() {
   }

   public final boolean supportsParameter(MethodParameter parameter) {
      return parameter.hasParameterAnnotation(UserAgentInfo.class);
   }

   public final Object resolveArgument(@NonNull MethodParameter parameter, ModelAndViewContainer mavContainer, @NonNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
      UserAgent agentContext = UserAgentContextHolder.getUserAgentContext();
      if (agentContext == null) {
         HttpServletRequest request = (HttpServletRequest)webRequest.getNativeRequest(HttpServletRequest.class);
         Assert.notNull(request, "request not be null!");
         HttpHeaders headers = RequestUtils.headers(request);
         agentContext = UserAgentHelper.convert(headers);
         UserAgentContextHolder.setUserAgentContext(agentContext);
      }

      return agentContext;
   }
}
