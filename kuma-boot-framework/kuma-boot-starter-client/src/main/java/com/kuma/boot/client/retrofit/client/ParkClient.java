package com.kuma.boot.client.retrofit.client;

import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import com.kuma.boot.client.retrofit.model.Person;
import com.kuma.boot.common.model.result.Result;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import java.util.concurrent.CompletableFuture;
import reactor.core.publisher.Mono;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

@RetrofitClient(
   baseUrl = "http://www.baidu.com"
)
public interface ParkClient {
   @POST("getString")
   String getString(@Body Person person);

   @GET("person")
   Result<Person> getPerson(@Query("id") Long id);

   @GET("person")
   CompletableFuture<Result<Person>> getPersonCompletableFuture(@Query("id") Long id);

   @POST("savePerson")
   Void savePersonVoid(@Body Person person);

   @GET("person")
   Response<Result<Person>> getPersonResponse(@Query("id") Long id);

   @GET("person")
   Call<Result<Person>> getPersonCall(@Query("id") Long id);

   @GET("person")
   Mono<Result<Person>> monoPerson(@Query("id") Long id);

   @GET("person")
   Single<Result<Person>> singlePerson(@Query("id") Long id);

   @GET("ping")
   Completable ping();
}
