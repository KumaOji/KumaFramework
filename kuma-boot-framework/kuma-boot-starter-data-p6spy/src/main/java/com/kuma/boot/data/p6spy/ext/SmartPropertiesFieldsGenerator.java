package com.kuma.boot.data.p6spy.ext;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmartPropertiesFieldsGenerator {
   static final Pattern FIELD_PATTERN = Pattern.compile("^#(\\w+)=.*");

   public SmartPropertiesFieldsGenerator() {
   }

   public static void main(String[] args) throws IOException {
      BufferedReader bufferedReader = new BufferedReader(new FileReader("src/main/resources/spy.properties"));

      try {
         Set<String> fieldsSet = new HashSet(32, 1.0F);

         String line;
         while((line = bufferedReader.readLine()) != null) {
            Matcher matcher = FIELD_PATTERN.matcher(line);
            if (matcher.find()) {
               String field = matcher.group(1);
               field = String.format("private String %s;", field);
               fieldsSet.add(field);
            }
         }

         PrintStream var10001 = System.out;
         Objects.requireNonNull(var10001);
         fieldsSet.forEach(var10001::println);
      } catch (Throwable var7) {
         try {
            bufferedReader.close();
         } catch (Throwable var6) {
            var7.addSuppressed(var6);
         }

         throw var7;
      }

      bufferedReader.close();
   }
}
