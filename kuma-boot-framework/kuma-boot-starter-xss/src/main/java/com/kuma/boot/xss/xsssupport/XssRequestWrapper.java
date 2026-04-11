package com.kuma.boot.xss.xsssupport;

import com.kuma.boot.xss.utils.XssUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class XssRequestWrapper extends HttpServletRequestWrapper {
   private final List<String> ignoreParamValueList;

   public XssRequestWrapper(HttpServletRequest request, List<String> ignoreParamValueList) {
      super(request);
      this.ignoreParamValueList = ignoreParamValueList;
   }

   public Map<String, String[]> getParameterMap() {
      Map<String, String[]> requestMap = super.getParameterMap();

      for(Map.Entry<String, String[]> me : requestMap.entrySet()) {
         String[] values = (String[])me.getValue();

         for(int i = 0; i < values.length; ++i) {
            values[i] = XssUtil.xssClean(values[i], this.ignoreParamValueList);
         }
      }

      return requestMap;
   }

   public String getQueryString() {
      String queryString = super.getQueryString();
      if (null != queryString) {
         queryString = URLDecoder.decode(queryString, StandardCharsets.UTF_8);
      }

      return XssUtil.xssClean(queryString, this.ignoreParamValueList);
   }

   public String[] getParameterValues(String paramString) {
      String[] arrayOfString1 = super.getParameterValues(paramString);
      if (arrayOfString1 == null) {
         return null;
      } else {
         int i = arrayOfString1.length;
         String[] arrayOfString2 = new String[i];

         for(int j = 0; j < i; ++j) {
            arrayOfString2[j] = XssUtil.xssClean(arrayOfString1[j], this.ignoreParamValueList, paramString);
         }

         return arrayOfString2;
      }
   }

   public String getParameter(String paramString) {
      String str = super.getParameter(paramString);
      return str == null ? null : XssUtil.xssClean(str, this.ignoreParamValueList);
   }

   public String getHeader(String paramString) {
      String str = super.getHeader(paramString);
      return str == null ? null : XssUtil.xssClean(str, this.ignoreParamValueList);
   }
}
