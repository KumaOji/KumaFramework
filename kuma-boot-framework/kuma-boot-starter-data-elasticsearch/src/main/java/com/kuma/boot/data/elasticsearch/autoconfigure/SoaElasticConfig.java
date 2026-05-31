package com.kuma.boot.data.elasticsearch.autoconfigure;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.elasticsearch.config.ElasticsearchConfigurationSupport;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchCustomConversions;

@AutoConfiguration
@ConditionalOnExpression("'${spring.elasticsearch.uris:}' != ''")
public class SoaElasticConfig extends ElasticsearchConfigurationSupport {
   public SoaElasticConfig() {
   }

   @Bean
   public ElasticsearchCustomConversions elasticsearchCustomConversions() {
      List<Converter<?, ?>> converters = new ArrayList<>();
      converters.add(SoaElasticConfig.LongToLocalDateTimeConverter.INSTANCE);
      converters.add(SoaElasticConfig.StringToLocalDateTimeConverter.INSTANCE);
      converters.add(SoaElasticConfig.DateToLocalDateTimeConverter.INSTANCE);
      converters.add(SoaElasticConfig.LongToLocalDateConverter.INSTANCE);
      converters.add(SoaElasticConfig.StringToLocalDateConverter.INSTANCE);
      converters.add(SoaElasticConfig.DateToLocalDateConverter.INSTANCE);
      converters.add(SoaElasticConfig.LongToZonedDateTimeConverter.INSTANCE);
      converters.add(SoaElasticConfig.StringToZonedDateTimeConverter.INSTANCE);
      converters.add(SoaElasticConfig.DateToZonedDateTimeConverter.INSTANCE);
      converters.add(SoaElasticConfig.LongToOffsetDateTimeConverter.INSTANCE);
      converters.add(SoaElasticConfig.StringToOffsetDateTimeConverter.INSTANCE);
      converters.add(SoaElasticConfig.DateToOffsetDateTimeConverter.INSTANCE);
      converters.add(SoaElasticConfig.LocalDateTimeToLongConverter.INSTANCE);
      converters.add(SoaElasticConfig.LocalDateTimeToStringConverter.INSTANCE);
      converters.add(SoaElasticConfig.LocalDateTimeToDateConverter.INSTANCE);
      converters.add(SoaElasticConfig.LocalDateToLongConverter.INSTANCE);
      converters.add(SoaElasticConfig.LocalDateToStringConverter.INSTANCE);
      converters.add(SoaElasticConfig.LocalDateToDateConverter.INSTANCE);
      converters.add(SoaElasticConfig.ZonedDateTimeToLongConverter.INSTANCE);
      converters.add(SoaElasticConfig.ZonedDateTimeToStringConverter.INSTANCE);
      converters.add(SoaElasticConfig.ZonedDateTimeToDateConverter.INSTANCE);
      converters.add(SoaElasticConfig.OffsetDateTimeToLongConverter.INSTANCE);
      converters.add(SoaElasticConfig.OffsetDateTimeToStringConverter.INSTANCE);
      converters.add(SoaElasticConfig.OffsetDateTimeToDateConverter.INSTANCE);
      return new ElasticsearchCustomConversions(converters);
   }

   @ReadingConverter
   static enum LongToLocalDateTimeConverter implements Converter<Long, LocalDateTime> {
      INSTANCE;

      private LongToLocalDateTimeConverter() {
      }

      public LocalDateTime convert(Long source) {
         return Instant.ofEpochMilli(source).atZone(ZoneId.systemDefault()).toLocalDateTime();
      }

      // $FF: synthetic method
      private static LongToLocalDateTimeConverter[] $values() {
         return new LongToLocalDateTimeConverter[]{INSTANCE};
      }
   }

   @WritingConverter
   static enum LocalDateTimeToLongConverter implements Converter<LocalDateTime, Long> {
      INSTANCE;

      private LocalDateTimeToLongConverter() {
      }

      public Long convert(LocalDateTime source) {
         return source.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
      }

      // $FF: synthetic method
      private static LocalDateTimeToLongConverter[] $values() {
         return new LocalDateTimeToLongConverter[]{INSTANCE};
      }
   }

