package com.kuma.boot.useragent.servlet;

import com.kuma.boot.common.utils.servlet.RequestUtils;
import com.kuma.boot.useragent.domain.UserAgent;
import com.kuma.boot.useragent.helper.UserAgentHelper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;

public class UserAgentFilter extends OncePerRequestFilter {
   public UserAgentFilter() {
   }

   protected final void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
      HttpHeaders headers = RequestUtils.headers(request);
      UserAgent userAgent = UserAgentHelper.convert(headers);
      UserAgentContextHolder.setUserAgentContext(userAgent);
      filterChain.doFilter(request, response);
      UserAgentContextHolder.cleanUserAgentContext();
   }
}
