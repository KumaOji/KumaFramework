package com.kuma.boot.oss.common.util;

import cn.hutool.core.util.ObjUtil;
import com.kuma.boot.common.utils.date.DateUtils;
import com.kuma.boot.common.utils.id.IdGeneratorUtils;
import java.io.File;
import java.time.LocalDateTime;
import org.springframework.web.multipart.MultipartFile;

public class OssPathUtil {
   public static String valid(String basePath) {
      if (ObjUtil.isEmpty(basePath)) {
         basePath = "/";
      }

      basePath = basePath.replaceAll("\\\\", "/").replaceAll("//", "/");
      if (!basePath.startsWith("/")) {
         basePath = "/" + basePath;
      }

      if (!basePath.endsWith("/")) {
         basePath = basePath + "/";
      }

      return basePath;
   }

   public static String convertPath(String key, Boolean isAbsolute) {
      key = key.replaceAll("\\\\", "/").replaceAll("//", "/");
      if (isAbsolute && !key.startsWith("/")) {
         key = "/" + key;
      } else if (!isAbsolute && key.startsWith("/")) {
         key = key.replaceFirst("/", "");
      }

      return key;
   }

   public static String replaceKey(String path, String basePath, Boolean isAbsolute) {
      String newPath;
      if ("/".equals(basePath)) {
         newPath = convertPath(path, isAbsolute);
      } else {
         newPath = convertPath(path, isAbsolute).replaceAll(convertPath(basePath, isAbsolute), "");
      }

      return convertPath(newPath, isAbsolute);
   }

   public static String getTargetName(File file) {
      String date = DateUtils.format(LocalDateTime.now(), "yyyy/MM/dd");
      return date + "/" + IdGeneratorUtils.getId() + "/" + file.getName();
   }

   public static String getTargetName(MultipartFile file) {
      String date = DateUtils.format(LocalDateTime.now(), "yyyy/MM/dd");
      return date + "/" + IdGeneratorUtils.getId() + "/" + file.getName();
   }
}
