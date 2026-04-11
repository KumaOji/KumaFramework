package com.kuma.boot.websocket.spring.websocketsimple;

import jakarta.servlet.http.HttpServletRequest;

public final class DefaultTokenHandler implements TokenHandler {
   public DefaultTokenHandler() {
   }

   public LoginInfo getLoginInfo(HttpServletRequest httpServletRequest) {
      String token = httpServletRequest.getParameter("token");
      if (token == null) {
         return null;
      } else {
         LoginInfo loginInfo = new LoginInfo();
         loginInfo.setId(token);
         loginInfo.setType(token);
         return loginInfo;
      }
   }
}
