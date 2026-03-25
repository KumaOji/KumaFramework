package com.kuma.boot.encrypt.filter;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.encrypt.handler.EncryptHandler;
import com.kuma.boot.encrypt.handler.InitHandler;
import com.kuma.boot.encrypt.wrapper.EncryptRequestWrapper;
import com.kuma.boot.encrypt.wrapper.EncryptResponseWrapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class EncryptFilter implements Filter {
   private EncryptHandler encryptHandler;
   private static final AtomicBoolean isEncryptAnnotation = new AtomicBoolean(false);
   private static final Set ENCRYPT_CACHE_URI = new HashSet();

   public EncryptFilter(EncryptHandler encryptHandler) {
      this.encryptHandler = encryptHandler;
   }

   public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
      if (isEncryptAnnotation.get()) {
         if (this.checkUri(((HttpServletRequest)servletRequest).getRequestURI())) {
            this.chain(servletRequest, servletResponse, filterChain);
         } else {
            filterChain.doFilter(servletRequest, servletResponse);
         }
      } else {
         HttpServletRequest request = (HttpServletRequest)servletRequest;
         String contentType = request.getContentType();
         if (!request.getRequestURI().startsWith("/actuator") && contentType != null && contentType.equalsIgnoreCase("application/json")) {
            this.chain(servletRequest, servletResponse, filterChain);
         } else {
            filterChain.doFilter(servletRequest, servletResponse);
         }
      }

   }

   private void chain(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
      EncryptRequestWrapper request = new EncryptRequestWrapper((HttpServletRequest)servletRequest, this.encryptHandler);
      EncryptResponseWrapper response = new EncryptResponseWrapper((HttpServletResponse)servletResponse);
      filterChain.doFilter(request, response);
      byte[] responseData = response.getResponseData();
      if (responseData.length != 0) {
         LogUtils.info("接收的报文：" + new String(responseData), new Object[0]);
         byte[] encryptByte = this.encryptHandler.encode(URLEncoder.encode(new String(responseData), StandardCharsets.UTF_8).getBytes());
         LogUtils.info("加密后的报文：" + new String(encryptByte), new Object[0]);
         servletResponse.setContentLength(-1);
         servletResponse.setContentType("application/json");
         ServletOutputStream outputStream = servletResponse.getOutputStream();
         LogUtils.info("outputStream: " + outputStream.toString(), new Object[0]);
         outputStream.write(encryptByte);
         outputStream.flush();
      }
   }

   private boolean checkUri(String uri) {
      uri = uri.replaceAll("//+", "/");
      if (uri.endsWith("/")) {
         uri = uri.substring(0, uri.length() - 1);
      }

      return ENCRYPT_CACHE_URI.contains(uri);
   }

   public void init(FilterConfig filterConfig) throws ServletException {
      InitHandler.handler(filterConfig, ENCRYPT_CACHE_URI, isEncryptAnnotation);
   }

   public void destroy() {
   }
}
