package com.kuma.boot.monitor.ping;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.util.StringUtils;

public class PingFilter implements Filter {
   public PingFilter() {
   }

   public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
      HttpServletRequest request = (HttpServletRequest)servletRequest;
      HttpServletResponse response = (HttpServletResponse)servletResponse;
      String contextPath = StringUtils.trimTrailingCharacter(request.getContextPath(), '/');
      String uri = request.getRequestURI();
      if (uri.startsWith(contextPath + "/health/ping")) {
         response.setHeader("Content-type", "text/html;charset=UTF-8");
         response.setCharacterEncoding("UTF-8");
         response.getWriter().append("ok");
         response.getWriter().flush();
         response.getWriter().close();
      }

   }

   public void destroy() {
   }
}
