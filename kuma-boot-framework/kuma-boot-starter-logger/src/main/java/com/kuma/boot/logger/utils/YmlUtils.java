package com.kuma.boot.logger.utils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.util.CollectionUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;

public class YmlUtils {
   public static String PROPERTY_NAME = "logback-desensitize.yml";
   public static final String PATTERN = "pattern";
   public static final String PATTERNS = "patterns";
   public static final String OPEN_FLAG = "open";
   public static final String IGNORE = "ignore";
   public static final String YML_HEAD_KEY = "log-desensitize";
   public static final String CUSTOM = "custom";
   public static Map<String, Object> patternMap;
   public static final DumperOptions OPTIONS = new DumperOptions();

   public YmlUtils() {
   }

   private static Map<String, Object> getYmlByName(String fileName) {
      if (CollectionUtils.isEmpty(patternMap)) {
         Object fromYml = null;

         try {
            fromYml = getFromYml(fileName, "log-desensitize");
            if (fromYml instanceof Map) {
               return (Map)fromYml;
            }
         } catch (Exception var3) {
            return null;
         }
      }

      return patternMap;
   }

   public static Object getFromYml(String fileName, String key) {
      Yaml yaml = new Yaml(OPTIONS);
      InputStream inputStream = YmlUtils.class.getClassLoader().getResourceAsStream(fileName);
      HashMap<String, Object> map = (HashMap)yaml.loadAs(inputStream, HashMap.class);
      return Objects.nonNull(map) && map.size() > 0 ? map.get(key) : map;
   }

   public static Map<String, Object> getPattern() {
      Object pattern = patternMap.get("pattern");
      return pattern instanceof Map ? (Map)pattern : null;
   }

   public static Map<String, Object> getAllPattern() {
      Map<String, Object> allPattern = new HashMap();
      Map<String, Object> pattern = getPattern();
      Map<String, Object> patterns = getPatterns();
      if (!CollectionUtils.isEmpty(patterns)) {
         allPattern.putAll(patterns);
      }

      if (!CollectionUtils.isEmpty(pattern)) {
         allPattern.putAll(pattern);
      }

      return allPattern;
   }

   public static Map<String, Object> getPatterns() {
      Map<String, Object> map = new HashMap();
      Object patterns = patternMap.get("patterns");
      if (patterns instanceof List<?> list) {
         if (!CollectionUtils.isEmpty(list)) {
            for (Object item : list) {
               if (item instanceof Map) {
                  assembleMap(map, (Map<String, Object>) item);
               }
            }

            return map;
         }
      }

      if (patterns instanceof Map) {
         assembleMap(map, (Map)patterns);
         return map;
      } else {
         return null;
      }
   }

   private static void assembleMap(Map<String, Object> map, Map<String, Object> patterns) {
      Object key = patterns.get("key");
      if (key instanceof String) {
         String keyWords = ((String)key).replace(" ", "");
         String[] keyArr = keyWords.split(",");

         for(String keyStr : keyArr) {
            map.put(keyStr, patterns.get("custom"));
         }
      }

   }

   public static Boolean getOpen() {
      Object flag = patternMap.get("open");
      return flag instanceof Boolean ? (Boolean)flag : false;
   }

   public static Boolean getIgnore() {
      Object flag = patternMap.get("ignore");
      return flag instanceof Boolean ? (Boolean)flag : true;
   }

   static {
      OPTIONS.setDefaultFlowStyle(FlowStyle.BLOCK);
      patternMap = getYmlByName(PROPERTY_NAME);
   }
}
