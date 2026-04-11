package com.taotao.boot.client.forest.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.Var;
import com.dtflys.forest.extensions.BasicAuth;

@BaseRequest(
   baseURL = "http://gitee:8080/hello"
)
public interface BaiduClient {
   @Post("/hello/user?username={username}")
   @BasicAuth(
      username = "{username}",
      password = "bar"
   )
   String getAccessToken(@Var("username") String username);

   @Get("https://apis.map.qq.com/ws/geocoder/v1/?location=29.57,106.55&key=ZE6BZ-HH33P-IU4DI-VL2H3-VEEIS-35FFH")
   String getData();
}
