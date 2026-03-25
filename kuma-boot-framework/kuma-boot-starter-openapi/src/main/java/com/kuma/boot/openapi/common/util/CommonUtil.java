package com.kuma.boot.openapi.common.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.openapi.common.exception.OpenApiException;
import com.kuma.boot.openapi.common.model.InParams;

public class CommonUtil {
   public static byte[] getSignContent(InParams inParams) {
      byte[] bodyBytes = inParams.getBodyBytes();
      return ArrayUtil.addAll(new byte[][]{bodyBytes, inParams.getUuid().getBytes()});
   }

   public static String completeUrl(String baseUrl, String path) {
      if (StrUtil.isBlank(baseUrl)) {
         throw new OpenApiException("URL基础路径不能为空");
      } else {
         String separator = "/";
         String formattedUrl = baseUrl;
         if (baseUrl.endsWith(separator)) {
            formattedUrl = baseUrl.substring(0, baseUrl.length() - 1);
         }

         if (StrUtil.isNotBlank(path)) {
            path = path.startsWith(separator) ? path.substring(1) : path;
            return formattedUrl + separator + path;
         } else {
            return baseUrl;
         }
      }
   }

   public static Object cloneInstance(Object obj) {
      try {
         Object tmp = obj.getClass().getDeclaredConstructor().newInstance();
         BeanUtil.copyProperties(obj, tmp, new String[0]);
         return tmp;
      } catch (Exception ex) {
         LogUtils.error("克隆新实例失败", new Object[]{ex});
         return null;
      }
   }
}
