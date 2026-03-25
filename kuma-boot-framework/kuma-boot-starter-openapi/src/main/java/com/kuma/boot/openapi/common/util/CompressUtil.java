package com.kuma.boot.openapi.common.util;

import cn.hutool.core.util.ZipUtil;
import java.nio.charset.StandardCharsets;

public class CompressUtil {
   public static byte[] compressText(String text) {
      byte[] bodyBytes = text.getBytes(StandardCharsets.UTF_8);
      return ZipUtil.gzip(bodyBytes);
   }

   public static byte[] compress(byte[] bytes) {
      return ZipUtil.gzip(bytes);
   }

   public static String decompressToText(byte[] compressedBytes) {
      byte[] decompressedBytes = ZipUtil.unGzip(compressedBytes);
      return new String(decompressedBytes, StandardCharsets.UTF_8);
   }

   public static byte[] decompress(byte[] compressedBytes) {
      return ZipUtil.unGzip(compressedBytes);
   }
}
