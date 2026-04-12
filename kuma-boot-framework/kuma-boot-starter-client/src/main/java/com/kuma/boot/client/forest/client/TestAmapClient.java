package com.kuma.boot.client.forest.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.DataFile;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.JSONBody;
import com.dtflys.forest.annotation.LogHandler;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.Retry;
import com.dtflys.forest.annotation.Retryer;
import com.dtflys.forest.annotation.Success;
import com.dtflys.forest.annotation.Var;
import com.dtflys.forest.annotation.XMLBody;
import com.dtflys.forest.callback.OnProgress;
import com.dtflys.forest.extensions.BasicAuth;
import com.dtflys.forest.extensions.DownloadFile;
import com.dtflys.forest.extensions.OAuth2;
import com.dtflys.forest.extensions.OAuth2.GrantType;
import com.dtflys.forest.http.ForestRequest;
import com.kuma.boot.client.forest.config.MyLogHandler;
import com.kuma.boot.client.forest.config.MyRetryCondition;
import com.kuma.boot.client.forest.config.MyRetryer;
import com.kuma.boot.client.forest.config.MySuccessCondition;
import java.io.File;
import java.util.List;
import java.util.Map;

@BaseRequest(
   baseURL = "http://127.0.0.1:8080/hello"
)
public interface TestAmapClient {
   @Get("http://ditu.amap.com/service/regeo?longitude={0}&latitude={1}")
   @Retry(
      maxRetryCount = "3",
      maxRetryInterval = "10",
      condition = MyRetryCondition.class
   )
   @Retryer(MyRetryer.class)
   @LogHandler(MyLogHandler.class)
   @Success(
      condition = MySuccessCondition.class
   )
   Map getLocation(String longitude, String latitude);

   @Post("/test/json")
   String postJsonMap(@JSONBody Map mapObj);

   @Post("/test/json")
   String postJsonText(@JSONBody String jsonText);

   @Post("/test/xml")
   String postXmlBodyString(@XMLBody String xml);

   @Post("/upload")
   Map upload(@DataFile("file") String filePath, OnProgress onProgress);

   @Post("/upload")
   ForestRequest<Map> uploadByteArrayMap(@DataFile(value = "file",fileName = "{_key}") Map<String, byte[]> byteArrayMap);

   @Post("/upload")
   ForestRequest<Map> uploadByteArrayList(@DataFile(value = "file",fileName = "test-img-{_index}.jpg") List<byte[]> byteArrayList);

   @Get("http://127.0.0.1:8080/images/xxx.jpg")
   @DownloadFile(
      dir = "{0}"
   )
   File downloadFile(String dir, OnProgress onProgress);

   @Post("/hello/user?username={username}")
   @BasicAuth(
      username = "{username}",
      password = "bar"
   )
   String send(@Var("username") String username);

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
