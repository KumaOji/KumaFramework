package com.kuma.boot.client.forest.config;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.forest.reflection.ForestMethod;
import com.kuma.boot.cache.redis.repository.RedisRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MyInterceptor implements Interceptor {
   private final RedisRepository repository;
   private static final Logger log = LoggerFactory.getLogger(MyInterceptor.class);

   public MyInterceptor(RedisRepository repository) {
      this.repository = repository;
   }

   public void onInvokeMethod(ForestRequest req, ForestMethod method, Object[] args) {
      log.info("on invoke method");
      this.addAttribute(req, "A", "value1");
      this.addAttribute(req, "B", "value2");
   }

   public boolean beforeExecute(ForestRequest req) {
      Object accessToken = this.repository.get("gitee_access_token");
      if (accessToken == null) {
      }

      log.info("invoke Simple beforeExecute");
      req.addHeader("accessToken", accessToken);
      req.addQuery("username", "foo");
      return true;
   }

   public void onSuccess(Void data, ForestRequest req, ForestResponse res) {
      log.info("invoke Simple onSuccess");
      int status = res.getStatusCode();
      String content = res.getContent();
      String result = res.getResult().toString();
      result = (String)res.getResult();
      this.getAttributeAsString(req, "A1");
   }

   public void onError(ForestRuntimeException ex, ForestRequest req, ForestResponse res) {
      log.info("invoke Simple onError");
      int status = res.getStatusCode();
      String content = res.getContent();
      String result = (String)res.getResult();
   }

   public void afterExecute(ForestRequest req, ForestResponse res) {
      log.info("invoke Simple afterExecute");
      int status = res.getStatusCode();
      String content = res.getContent();
      String result = (String)res.getResult();
   }
}
