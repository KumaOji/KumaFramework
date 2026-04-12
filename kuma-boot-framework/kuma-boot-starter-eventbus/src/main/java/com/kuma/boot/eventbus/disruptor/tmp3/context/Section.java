package com.kuma.boot.eventbus.disruptor.tmp3.context;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Section implements Map<String, String> {
   private final String name;
   private final Map<String, String> props;

   Section(String name) {
      if (name == null) {
         throw new NullPointerException("name");
      } else {
         this.name = name;
         this.props = new LinkedHashMap<>();
      }
   }

   Section(String name, String sectionContent) {
      if (name == null) {
         throw new NullPointerException("name");
      } else {
         this.name = name;
         Map<String, String> props;
         if (!StringUtils.isEmpty(sectionContent)) {
            props = toMapProps(sectionContent);
         } else {
            props = new LinkedHashMap<>();
         }

         if (props != null) {
            this.props = props;
         } else {
            this.props = new LinkedHashMap<>();
         }

      }
   }

   Section(Section defaults) {
      this(defaults.getName());
      this.putAll(defaults.props);
   }

   protected static boolean isContinued(String line) {
      if (!StringUtils.isEmpty(line)) {
         return false;
      } else {
         int length = line.length();
         int backslashCount = 0;

         for(int i = length - 1; i > 0 && line.charAt(i) == '\\'; --i) {
            ++backslashCount;
         }

         return backslashCount % 2 != 0;
      }
   }

   private static boolean isKeyValueSeparatorChar(char c) {
      return Character.isWhitespace(c) || c == ':' || c == '=';
   }

   private static boolean isCharEscaped(CharSequence s, int index) {
      return index > 0 && s.charAt(index - 1) == '\\';
   }

   protected static String[] splitKeyValue(String keyValueLine) {
      String line = StringUtils.trimToNull(keyValueLine);
      if (line == null) {
         return null;
      } else {
         StringBuilder keyBuffer = new StringBuilder();
         StringBuilder valueBuffer = new StringBuilder();
         boolean buildingKey = true;

         for(int i = 0; i < line.length(); ++i) {
            char c = line.charAt(i);
            if (buildingKey) {
               if (isKeyValueSeparatorChar(c) && !isCharEscaped(line, i)) {
                  buildingKey = false;
               } else {
                  keyBuffer.append(c);
               }
            } else if (valueBuffer.length() != 0 || !isKeyValueSeparatorChar(c) || isCharEscaped(line, i)) {
               valueBuffer.append(c);
            }
         }

         String key = StringUtils.trimToNull(keyBuffer.toString());
         String value = StringUtils.trimToNull(valueBuffer.toString());
         if (key != null && value != null) {
            LogUtils.info("Discovered key/value pair: {}={}", new Object[]{key, value});
            return new String[]{key, value};
         } else {
            String msg = "Line argument must contain a key and a value.  Only one string token was found.";
            throw new IllegalArgumentException(msg);
         }
      }
   }

   private static Map<String, String> toMapProps(String content) {
      Map<String, String> props = new LinkedHashMap<>();
      StringBuilder lineBuffer = new StringBuilder();
      Scanner scanner = new Scanner(content);

      while(scanner.hasNextLine()) {
         String line = StringUtils.trimToNull(scanner.nextLine());
         if (isContinued(line)) {
            line = line.substring(0, line.length() - 1);
            lineBuffer.append(line);
         } else {
            lineBuffer.append(line);
            line = lineBuffer.toString();
            lineBuffer = new StringBuilder();
            String[] kvPair = splitKeyValue(line);
            props.put(kvPair[0], kvPair[1]);
         }
      }

      return props;
   }

   public String getName() {
      return this.name;
   }

   public void clear() {
      this.props.clear();
   }

   public boolean containsKey(Object key) {
      return this.props.containsKey(key);
   }

   public boolean containsValue(Object value) {
      return this.props.containsValue(value);
   }

   public Set<Map.Entry<String, String>> entrySet() {
      return this.props.entrySet();
   }

   public String get(Object key) {
      return this.props.get(key);
   }

   public boolean isEmpty() {
      return this.props.isEmpty();
   }

   public Set<String> keySet() {
      return this.props.keySet();
   }

   public String put(String key, String value) {
      return this.props.put(key, value);
   }

   public void putAll(Map<? extends String, ? extends String> m) {
      this.props.putAll(m);
   }

   public String remove(Object key) {
      return this.props.remove(key);
   }

   public int size() {
      return this.props.size();
   }

   public Collection<String> values() {
      return this.props.values();
   }

   public String toString() {
      String name = this.getName();
      return "".equals(name) ? "<default>" : name;
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof Section other)) {
         return false;
      } else {
         return this.getName().equals(other.getName()) && this.props.equals(other.props);
      }
   }

   public int hashCode() {
      return this.name.hashCode() * 31 + this.props.hashCode();
   }
}
