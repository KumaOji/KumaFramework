package com.kuma.boot.encrypt.wrapper;

import com.kuma.boot.encrypt.handler.EncryptHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

public class EncryptRequestWrapperFactory {
   public static HttpServletRequest getWrapper(HttpServletRequest request, EncryptHandler encryptService) throws IOException, ServletException {
      String contentType = request.getContentType();
      int contentLength = request.getContentLength();
      if (contentType != null && contentLength != 0) {
         contentType = contentType.toLowerCase();
         return (HttpServletRequest)(contentIsJson(contentType) ? new EncryptBodyRequestWrapper(request, encryptService) : request);
      } else {
         return request;
      }
   }

   public static HttpServletRequest getCacheWarpper(HttpServletRequest request) throws IOException, ServletException {
      return (HttpServletRequest)("POST".equalsIgnoreCase(request.getMethod()) && contentIsJson(request.getContentType()) ? new CacheRequestWrapper(request) : request);
   }

   public static boolean contentIsJson(String contentType) {
      return contentType.equals("application/json".toLowerCase()) || contentType.equals("application/json".toLowerCase());
   }
}
