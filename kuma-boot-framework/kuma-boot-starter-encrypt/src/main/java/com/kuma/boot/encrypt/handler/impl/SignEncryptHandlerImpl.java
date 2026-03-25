package com.kuma.boot.encrypt.handler.impl;

import cn.hutool.crypto.digest.MD5;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.encrypt.exception.EncryptException;
import com.kuma.boot.encrypt.handler.SignEncryptHandler;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SignEncryptHandlerImpl implements SignEncryptHandler {
   public Object handle(Object proceed, long timeout, TimeUnit timeUnit, String signSecret, Map jsonMap) throws EncryptException {
      Object sign = jsonMap.get("sign");
      Object timestamp = jsonMap.get("timestamp");
      this.checkParam(sign, timestamp, timeout, timeUnit);
      String digestMd5 = this.getDigest(jsonMap, signSecret, StandardCharsets.UTF_8);
      LogUtils.debug("加密后的字符：" + digestMd5, new Object[0]);
      if (!digestMd5.equals(sign)) {
         throw new EncryptException("Illegal request,Decryption failed");
      } else {
         return proceed;
      }
   }

   private void checkParam(Object sign, Object timestamp, long timeout, TimeUnit timeUnit) {
      if (sign == null) {
         throw new EncryptException("Illegal request,Sign does not exist");
      } else if (timestamp == null) {
         throw new EncryptException("Illegal request,timestamp does not exist");
      } else {
         long now = System.currentTimeMillis();
         long timestampLong = Long.parseLong(timestamp.toString());
         if (now >= timestampLong + timeout || now < timestampLong) {
            throw new EncryptException("非法请求，请求超时");
         }
      }
   }

   @SuppressWarnings("unchecked")
   private String getDigest(Map map, String sortSignSecret, Charset charset) {
      StringBuilder sb = new StringBuilder();
      ((Map<Object, Object>) map).entrySet().stream().filter((entry) -> entry != null && !"sign".equals(entry.getKey())).sorted(Comparator.comparing((entry) -> entry.getKey().toString())).forEach((entry) -> sb.append(entry.getKey().toString()).append("=").append(entry.getValue().toString()).append("&"));
      sb.append("secret").append("=").append(sortSignSecret);
      return MD5.create().digestHex16(sb.toString(), charset);
   }
}
