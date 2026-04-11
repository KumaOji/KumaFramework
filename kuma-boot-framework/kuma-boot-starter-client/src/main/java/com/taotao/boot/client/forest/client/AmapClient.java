package com.taotao.boot.client.forest.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.Var;
import com.dtflys.forest.extensions.BasicAuth;
import com.dtflys.forest.extensions.OAuth2;
import com.dtflys.forest.extensions.OAuth2.GrantType;

@BaseRequest(
   baseURL = "http://gitee:8080/hello"
)
public interface AmapClient {
   @Post("/hello/user?username={username}")
   @BasicAuth(
      username = "{username}",
      password = "bar"
   )
   String getAccessToken(@Var("username") String username);

   @OAuth2(
      tokenUri = "/auth/oauth/token",
      clientId = "password",
      clientSecret = "xxxxx-yyyyy-zzzzz",
      grantType = GrantType.PASSWORD,
      scope = "any",
      username = "root",
      password = "xxxxxx"
   )
   @Get("/test/data")
   String getData();
}
