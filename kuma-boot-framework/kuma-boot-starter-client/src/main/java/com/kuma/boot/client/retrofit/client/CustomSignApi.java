package com.kuma.boot.client.retrofit.client;

import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import com.kuma.boot.client.retrofit.sign.Sign;
import com.kuma.boot.common.model.result.Result;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

@RetrofitClient(
   baseUrl = "${test.baseUrl}"
)
@Sign(
   accessKeyId = "${test.accessKeyId}",
   accessKeySecret = "${test.accessKeySecret}",
   exclude = {"/api/test/person"}
)
public interface CustomSignApi {
   @GET("person")
   Result<String> getPerson(@Query("id") Long id);

   @POST("savePerson")
   Result<String> savePerson(@Body Result person);
}
