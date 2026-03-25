package com.kuma.boot.oss.common.util;

public class MimeTypeUtil {
   public static final String IMAGE_PNG = "image/png";
   public static final String IMAGE_JPG = "image/jpg";
   public static final String IMAGE_JPEG = "image/jpeg";
   public static final String IMAGE_BMP = "image/bmp";
   public static final String IMAGE_GIF = "image/gif";
   public static final String[] IMAGE_EXTENSION = new String[]{"bmp", "gif", "jpg", "jpeg", "png"};
   public static final String[] FLASH_EXTENSION = new String[]{"swf", "flv"};
   public static final String[] MEDIA_EXTENSION = new String[]{"swf", "flv", "mp3", "wav", "wma", "wmv", "mid", "avi", "mpg", "asf", "rm", "rmvb"};
   public static final String[] DEFAULT_ALLOWED_EXTENSION = new String[]{"bmp", "gif", "jpg", "jpeg", "png", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "html", "htm", "txt", "rar", "zip", "gz", "bz2", "pdf"};

   public static String getExtension(String prefix) {
      String var10000;
      switch (prefix) {
         case "image/png" -> var10000 = "png";
         case "image/jpg" -> var10000 = "jpg";
         case "image/jpeg" -> var10000 = "jpeg";
         case "image/bmp" -> var10000 = "bmp";
         case "image/gif" -> var10000 = "gif";
         default -> var10000 = "";
      }

      return var10000;
   }
}
