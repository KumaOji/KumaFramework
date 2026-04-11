package com.kuma.boot.xss.filter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.xss.xsssupport.XssRequestWrapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import org.springframework.util.AntPathMatcher;

public class XssFilter implements Filter {
   public static final String IGNORE_PATH = "ignorePath";
   public static final String IGNORE_PARAM_VALUE = "ignoreParamValue";
   private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();
   private List<String> ignorePathList;
   private List<String> ignoreParamValueList;

   public XssFilter() {
   }

   public void init(FilterConfig fc) {
      this.ignorePathList = CharSequenceUtil.split(fc.getInitParameter("ignorePath"), ",");
      this.ignoreParamValueList = CharSequenceUtil.split(fc.getInitParameter("ignoreParamValue"), ",");
   }

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
      String uriPath = ((HttpServletRequest)request).getRequestURI();
      if (this.isIgnorePath(uriPath)) {
         LogUtils.debug("\u5ffd\u7565\u8fc7\u6ee4\u8def\u5f84=[{}]", new Object[]{uriPath});
         chain.doFilter(request, response);
      } else {
         LogUtils.debug("\u8fc7\u6ee4\u5668\u5305\u88c5\u8bf7\u6c42\u8def\u5f84=[{}]", new Object[]{uriPath});
         chain.doFilter(new XssRequestWrapper((HttpServletRequest)request, this.ignoreParamValueList), response);
      }
   }

   private boolean isIgnorePath(String uriPath) {
      if (StrUtil.isBlank(uriPath)) {
         return true;
      } else if (uriPath.startsWith("/actuator")) {
         return true;
      } else {
         return CollUtil.isEmpty(this.ignorePathList) ? false : this.ignorePathList.stream().anyMatch((url) -> uriPath.startsWith(url) || ANT_PATH_MATCHER.match(url, uriPath));
      }
   }
}
