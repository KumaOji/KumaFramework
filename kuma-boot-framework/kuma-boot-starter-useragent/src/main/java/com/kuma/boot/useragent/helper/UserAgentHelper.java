package com.kuma.boot.useragent.helper;

import com.kuma.boot.core.utils.context.ContextUtils;
import com.kuma.boot.useragent.domain.UserAgent;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpHeaders;

public class UserAgentHelper {
   private static final ConversionService conversionService = (ConversionService)ContextUtils.getBean(ConversionService.class);

   public UserAgentHelper() {
   }

   public static UserAgent convert(HttpHeaders headers) {
      return (UserAgent)conversionService.convert(headers, UserAgent.class);
   }
}
