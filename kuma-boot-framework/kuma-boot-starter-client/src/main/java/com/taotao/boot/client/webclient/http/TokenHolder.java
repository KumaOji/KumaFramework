package com.taotao.boot.client.webclient.http;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class TokenHolder {
   public TokenHolder() {
   }

   public void putToken(String token) {
      RequestAttributes ra = RequestContextHolder.getRequestAttributes();
      HttpServletRequest request = ((ServletRequestAttributes)ra).getRequest();
      request.getSession().setAttribute("token", token);
   }

   public String getToken() {
      RequestAttributes ra = RequestContextHolder.getRequestAttributes();
      HttpServletRequest request = ((ServletRequestAttributes)ra).getRequest();
      Object token = request.getSession().getAttribute("token");
      return token != null ? (String)token : null;
   }
}
