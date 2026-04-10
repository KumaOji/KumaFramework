package com.kuma.boot.logger.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.util.CollectionUtils;

public class DesensitizationUtils {
   public static final Pattern REGEX_PATTERN = Pattern.compile("\\s*([\"]?[\\w]+[\"]?)(\\s*[:=]+[^\\u4e00-\\u9fa5@,.*{\\[\\w]*\\s*)([\\u4e00-\\u9fa5_\\-@.\\w]+)[\\W&&[^\\-@.]]?\\s*");
   public static final Pattern REGEX_NUM = Pattern.compile("[^0-9]");
   public static Boolean openFlag = false;
   public static Boolean ignoreFlag = true;
   private static Boolean initIgnoreFlag = false;
   private static Boolean initOpenFlag = false;
   public static Map<String, Object> allPattern;
   public static Map<String, Object> lowerCaseAllPattern;
   public static final String PHONE = "phone";
   public static final String EMAIL = "email";
   public static final String IDENTITY = "identity";
   public static final String OTHER = "other";
   public static final String PASSWORD = "password";

   public DesensitizationUtils() {
   }

   public String customChange(String eventFormattedMessage) {
      try {
         String originalMessage = eventFormattedMessage;
         boolean flag = false;
         Map<String, Object> patternMap = YmlUtils.patternMap;
         if (!CollectionUtils.isEmpty(patternMap)) {
            if (!this.checkOpen(patternMap)) {
               return "";
            }

            Matcher regexMatcher = REGEX_PATTERN.matcher(eventFormattedMessage);

            while(regexMatcher.find()) {
               try {
                  String key = regexMatcher.group(1).replaceAll("\"", "").trim();
                  String originalValue = regexMatcher.group(3);
                  Object keyPatternValue = this.getKeyIgnoreCase(key);
                  if (null != keyPatternValue && null != originalValue && !"null".equals(originalValue)) {
                     String value = originalValue.replaceAll("\"", "").trim();
                     if (!"null".equals(value) || value.equalsIgnoreCase(key)) {
                        String patternVales = this.getMultiplePattern(keyPatternValue, value);
                        if (!"".equals(patternVales)) {
                           patternVales = patternVales.replaceAll(" ", "");
                           if ("password".equalsIgnoreCase(patternVales)) {
                              String var10000 = regexMatcher.group(1);
                              String origin = var10000 + regexMatcher.group(2) + regexMatcher.group(3);
                              String var10002 = regexMatcher.group(1);
                              originalMessage = originalMessage.replace(origin, var10002 + regexMatcher.group(2) + "******");
                              flag = true;
                           } else {
                              String originalPatternValues = patternVales;
                              String filterData = this.getBracketPattern(patternVales);
                              if (!"".equals(filterData)) {
                                 patternVales = filterData;
                              }

                              String[] split = patternVales.split(",");
                              value = this.getReplaceValue(value, patternVales, split, originalPatternValues);
                              if (value != null && !"".equals(value)) {
                                 flag = true;
                                 String var20 = regexMatcher.group(1);
                                 String origin = var20 + regexMatcher.group(2) + regexMatcher.group(3);
                                 String var21 = regexMatcher.group(1);
                                 originalMessage = originalMessage.replace(origin, var21 + regexMatcher.group(2) + value);
                              }
                           }
                        }
                     }
                  }
               } catch (Exception var15) {
                  return "";
               }
            }
         }

         return flag ? originalMessage : "";
      } catch (Exception var16) {
         return "";
      }
   }

   private String getReplaceValue(String value, String patternVales, String[] split, String originalPatternValues) {
      if (split.length >= 2 && !"".equals(patternVales)) {
         String append = "";
         String start = REGEX_NUM.matcher(split[0]).replaceAll("");
         String end = REGEX_NUM.matcher(split[1]).replaceAll("");
         int startSub = Integer.parseInt(start) - 1;
         int endSub = Integer.parseInt(end) - 1;
         if (originalPatternValues.contains(">")) {
            int index = originalPatternValues.indexOf(">");
            String flagSub = originalPatternValues.substring(0, index);
            int indexOf = value.indexOf(flagSub);
            String newValue = value.substring(0, indexOf);
            int newValueL = newValue.length();
            append = value.substring(indexOf);
            String var10000 = this.dataDesensitization(Math.max(startSub, 0), endSub >= 0 ? (endSub <= newValueL ? endSub : newValueL - 1) : 0, newValue);
            value = var10000 + append;
         } else if (originalPatternValues.contains("<")) {
            int index = originalPatternValues.indexOf("<");
            String flagSub = originalPatternValues.substring(0, index);
            int indexOf = value.indexOf(flagSub);
            String newValue = value.substring(indexOf + 1);
            int newValueL = newValue.length();
            append = value.substring(0, indexOf + 1);
            value = append + this.dataDesensitization(Math.max(startSub, 0), endSub >= 0 ? (endSub <= newValueL ? endSub : newValueL - 1) : 0, newValue);
         } else if (originalPatternValues.contains(",")) {
            int newValueL = value.length();
            value = this.dataDesensitization(Math.max(startSub, 0), endSub >= 0 ? (endSub <= newValueL ? endSub : newValueL - 1) : 0, value);
         }
      } else if (!"".equals(patternVales)) {
         int beforeIndexOf = patternVales.indexOf("*");
         int last = patternVales.length() - patternVales.lastIndexOf("*");
         int lastIndexOf = value.length() - last;
         value = this.dataDesensitization(beforeIndexOf, lastIndexOf, value);
      }

      return value;
   }

