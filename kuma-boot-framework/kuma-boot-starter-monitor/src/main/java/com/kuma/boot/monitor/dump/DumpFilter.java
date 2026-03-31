package com.kuma.boot.monitor.dump;

import com.kuma.boot.common.utils.context.ContextUtils;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import org.springframework.util.StringUtils;

public class DumpFilter implements Filter {
   public DumpFilter() {
   }

   public void init(FilterConfig filterConfig) throws ServletException {
   }

   public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
      HttpServletRequest request = (HttpServletRequest)servletRequest;
      HttpServletResponse response = (HttpServletResponse)servletResponse;
      String contextPath = StringUtils.trimTrailingCharacter(request.getContextPath(), '/');
      String uri = request.getRequestURI();
      DumpProvider dumpProvider = (DumpProvider)ContextUtils.getBean(DumpProvider.class, true);
      if (uri.startsWith(contextPath + "/health/dump") && Objects.nonNull(dumpProvider)) {
         if (uri.startsWith(contextPath + "/health/dump/zip/")) {
            dumpProvider.zip(request.getParameter("name"));
         } else if (uri.startsWith(contextPath + "/health/dump/download/")) {
            dumpProvider.download(request.getParameter("name"));
         } else if (uri.startsWith(contextPath + "/health/dump/do/")) {
            dumpProvider.dump();
         } else {
            dumpProvider.list();
         }
      }

   }

   public void destroy() {
   }
}
