package com.kuma.boot.client.retrofit.config;

import com.github.lianjiatech.retrofit.spring.boot.interceptor.BasePathMatchInterceptor;
import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

@Component
public class TimeStampInterceptor extends BasePathMatchInterceptor {
   public TimeStampInterceptor() {
   }

   public Response doIntercept(Interceptor.Chain chain) throws IOException {
      Request request = chain.request();
      HttpUrl url = request.url();
      long timestamp = System.currentTimeMillis();
      HttpUrl newUrl = url.newBuilder().addQueryParameter("timestamp", String.valueOf(timestamp)).build();
      Request newRequest = request.newBuilder().url(newUrl).build();
      return chain.proceed(newRequest);
   }
}
