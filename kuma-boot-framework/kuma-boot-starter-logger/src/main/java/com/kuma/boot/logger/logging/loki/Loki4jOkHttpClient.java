package com.kuma.boot.logger.logging.loki;

import com.github.loki4j.client.http.HttpConfig;
import com.github.loki4j.client.http.Loki4jHttpClient;
import com.github.loki4j.client.http.LokiResponse;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Loki4jOkHttpClient implements Loki4jHttpClient {
   private final HttpConfig conf;
   private final OkHttpClient httpClient;
   private final MediaType mediaType;
   private final Request requestBuilder;
   private byte[] bodyBuffer = new byte[0];

   public Loki4jOkHttpClient(HttpConfig conf) {
      this.conf = conf;
      this.httpClient = okHttpClientBuilder(conf);
      this.mediaType = MediaType.get(conf.contentType);
      this.requestBuilder = requestBuilder(conf);
   }

   private static OkHttpClient okHttpClientBuilder(HttpConfig conf) {
      return (new OkHttpClient.Builder()).connectTimeout(conf.connectionTimeoutMs, TimeUnit.MICROSECONDS).writeTimeout(conf.requestTimeoutMs, TimeUnit.MICROSECONDS).readTimeout(conf.requestTimeoutMs, TimeUnit.MICROSECONDS).build();
   }

   private static Request requestBuilder(HttpConfig conf) {
      Request.Builder request = (new Request.Builder()).url(conf.pushUrl).addHeader("Content-Type", conf.contentType);
      conf.tenantId.ifPresent((tenant) -> request.addHeader("X-Scope-OrgID", tenant));
      conf.basicAuthToken().ifPresent((token) -> request.addHeader("Authorization", "Basic " + token));
      return request.build();
   }

   public HttpConfig getConfig() {
      return this.conf;
   }

   public LokiResponse send(ByteBuffer batch) throws Exception {
      Request.Builder request = this.requestBuilder.newBuilder();
      if (batch.hasArray()) {
         request.post(RequestBody.create(batch.array(), this.mediaType, batch.position(), batch.remaining()));
      } else {
         int len = batch.remaining();
         if (len > this.bodyBuffer.length) {
            this.bodyBuffer = new byte[len];
         }

         batch.get(this.bodyBuffer, 0, len);
         request.post(RequestBody.create(this.bodyBuffer, this.mediaType, 0, len));
      }

      Call call = this.httpClient.newCall(request.build());

      try {
         Response response = call.execute();

         LokiResponse var6;
         try {
            String body = response.body() != null ? response.body().string() : "";
            var6 = new LokiResponse(response.code(), body);
         } catch (Throwable var8) {
            if (response != null) {
               try {
                  response.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (response != null) {
            response.close();
         }

         return var6;
      } catch (IOException e) {
         throw new RuntimeException("Error while sending batch to Loki", e);
      }
   }

   public void close() throws Exception {
      this.httpClient.dispatcher().executorService().shutdown();
      this.httpClient.connectionPool().evictAll();

      assert this.httpClient.cache() != null;

      this.httpClient.cache().close();
   }
}