   @ReadingConverter
   static enum StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {
      INSTANCE;

      private StringToLocalDateTimeConverter() {
      }

      public LocalDateTime convert(String source) {
         String t = source.indexOf(84) > 0 ? "'T'" : " ";
         String mill = source.indexOf(46) > 0 ? ".SSS" : "";
         StringBuilder sb = new StringBuilder(30);
         sb.append("yyyy-MM-dd").append(t).append("HH:mm:ss").append(mill);
         if (source.length() > 19 + mill.length()) {
            sb.append("z");
         }

         DateTimeFormatter df = DateTimeFormatter.ofPattern(sb.toString());
         return LocalDateTime.parse(source, df);
      }

      // $FF: synthetic method
      private static StringToLocalDateTimeConverter[] $values() {
         return new StringToLocalDateTimeConverter[]{INSTANCE};
      }
   }

   @WritingConverter
   static enum LocalDateTimeToStringConverter implements Converter<LocalDateTime, String> {
      INSTANCE;

      private LocalDateTimeToStringConverter() {
      }

      public String convert(LocalDateTime source) {
         return source.atZone(ZoneId.systemDefault()).toString();
      }

      // $FF: synthetic method
      private static LocalDateTimeToStringConverter[] $values() {
         return new LocalDateTimeToStringConverter[]{INSTANCE};
      }
   }

   @WritingConverter
   static enum DateToLocalDateTimeConverter implements Converter<Date, LocalDateTime> {
      INSTANCE;

      private DateToLocalDateTimeConverter() {
      }

      public LocalDateTime convert(Date date) {
         return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
      }

      // $FF: synthetic method
      private static DateToLocalDateTimeConverter[] $values() {
         return new DateToLocalDateTimeConverter[]{INSTANCE};
      }
   }

   @WritingConverter
   static enum LocalDateTimeToDateConverter implements Converter<LocalDateTime, Date> {
      INSTANCE;

      private LocalDateTimeToDateConverter() {
      }

      public Date convert(LocalDateTime source) {
         return new Date(source.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
      }

      // $FF: synthetic method
      private static LocalDateTimeToDateConverter[] $values() {
         return new LocalDateTimeToDateConverter[]{INSTANCE};
      }
   }

   @WritingConverter
   static enum LocalDateToLongConverter implements Converter<LocalDate, Long> {
      INSTANCE;

      private LocalDateToLongConverter() {
      }

      public Long convert(LocalDate source) {
         return source.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
      }

      // $FF: synthetic method
      private static LocalDateToLongConverter[] $values() {
         return new LocalDateToLongConverter[]{INSTANCE};
      }
   }

   @ReadingConverter
   static enum LongToLocalDateConverter implements Converter<Long, LocalDate> {
      INSTANCE;

      private LongToLocalDateConverter() {
      }

      public LocalDate convert(Long source) {
         return Instant.ofEpochMilli(source).atZone(ZoneId.systemDefault()).toLocalDate();
      }

      // $FF: synthetic method
      private static LongToLocalDateConverter[] $values() {
         return new LongToLocalDateConverter[]{INSTANCE};
      }
   }

   @ReadingConverter
   static enum StringToLocalDateConverter implements Converter<String, LocalDate> {
      INSTANCE;

      private StringToLocalDateConverter() {
      }

      public LocalDate convert(String source) {
         int length = source.length();
         if (length <= 10) {
            return LocalDate.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
         } else {
            String t = source.indexOf(84) > 0 ? "'T'" : " ";
            String mill = source.indexOf(46) > 0 ? ".SSS" : "";
            StringBuilder sb = new StringBuilder(30);
            sb.append("yyyy-MM-dd").append(t).append("HH:mm:ss").append(mill);
            if (length > 19 + mill.length()) {
               sb.append("z");
            }

            DateTimeFormatter df = DateTimeFormatter.ofPattern(sb.toString());
            return LocalDate.parse(source, df);
         }
      }

