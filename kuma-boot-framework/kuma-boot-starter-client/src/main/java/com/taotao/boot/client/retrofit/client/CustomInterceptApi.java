package com.taotao.boot.client.retrofit.client;

import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import com.github.lianjiatech.retrofit.spring.boot.interceptor.Intercept;
import com.taotao.boot.client.retrofit.config.TimeStampInterceptor;
import com.kuma.boot.common.model.result.Result;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

@RetrofitClient(
   baseUrl = "${test.baseUrl}"
)
@Intercept(
   handler = TimeStampInterceptor.class,
   include = {"/api/**"},
   exclude = {"/api/test/savePerson"}
)
public interface CustomInterceptApi {
   @GET("person")
   Result<String> getPerson(@Query("id") Long id);

   @POST("savePerson")
   Result<String> savePerson(@Body Result person);
}
