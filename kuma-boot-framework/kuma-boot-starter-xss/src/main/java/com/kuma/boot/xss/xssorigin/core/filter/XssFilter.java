package com.kuma.boot.xss.xssorigin.core.filter;

import com.kuma.boot.xss.xssorigin.core.propertie.XssProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

public class XssFilter extends OncePerRequestFilter {
   private final XssProperties xssProperties;
   private final PathMatcher pathMatcher;

   public XssFilter(XssProperties xssProperties, PathMatcher pathMatcher) {
      this.xssProperties = xssProperties;
      this.pathMatcher = pathMatcher;
   }

   protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
      filterChain.doFilter(new XssRequestWrapper(request), response);
   }

   protected boolean shouldNotFilter(HttpServletRequest request) {
      return !this.xssProperties.isEnable() ? true : this.xssProperties.getIgnoreUrls().stream().anyMatch((excludeUrl) -> this.pathMatcher.match(excludeUrl, request.getRequestURI()));
   }
}
