package com.kuma.boot.logger.eden.bootstrap.filter;

import cn.hutool.core.text.CharSequenceUtil;
import com.kuma.boot.common.constant.SymbolConstants;
import com.kuma.boot.logger.eden.IpConfigUtils;
import com.kuma.boot.logger.eden.ServletUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.MDC;
import org.springframework.core.env.Environment;

public class BootstrapLogHttpFilter extends HttpFilter {
   private boolean enabledMdc = false;
   private final Environment env;

   public BootstrapLogHttpFilter(Environment env) {
      this.env = env;
   }

   protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
      if (this.enabledMdc) {
         String appName = CharSequenceUtil.trimToEmpty(this.env.getProperty("spring.application.name"));
         String profile = CharSequenceUtil.trimToEmpty(String.join(SymbolConstants.COMMA, getActiveProfiles(this.env)));
         String requestURI = ServletUtils.getRequestURI(req);
         String remoteUser = ServletUtils.getRemoteUser(req);
         String remoteAddr = IpConfigUtils.parseIpAddress(req);
         String localAddr = ServletUtils.getLocalAddr(req);
         MDC.put("app", appName);
         MDC.put("profile", profile);
         MDC.put("requestURI", requestURI);
         MDC.put("remoteUser", remoteUser);
         MDC.put("remoteAddr", remoteAddr);
         MDC.put("localAddr", localAddr);
      }

      chain.doFilter(req, res);
   }

   public void setEnabledMdc(boolean enabledMdc) {
      this.enabledMdc = enabledMdc;
   }

   private static String[] getActiveProfiles(Environment env) {
      String[] profiles = env.getActiveProfiles();
      return profiles.length == 0 ? env.getDefaultProfiles() : profiles;
   }
}
