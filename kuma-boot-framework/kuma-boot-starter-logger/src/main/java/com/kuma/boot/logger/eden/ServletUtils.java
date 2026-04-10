package com.kuma.boot.logger.eden;

import cn.hutool.core.text.CharSequenceUtil;
import com.kuma.boot.common.utils.lang.StringUtils;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public final class ServletUtils {
   public static final String ACCEPT_RANGES = "bytes";
   public static final String CONTENT_DISPOSITION_ATTACH = "attachment;filename={0}";
   public static final String APPLICATION_JSON_UTF8_VALUE = "application/json;charset=UTF-8";

   public ServletUtils() {
   }

   public static Map<String, String> toMap(ServletRequest request) {
      Map<String, String[]> parameterMap = request.getParameterMap();
      Map<String, String> returnMap = new HashMap();

      for(Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
         Object valueObj = entry.getValue();
         if (StringUtils.isEmpty(valueObj)) {
            returnMap.put((String)entry.getKey(), "");
         } else {
            String[] values = (String[])valueObj;
            StringBuilder sb = new StringBuilder();

            for(String val : values) {
               sb.append(val).append(".");
            }

            if (sb.indexOf(".") >= 0) {
               sb.delete(sb.length() - 1, sb.length());
            }

            returnMap.put((String)entry.getKey(), sb.toString());
         }
      }

      return returnMap;
   }

   public static ServletRequestAttributes getRequestAttributes() {
      return (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
   }

   public static HttpServletRequest getRequest() {
      return getRequestAttributes().getRequest();
   }

   public static HttpServletResponse getResponse() {
      return getRequestAttributes().getResponse();
   }

   public static HttpSession getSession() {
      return getRequest().getSession();
   }

   public static String getRemoteUser() {
      HttpServletRequest request = getRequest();
      return getRemoteUser(request);
   }

   public static String getRemoteUser(HttpServletRequest request) {
      return CharSequenceUtil.trimToEmpty(request.getRemoteUser());
   }

   public static String getRemoteAddr() {
      HttpServletRequest request = getRequest();
      return getRemoteAddr(request);
   }

   public static String getRemoteAddr(HttpServletRequest request) {
      return CharSequenceUtil.trimToEmpty(IpConfigUtils.parseIpAddress(request));
   }

   public static String getRemoteHost() {
      HttpServletRequest request = getRequest();
      return getRemoteHost(request);
   }

   public static String getRemoteHost(HttpServletRequest request) {
      return CharSequenceUtil.trimToEmpty(request.getRemoteHost());
   }

   public static String getLocalAddr() {
      HttpServletRequest request = getRequest();
      return getLocalAddr(request);
   }

   public static String getLocalAddr(HttpServletRequest request) {
      return CharSequenceUtil.trimToEmpty(request.getLocalAddr());
   }

   public static String getRequestURI() {
      return getRequestURI(getRequest());
   }

   public static String getRequestURI(HttpServletRequest request) {
      return CharSequenceUtil.trimToEmpty(request.getRequestURI());
   }

   public static String getRequestURL() {
      return getRequestURL(getRequest());
   }

   public static String getRequestURL(HttpServletRequest request) {
      return request.getRequestURL().toString();
   }

   public static String getContextPath() {
      HttpServletRequest request = getRequest();
      return getContextPath(request);
   }

   public static String getContextPath(HttpServletRequest request) {
      return CharSequenceUtil.trimToEmpty(request.getContextPath());
   }

   public static String getQueryString() {
      HttpServletRequest request = getRequest();
      return getQueryString(request);
   }

   public static String getQueryString(HttpServletRequest request) {
      return CharSequenceUtil.trimToEmpty(request.getQueryString());
   }

   public static String getRequestPath() {
      HttpServletRequest request = getRequest();
      return getRequestPath(request);
   }

   public static String getRequestPath(HttpServletRequest request) {
      String queryString = request.getQueryString();
      String requestURI = request.getRequestURI();
      if (StringUtils.isNotEmpty(queryString)) {
         requestURI = requestURI + "?" + queryString;
      }

      int index = requestURI.indexOf("&");
      if (index > -1) {
         requestURI = requestURI.substring(0, index);
      }

      return requestURI.substring(request.getContextPath().length() + 1);
   }

   public static String getRequestParameters(HttpServletRequest request) {
      Map<String, String[]> parameterMap = request.getParameterMap();
      return parameterMap.isEmpty() ? "" : (String)parameterMap.entrySet().stream().map((entry) -> {
         String var10000 = (String)entry.getKey();
         return var10000 + "=" + String.join(",", (CharSequence[])entry.getValue());
      }).collect(Collectors.joining(", "));
   }

   public static String getRequestHeaders(HttpServletRequest request) {
      StringBuilder sb = new StringBuilder();
      Enumeration<String> headerNames = request.getHeaderNames();

      for(int i = 0; headerNames.hasMoreElements(); ++i) {
         if (i > 0) {
            sb.append(", ");
         }

         String headerName = (String)headerNames.nextElement();
         String headerValue = request.getHeader(headerName);
         sb.append(headerName).append("=").append(headerValue);
      }

      return sb.toString();
   }

   public static String getRequestBody(HttpServletRequest request) {
      try {
         BufferedReader reader = request.getReader();

         String var2;
         label45: {
            try {
               if (reader != null) {
                  var2 = (String)reader.lines().collect(Collectors.joining(System.lineSeparator()));
                  break label45;
               }
            } catch (Throwable var5) {
               if (reader != null) {
                  try {
                     reader.close();
                  } catch (Throwable var4) {
                     var5.addSuppressed(var4);
                  }
               }

               throw var5;
            }

            if (reader != null) {
               reader.close();
            }

            return null;
         }

         if (reader != null) {
            reader.close();
         }

         return var2;
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   public static String getResponseHeaders(HttpServletResponse response) {
      return (String)response.getHeaderNames().stream().map((name) -> name + "=" + response.getHeader(name)).collect(Collectors.joining(", "));
   }

   public static String getResponseBody(HttpServletResponse response) {
      return "";
   }

   public static boolean isAjaxRequest() {
      HttpServletRequest request = getRequest();
      return isAjaxRequest(request);
   }

   public static boolean isAjaxRequest(HttpServletRequest request) {
      String accept = request.getHeader("accept");
      if (accept != null && accept.contains("application/json")) {
         return true;
      } else {
         String xRequestedWith = request.getHeader("X-Requested-With");
         return xRequestedWith != null && xRequestedWith.contains("XMLHttpRequest");
      }
   }

   public static <X> ResponseEntity<X> wrapOrNotFound(X maybeResponse) {
      return wrapOrNotFound(maybeResponse, (HttpHeaders)null);
   }

   public static <X> ResponseEntity<X> wrapOrNotFound(X maybeResponse, HttpHeaders header) {
      return maybeResponse == null ? new ResponseEntity(HttpStatus.NOT_FOUND) : ((ResponseEntity.BodyBuilder)ResponseEntity.ok().headers(header)).body(maybeResponse);
   }
}
