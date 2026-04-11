package com.kuma.boot.data.mongodb.helper.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatUtils {
   public static Pattern regex = Pattern.compile("\\$\\{([^}]*)\\}");

   public FormatUtils() {
   }

   public static String bson(String json) {
      json = transString(json);
      String blank = "    ";
      String indent = "";
      StringBuilder sb = new StringBuilder();

      for(char c : json.toCharArray()) {
         switch (c) {
            case ',':
               sb.append(",\n").append(indent);
               break;
            case '[':
               indent = indent + blank;
               sb.append("[\n").append(indent);
               break;
            case ']':
               indent = indent.substring(0, indent.length() - blank.length());
               sb.append("\n").append(indent).append("]");
               break;
            case '{':
               indent = indent + blank;
               sb.append("{\n").append(indent);
               break;
            case '}':
               indent = indent.substring(0, indent.length() - blank.length());
               sb.append("\n").append(indent).append("}");
               break;
            default:
               sb.append(c);
         }
      }

      return sb.toString();
   }

   private static String transString(String str) {
      str = str.replace(", ", ",").replace("{\"$oid\":", "${");

      for(String tp : getContentInfo(str)) {
         str = str.replace("${" + tp + "}", "ObjectId(" + tp.trim() + ")");
      }

      return str;
   }

   private static List<String> getContentInfo(String content) {
      Matcher matcher = regex.matcher(content);
      List<String> list = new ArrayList();

      while(matcher.find()) {
         list.add(matcher.group(1));
      }

      return list;
   }
}
