package com.taotao.boot.client.forest.client;

import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.annotation.Var;
import com.kuma.boot.client.forest.auth.MyAuth;

public interface MyClient {
   @Request("http://127.0.0.1:8080/hello")
   String helloForest();

   @Get("/hello/user?username={username}")
   @MyAuth(
      username = "{username}",
      password = "bar"
   )
   String send(@Var("username") String username);
}
