package com.kuma.boot.client.retrofit.client;

import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import com.kuma.boot.common.model.result.Result;
import retrofit2.http.GET;
import retrofit2.http.Query;

@RetrofitClient(
   baseUrl = "${test.baseUrl}",
   sourceOkHttpClient = "testSourceOkHttpClient"
)
public interface CustomOkHttpTestApi {
   @GET("person")
   Result<String> getPerson(@Query("id") Long id);
}