      // $FF: synthetic method
      private static StringToLocalDateConverter[] $values() {
         return new StringToLocalDateConverter[]{INSTANCE};
      }
   }

   @WritingConverter
   static enum LocalDateToStringConverter implements Converter<LocalDate, String> {
      INSTANCE;

      private LocalDateToStringConverter() {
      }

      public String convert(LocalDate source) {
         return source.toString();
      }

      // $FF: synthetic method
      private static LocalDateToStringConverter[] $values() {
         return new LocalDateToStringConverter[]{INSTANCE};
      }
   }

   @WritingConverter
   static enum DateToLocalDateConverter implements Converter<Date, LocalDate> {
      INSTANCE;

      private DateToLocalDateConverter() {
      }

      public LocalDate convert(Date date) {
         return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
      }

      // $FF: synthetic method
      private static DateToLocalDateConverter[] $values() {
         return new DateToLocalDateConverter[]{INSTANCE};
      }
   }

   @WritingConverter
   static enum LocalDateToDateConverter implements Converter<LocalDate, Date> {
      INSTANCE;

      private LocalDateToDateConverter() {
      }

      public Date convert(LocalDate source) {
         return new Date(source.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
      }

      // $FF: synthetic method
      private static LocalDateToDateConverter[] $values() {
         return new LocalDateToDateConverter[]{INSTANCE};
      }
   }

   @ReadingConverter
   static enum LongToZonedDateTimeConverter implements Converter<Long, ZonedDateTime> {
      INSTANCE;

      private LongToZonedDateTimeConverter() {
      }

      public ZonedDateTime convert(Long source) {
         return Instant.ofEpochMilli(source).atZone(ZoneId.systemDefault());
      }

      // $FF: synthetic method
      private static LongToZonedDateTimeConverter[] $values() {
         return new LongToZonedDateTimeConverter[]{INSTANCE};
      }
   }

   @WritingConverter
   static enum ZonedDateTimeToLongConverter implements Converter<ZonedDateTime, Long> {
      INSTANCE;

      private ZonedDateTimeToLongConverter() {
      }

      public Long convert(ZonedDateTime source) {
         return source.toInstant().toEpochMilli();
      }

      // $FF: synthetic method
      private static ZonedDateTimeToLongConverter[] $values() {
         return new ZonedDateTimeToLongConverter[]{INSTANCE};
      }
   }

   @ReadingConverter
   static enum StringToZonedDateTimeConverter implements Converter<String, ZonedDateTime> {
      INSTANCE;

      private StringToZonedDateTimeConverter() {
      }

      public ZonedDateTime convert(String source) {
         String t = source.indexOf(84) > 0 ? "'T'" : " ";
         String mill = source.indexOf(46) > 0 ? ".SSS" : "";
         StringBuilder sb = new StringBuilder(30);
         sb.append("yyyy-MM-dd").append(t).append("HH:mm:ss").append(mill);
         if (source.length() > 19 + mill.length()) {
            sb.append("z");
         }

         DateTimeFormatter df = DateTimeFormatter.ofPattern(sb.toString());
         return ZonedDateTime.parse(source, df);
      }

      // $FF: synthetic method
      private static StringToZonedDateTimeConverter[] $values() {
         return new StringToZonedDateTimeConverter[]{INSTANCE};
      }
   }

   @WritingConverter
   static enum ZonedDateTimeToStringConverter implements Converter<ZonedDateTime, String> {
      INSTANCE;

      private ZonedDateTimeToStringConverter() {
      }

      public String convert(ZonedDateTime source) {
         return source.toString();
      }

      // $FF: synthetic method
      private static ZonedDateTimeToStringConverter[] $values() {
         return new ZonedDateTimeToStringConverter[]{INSTANCE};
      }
   }

   @ReadingConverter
   static enum DateToZonedDateTimeConverter implements Converter<Date, ZonedDateTime> {
      INSTANCE;

      private DateToZonedDateTimeConverter() {
      }

      public ZonedDateTime convert(Date date) {
         return date.toInstant().atZone(ZoneId.systemDefault());
      }

