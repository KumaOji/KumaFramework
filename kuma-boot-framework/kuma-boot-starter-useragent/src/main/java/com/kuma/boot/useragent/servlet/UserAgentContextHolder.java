package com.kuma.boot.useragent.servlet;

import com.kuma.boot.useragent.domain.UserAgent;
import org.springframework.core.NamedInheritableThreadLocal;

public class UserAgentContextHolder {
   private static final ThreadLocal<UserAgent> inheritableContext = new NamedInheritableThreadLocal("inheritable useragent context");

   public UserAgentContextHolder() {
   }

   public static void setUserAgentContext(UserAgent userAgent) {
      inheritableContext.set(userAgent);
   }

   public static UserAgent getUserAgentContext() {
      return (UserAgent)inheritableContext.get();
   }

   public static void cleanUserAgentContext() {
      inheritableContext.remove();
   }
}
