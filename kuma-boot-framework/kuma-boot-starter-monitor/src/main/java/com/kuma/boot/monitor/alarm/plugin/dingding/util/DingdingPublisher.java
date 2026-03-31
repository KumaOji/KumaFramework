package com.kuma.boot.monitor.alarm.plugin.dingding.util;

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

public class DingdingPublisher {
   private static final String TEMPLATE = "title:\t%s\n\ncontent:\t%s";
   private static final Logger logger = LoggerFactory.getLogger(DingdingPublisher.class);
   private static final String DING_TALK_URL = "https://oapi.dingtalk.com/robot/send?access_token=";
   private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
   private static OkHttpClient okHttpClient = new OkHttpClient();

   public DingdingPublisher() {
   }

   public static void sendMessage(String title, String content, String token) {
      String msg = String.format("title:\t%s\n\ncontent:\t%s", title, content);

      try {
         doPost(msg, token);
      } catch (Exception e) {
         logger.error("failed to publish msg: {} to DingDing! {}", msg, e);
      }

   }

   public static String doPost(String msg, String token) throws IOException {
      RequestBody body = RequestBody.create(buildTextMsgBody(msg), JSON);
      Response response = okHttpClient.newCall((new Request.Builder()).url("https://oapi.dingtalk.com/robot/send?access_token=" + token).post(body).build()).execute();

      String var4;
      try {
         var4 = response.body().string();
      } catch (Throwable var7) {
         if (response != null) {
            try {
               response.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (response != null) {
         response.close();
      }

      return var4;
   }

   private static String buildTextMsgBody(String content) {
      JSONObject msg = new JSONObject();
      msg.put("msgtype", "text");
      JSONObject text = new JSONObject();
      text.put("content", content);
      msg.put("text", text);
      return msg.toJSONString(new JSONWriter.Feature[0]);
   }
}
