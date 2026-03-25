package com.kuma.boot.openapi.common.util;

import cn.hutool.core.util.StrUtil;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Util {
   public static String bytesToBase64(byte[] bytes) {
      byte[] encodedBytes = Base64.getEncoder().encode(bytes);
      return new String(encodedBytes, StandardCharsets.UTF_8);
   }

   public static byte[] base64ToBytes(String base64Str) {
      byte[] bytes = base64Str.getBytes(StandardCharsets.UTF_8);
      return Base64.getDecoder().decode(bytes);
   }

   public static String strToBase64(String str) {
      if (StrUtil.isEmpty(str)) {
         return "";
      } else {
         byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
         return bytesToBase64(bytes);
      }
   }

   public static String base64ToStr(String base64Str) {
      if (StrUtil.isEmpty(base64Str)) {
         return "";
      } else {
         byte[] bytes = base64ToBytes(base64Str);
         return new String(bytes, StandardCharsets.UTF_8);
      }
   }
}
