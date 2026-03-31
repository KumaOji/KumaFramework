package com.kuma.boot.monitor.alarm.core.loader.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class PropertiesUtil {
   public PropertiesUtil() {
   }

   public static Properties read(String fileName) throws IOException {
      InputStream inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName);

      Properties var3;
      try {
         Properties pro = new Properties();
         pro.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
         var3 = pro;
      } catch (Throwable var5) {
         if (inputStream != null) {
            try {
               inputStream.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (inputStream != null) {
         inputStream.close();
      }

      return var3;
   }

   public static void copy(Properties source, Object dest) throws IllegalAccessException {
      Field[] fields = dest.getClass().getDeclaredFields();

      for(Field f : fields) {
         if (!Modifier.isStatic(f.getModifiers())) {
            f.setAccessible(true);
            f.set(dest, parseObj(source.getProperty(f.getName()), f.getType()));
         }
      }

   }

   private static <T> T parseObj(String obj, Class<T> clz) {
      return (T)PropertiesUtil.ParseFuncEnum.getFunc(clz).apply(obj);
   }

   public static enum ParseFuncEnum {
      INT_PARSE(List.of(Integer.TYPE, Integer.class)) {
         public Function<String, Integer> getFunc() {
            return Integer::valueOf;
         }
      },
      LONG_PARSE(List.of(Long.TYPE, Long.class)) {
         public Function<String, Long> getFunc() {
            return Long::valueOf;
         }
      },
      BOOLEAN_PARSE(List.of(Boolean.TYPE, Boolean.class)) {
         public Function<String, Boolean> getFunc() {
            return Boolean::valueOf;
         }
      },
      FLOAT_PARSE(List.of(Float.TYPE, Float.class)) {
         public Function<String, Float> getFunc() {
            return Float::valueOf;
         }
      },
      DOUBLE_PARSE(List.of(Double.TYPE, Double.class)) {
         public Function<String, Double> getFunc() {
            return Double::valueOf;
         }
      },
      SHORT_PARSE(List.of(Short.TYPE, Short.class)) {
         public Function<String, Short> getFunc() {
            return Short::valueOf;
         }
      },
      BYTE_PARSE(List.of(Byte.TYPE, Byte.class)) {
         public Function<String, Byte> getFunc() {
            return Byte::valueOf;
         }
      },
      CHAR_PARSE(List.of(Character.TYPE, Character.class)) {
         public Function<String, Character> getFunc() {
            return (s) -> s.charAt(0);
         }
      },
      STRING_PARSE(List.of(String.class)) {
         public Function<String, String> getFunc() {
            return (s) -> s;
         }
      };

      private final List<Class<?>> clzList;
      private static Map<Class<?>, ParseFuncEnum> map = new ConcurrentHashMap(20);

      public abstract <T> Function<String, T> getFunc();

      private ParseFuncEnum(List<Class<?>> clz) {
         this.clzList = clz;
      }

      public static <T> Function<String, T> getFunc(Class<T> clz) {
         return ((ParseFuncEnum)map.get(clz)).getFunc();
      }

      // $FF: synthetic method
      private static ParseFuncEnum[] $values() {
         return new ParseFuncEnum[]{INT_PARSE, LONG_PARSE, BOOLEAN_PARSE, FLOAT_PARSE, DOUBLE_PARSE, SHORT_PARSE, BYTE_PARSE, CHAR_PARSE, STRING_PARSE};
      }

      static {
         for(ParseFuncEnum enu : values()) {
            for(Class<?> clz : enu.clzList) {
               map.put(clz, enu);
            }
         }

      }
   }
}