      // $FF: synthetic method
      private static DateToZonedDateTimeConverter[] $values() {
         return new DateToZonedDateTimeConverter[]{INSTANCE};
      }
   }

   @WritingConverter
   static enum ZonedDateTimeToDateConverter implements Converter<ZonedDateTime, Date> {
      INSTANCE;

      private ZonedDateTimeToDateConverter() {
      }

      public Date convert(ZonedDateTime source) {
         return new Date(source.toInstant().toEpochMilli());
      }

      // $FF: synthetic method
      private static ZonedDateTimeToDateConverter[] $values() {
         return new ZonedDateTimeToDateConverter[]{INSTANCE};
      }
   }

   @ReadingConverter
   static enum LongToOffsetDateTimeConverter implements Converter<Long, OffsetDateTime> {
      INSTANCE;

      private LongToOffsetDateTimeConverter() {
      }

      public OffsetDateTime convert(Long source) {
         return OffsetDateTime.ofInstant(Instant.ofEpochMilli(source), ZoneId.systemDefault());
      }

      // $FF: synthetic method
      private static LongToOffsetDateTimeConverter[] $values() {
         return new LongToOffsetDateTimeConverter[]{INSTANCE};
      }
   }

   @WritingConverter
   static enum OffsetDateTimeToLongConverter implements Converter<OffsetDateTime, Long> {
      INSTANCE;

      private OffsetDateTimeToLongConverter() {
      }

      public Long convert(OffsetDateTime source) {
         return source.toInstant().toEpochMilli();
      }

      // $FF: synthetic method
      private static OffsetDateTimeToLongConverter[] $values() {
         return new OffsetDateTimeToLongConverter[]{INSTANCE};
      }
   }

   @ReadingConverter
   static enum StringToOffsetDateTimeConverter implements Converter<String, OffsetDateTime> {
      INSTANCE;

      private StringToOffsetDateTimeConverter() {
      }

      public OffsetDateTime convert(String source) {
         String t = source.indexOf(84) > 0 ? "'T'" : " ";
         String mill = source.indexOf(46) > 0 ? ".SSS" : "";
         StringBuilder sb = new StringBuilder(30);
         sb.append("yyyy-MM-dd").append(t).append("HH:mm:ss").append(mill);
         if (source.length() > 19 + mill.length()) {
            sb.append("z");
         }

         DateTimeFormatter df = DateTimeFormatter.ofPattern(sb.toString());
         return OffsetDateTime.parse(source, df);
      }

      // $FF: synthetic method
      private static StringToOffsetDateTimeConverter[] $values() {
         return new StringToOffsetDateTimeConverter[]{INSTANCE};
      }
   }

   @WritingConverter
   static enum OffsetDateTimeToStringConverter implements Converter<OffsetDateTime, String> {
      INSTANCE;

      private OffsetDateTimeToStringConverter() {
      }

      public String convert(OffsetDateTime source) {
         return source.toString();
      }

      // $FF: synthetic method
      private static OffsetDateTimeToStringConverter[] $values() {
         return new OffsetDateTimeToStringConverter[]{INSTANCE};
      }
   }

   @ReadingConverter
   static enum DateToOffsetDateTimeConverter implements Converter<Date, OffsetDateTime> {
      INSTANCE;

      private DateToOffsetDateTimeConverter() {
      }

      public OffsetDateTime convert(Date date) {
         return OffsetDateTime.ofInstant(date.toInstant(), ZoneOffset.systemDefault());
      }

      // $FF: synthetic method
      private static DateToOffsetDateTimeConverter[] $values() {
         return new DateToOffsetDateTimeConverter[]{INSTANCE};
      }
   }

   @WritingConverter
   static enum OffsetDateTimeToDateConverter implements Converter<OffsetDateTime, Date> {
      INSTANCE;

      private OffsetDateTimeToDateConverter() {
      }

      public Date convert(OffsetDateTime source) {
         return new Date(source.toInstant().toEpochMilli());
      }

      // $FF: synthetic method
      private static OffsetDateTimeToDateConverter[] $values() {
         return new OffsetDateTimeToDateConverter[]{INSTANCE};
      }
   }
}
