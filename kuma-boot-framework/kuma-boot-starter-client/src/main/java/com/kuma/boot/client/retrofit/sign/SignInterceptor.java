package com.kuma.boot.client.retrofit.sign;

import com.github.lianjiatech.retrofit.spring.boot.interceptor.BasePathMatchInterceptor;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

@Component
public class SignInterceptor extends BasePathMatchInterceptor {
   private String accessKeyId;
   private String accessKeySecret;

   public SignInterceptor() {
   }

   public void setAccessKeyId(String accessKeyId) {
      this.accessKeyId = accessKeyId;
   }

   public void setAccessKeySecret(String accessKeySecret) {
      this.accessKeySecret = accessKeySecret;
   }

   public Response doIntercept(Interceptor.Chain chain) throws IOException {
      Request request = chain.request();
      Request newReq = request.newBuilder().addHeader("accessKeyId", this.accessKeyId).addHeader("accessKeySecret", this.accessKeySecret).build();
      return chain.proceed(newReq);
   }
}
