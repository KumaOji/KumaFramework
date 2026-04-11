package com.kuma.boot.xss.filter;

import com.kuma.boot.common.utils.log.LogUtils;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

public class XssHttpServletFilter implements Filter {
   public XssHttpServletFilter() {
   }

   public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
      HttpServletRequest request = (HttpServletRequest)servletRequest;
      XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper(request);
      LogUtils.debug("XssHttpServletFilter wrapper request for [{}].", new Object[]{request.getRequestURI()});
      filterChain.doFilter(xssRequest, servletResponse);
   }
}