   private Object getKeyIgnoreCase(String key) {
      if (CollectionUtils.isEmpty(allPattern)) {
         allPattern = YmlUtils.getAllPattern();
      }

      if (!initIgnoreFlag) {
         initIgnoreFlag = true;
         ignoreFlag = YmlUtils.getIgnore();
         if (ignoreFlag) {
            lowerCaseAllPattern = this.transformUpperCase(allPattern);
         }
      }

      return ignoreFlag ? lowerCaseAllPattern.get(key.toLowerCase()) : allPattern.get(key);
   }

   public Map<String, Object> transformUpperCase(Map<String, Object> pattern) {
      Map<String, Object> resultMap = new HashMap();
      if (pattern != null && !pattern.isEmpty()) {
         for(String key : pattern.keySet()) {
            String newKey = key.toLowerCase();
            resultMap.put(newKey, pattern.get(key));
         }
      }

      return resultMap;
   }

   private String getMultiplePattern(Object patternVale, String newValue) {
      if (patternVale instanceof String) {
         return (String)patternVale;
      } else if (patternVale instanceof Map) {
         return this.getPatternByMap((Map)patternVale, newValue);
      } else {
         if (patternVale instanceof List) {
            List<Map<String, Object>> list = (List)patternVale;
            if (!CollectionUtils.isEmpty(list)) {
               for(Map<String, Object> map : list) {
                  String patternValue = this.getPatternByMap(map, newValue);
                  if (!"".equals(patternValue)) {
                     return patternValue;
                  }
               }
            }
         }

         return "";
      }
   }

   private String getPatternByMap(Map<String, Object> map, String value) {
      if (CollectionUtils.isEmpty(map)) {
         return "";
      } else {
         Object customRegexObj = map.get("customRegex");
         Object positionObj = map.get("position");
         String customRegex = "";
         String position = "";
         if (customRegexObj instanceof String) {
            customRegex = (String)customRegexObj;
         }

         if (positionObj instanceof String) {
            position = (String)positionObj;
         }

         if (!"".equals(customRegex) && value.matches(customRegex)) {
            return position;
         } else {
            Object defaultRegexObj = map.get("defaultRegex");
            String defaultRegex = "";
            if (defaultRegexObj instanceof String) {
               defaultRegex = (String)defaultRegexObj;
            }

            if (!"".equals(defaultRegex)) {
               if ("identity".equals(defaultRegex) && isIdentity(value)) {
                  return position;
               }

               if ("email".equals(defaultRegex) && isEmail(value)) {
                  return position;
               }

               if ("phone".equals(defaultRegex) && isMobile(value)) {
                  return position;
               }

               if ("other".equals(defaultRegex)) {
                  return position;
               }
            }

            return "";
         }
      }
   }

   private String getBracketPattern(String patternVales) {
      if (patternVales.contains("(")) {
         int startCons = patternVales.indexOf("(");
         int endCons = patternVales.indexOf(")");
         patternVales = patternVales.substring(startCons + 1, endCons);
         return patternVales;
      } else {
         return "";
      }
   }

   public static boolean isEmail(String str) {
      return str.matches("^[\\w-]+@[\\w-]+(\\.[\\w-]+)+$");
   }

   public static boolean isIdentity(String str) {
      return str.matches("(^\\d{18}$)|(^\\d{15}$)");
   }

   public static boolean isMobile(String str) {
      return str.matches("^1[0-9]{10}$");
   }

   private Boolean checkOpen(Map<String, Object> pattern) {
      if (!initOpenFlag) {
         initOpenFlag = true;
         openFlag = YmlUtils.getOpen();
      }

      return openFlag;
   }

   public String dataDesensitization(int start, int end, String value) {
      if (start >= 0 && end + 1 <= value.length()) {
         char[] chars = value.toCharArray();

         for(int i = start; i < chars.length && i < end + 1; ++i) {
            chars[i] = '*';
         }

         return new String(chars);
      } else if (start >= 0 && end >= value.length()) {
         char[] chars = value.toCharArray();

         for(int i = start; i < chars.length; ++i) {
            chars[i] = '*';
         }

         return new String(chars);
      } else {
         return value;
      }
   }
}
