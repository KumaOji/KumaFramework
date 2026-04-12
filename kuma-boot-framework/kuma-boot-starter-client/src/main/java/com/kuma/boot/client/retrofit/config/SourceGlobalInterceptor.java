package com.kuma.boot.client.retrofit.config;

import com.github.lianjiatech.retrofit.spring.boot.interceptor.GlobalInterceptor;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

@Component
public class SourceGlobalInterceptor implements GlobalInterceptor {
   public SourceGlobalInterceptor() {
   }

   public Response intercept(Interceptor.Chain chain) throws IOException {
      Request request = chain.request();
      Request newReq = request.newBuilder().addHeader("source", "test").build();
      return chain.proceed(newReq);
   }
}
