package com.kuma.boot.openapi.common.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson2.JSONB;
import com.kuma.boot.openapi.common.model.Binary;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class BinaryUtil {
   public static boolean isBinaryParam(Object obj) {
      return obj instanceof Binary || TypeUtil.isBinaryArray(obj.getClass()) || TypeUtil.isBinaryCollection(obj);
   }

   public static String getBinaryString(Binary binary) {
      Binary tmp = (Binary)CommonUtil.cloneInstance(binary);
      long length = binary.getLength();
      tmp.setData((byte[])null);
      tmp.setLength(length);
      return StrObjectConvert.objToStr(tmp, tmp.getClass());
   }

   @SuppressWarnings("unchecked")
   public static String getBinariesString(List binaries) {
      List<Binary> temps = new ArrayList();

      for(Binary binary : (List<Binary>) binaries) {
         Binary tmp = (Binary)CommonUtil.cloneInstance(binary);
         long length = binary.getLength();
         tmp.setData((byte[])null);
         tmp.setLength(length);
         temps.add(tmp);
      }

      return StrObjectConvert.objToStr(temps, temps.getClass());
   }

   public static byte[] buildSingleBinaryBytes(Binary binary, String paramStr) {
      long binaryLength = binary.getLength();
      byte[] binaryDataBytes = binary.getData();
      byte[] paramBytes = paramStr.getBytes(StandardCharsets.UTF_8);
      byte[] paramLengthBytes = NumberUtil.toBytes(paramBytes.length);
      byte[] binaryCountBytes = new byte[]{1};
      byte[] binaryLengthBytes = JSONB.toBytes(binaryLength);
      return ArrayUtil.addAll(new byte[][]{paramLengthBytes, paramBytes, binaryCountBytes, binaryLengthBytes, binaryDataBytes});
   }

   public static byte[] buildMultiBinaryBytes(List binaries, String paramJsonStr) {
      List<byte[]> binaryLengthBytesList = new ArrayList();
      List<byte[]> binaryDataBytesList = new ArrayList();

      for(Object obj : binaries) {
         Binary binary = (Binary) obj;
         byte[] binaryLengthBytes = JSONB.toBytes(binary.getLength());
         byte[] binaryDataBytes = binary.getData();
         binaryLengthBytesList.add(binaryLengthBytes);
         binaryDataBytesList.add(binaryDataBytes);
      }

      byte[] paramBytes = paramJsonStr.getBytes(StandardCharsets.UTF_8);
      byte[] paramLengthBytes = NumberUtil.toBytes(paramBytes.length);
      int binaryCount = binaries.size();
      byte[] binaryCountBytes = new byte[]{(byte)binaryCount};
      byte[] bodyBytes = ArrayUtil.addAll(new byte[][]{paramLengthBytes, paramBytes, binaryCountBytes});

      for(int i = 0; i < binaryCount; ++i) {
         bodyBytes = ArrayUtil.addAll(new byte[][]{bodyBytes, (byte[])binaryLengthBytesList.get(i), (byte[])binaryDataBytesList.get(i)});
      }

      return bodyBytes;
   }

   public static int getParamLength(byte[] bodyBytes) {
      int paramLength = NumberUtil.toInt(ArrayUtil.sub(bodyBytes, 0, 4));
      return paramLength;
   }

   public static String getParamStr(byte[] bodyBytes, int paramLength) {
      byte[] paramBytes = ArrayUtil.sub(bodyBytes, 4, 4 + paramLength);
      String paramStr = new String(paramBytes, StandardCharsets.UTF_8);
      return paramStr;
   }

   public static long getBinaryLengthStartIndex(int paramLength) {
      return (long)(4 + paramLength + 1);
   }

   public static byte[] getBinaryDataBytes(byte[] bodyBytes, long binaryLengthStartIndex) {
      long binaryLength = Convert.toLong(ArrayUtil.sub(bodyBytes, (int)binaryLengthStartIndex, (int)(binaryLengthStartIndex + 8L)));
      long binaryDataStartIndex = binaryLengthStartIndex + 8L;
      byte[] binaryDataBytes = ArrayUtil.sub(bodyBytes, (int)binaryDataStartIndex, (int)(binaryDataStartIndex + binaryLength));
      return binaryDataBytes;
   }

   public static long getNextBinaryLengthStartIndex(long binaryLengthStartIndex, byte[] binaryDataBytes) {
      return binaryLengthStartIndex + 8L + (long)binaryDataBytes.length;
   }
}
