package com.kuma.boot.xss.interceptor;

import com.kuma.boot.common.utils.reflect.ClassUtils;
import com.kuma.boot.xss.autoconfigure.properties.XssProperties;
import com.kuma.boot.xss.xsssupport.XssCleanIgnore;
import com.kuma.boot.xss.xsssupport.XssHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

public class XssCleanInterceptor implements AsyncHandlerInterceptor {
   private final XssProperties xssProperties;

   public XssCleanInterceptor(XssProperties xssProperties) {
      this.xssProperties = xssProperties;
   }

   public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
      if (!(handler instanceof HandlerMethod)) {
         return true;
      } else if (!this.xssProperties.getEnabled()) {
         return true;
      } else {
         HandlerMethod handlerMethod = (HandlerMethod)handler;
         XssCleanIgnore xssCleanIgnore = (XssCleanIgnore)ClassUtils.getAnnotation(handlerMethod, XssCleanIgnore.class);
         if (xssCleanIgnore != null) {
            XssHolder.setIgnore(xssCleanIgnore);
         }

         return true;
      }
   }

   public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
      XssHolder.remove();
   }

   public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
      XssHolder.remove();
   }
}
