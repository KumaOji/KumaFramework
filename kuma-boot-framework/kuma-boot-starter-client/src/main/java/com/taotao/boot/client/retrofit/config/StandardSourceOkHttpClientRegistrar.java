package com.taotao.boot.client.retrofit.config;

import com.github.lianjiatech.retrofit.spring.boot.core.SourceOkHttpClientRegistrar;
import com.github.lianjiatech.retrofit.spring.boot.core.SourceOkHttpClientRegistry;
import com.kuma.boot.common.utils.log.LogUtils;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Component;

@Component
public class StandardSourceOkHttpClientRegistrar implements SourceOkHttpClientRegistrar {
   public StandardSourceOkHttpClientRegistrar() {
   }

   public void register(SourceOkHttpClientRegistry registry) {
      registry.register("testSourceOkHttpClient", (new OkHttpClient.Builder()).addInterceptor((chain) -> {
         LogUtils.info("============\u4f7f\u7528testSourceOkHttpClient=============", new Object[0]);
         return chain.proceed(chain.request());
      }).build());
   }
}
