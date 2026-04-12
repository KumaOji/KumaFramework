package com.kuma.boot.client.webclient.http;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange
public interface UmsAdminApi {
   @PostExchange("admin/login")
   CommonResult<LoginInfo> login(@RequestParam("username") String username, @RequestParam("password") String password);

   public static class LoginInfo {
      public LoginInfo() {
      }
   }

   public static class CommonResult<T> {
      public CommonResult() {
      }
   }
}
