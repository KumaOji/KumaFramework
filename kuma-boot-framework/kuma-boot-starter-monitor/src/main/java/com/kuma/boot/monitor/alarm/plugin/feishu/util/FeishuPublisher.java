package com.kuma.boot.monitor.alarm.plugin.feishu.util;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeishuPublisher {
   private static final String TEMPLATE = "title:\t%s\n\ncontent:\t%s";
   private static final Logger logger = LoggerFactory.getLogger(FeishuPublisher.class);
   private static final String FEISHU_URL = "https://open.feishu.cn/open-apis/bot/hook/";
   private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
   private static OkHttpClient okHttpClient = new OkHttpClient();

   public FeishuPublisher() {
   }

   public static void sendMessage(String title, String content, String token) {
      try {
         doPost(title, content, token);
      } catch (Exception e) {
         logger.error("failed to publish msg: {} to DingDing! {}", content, e);
      }

   }

   public static String doPost(String title, String content, String token) throws IOException {
      RequestBody body = RequestBody.create(buildTextMsgBody(title, content), JSON);
      Response response = okHttpClient.newCall((new Request.Builder()).url("https://open.feishu.cn/open-apis/bot/hook/" + token).post(body).build()).execute();

      String var5;
      try {
         var5 = response.body().string();
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

      return var5;
   }

   private static String buildTextMsgBody(String title, String content) {
      JSONObject msg = new JSONObject(4);
      msg.put("title", title);
      msg.put("text", content);
      return msg.toJSONString(new JSONWriter.Feature[0]);
   }
}
