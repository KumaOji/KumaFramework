package com.kuma.boot.data.mongodb.autoconfigure;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

@AutoConfiguration
@ConditionalOnExpression("'${spring.data.mongodb.uri:}' != '' || '${spring.data.mongodb.host:}' != ''")
public class SoaMongoConfig {
   public static final String zonedDateTimeProviderBeanName = "zonedDateTimeProvider";
   public static final String offsetDateTimeProviderBeanName = "offsetDateTimeProvider";

   public SoaMongoConfig() {
   }

   @Bean
   @ConditionalOnMissingBean({MongoTransactionManager.class})
   MongoTransactionManager transactionManager(MongoDatabaseFactory factory) {
      return new MongoTransactionManager(factory);
   }

   @Bean
   public MongoCustomConversions customConversions() {
      List<Converter<?, ?>> converters = new ArrayList();
      converters.add(SoaMongoConfig.DateToZonedDateTimeConverter.INSTANCE);
      converters.add(SoaMongoConfig.ZonedDateTimeToDateConverter.INSTANCE);
      converters.add(SoaMongoConfig.DateToOffsetDateTimeConverter.INSTANCE);
      converters.add(SoaMongoConfig.OffsetDateTimeToDateConverter.INSTANCE);
      return new MongoCustomConversions(converters);
   }

   @ReadingConverter
   static enum DateToZonedDateTimeConverter implements Converter<Date, ZonedDateTime> {
      INSTANCE;

      private DateToZonedDateTimeConverter() {
      }

      public ZonedDateTime convert(Date source) {
         return source == null ? null : ZonedDateTime.ofInstant(source.toInstant(), ZoneId.systemDefault());
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
         return source == null ? null : Date.from(source.toInstant());
      }

      // $FF: synthetic method
      private static ZonedDateTimeToDateConverter[] $values() {
         return new ZonedDateTimeToDateConverter[]{INSTANCE};
      }
   }

   @ReadingConverter
   static enum DateToOffsetDateTimeConverter implements Converter<Date, OffsetDateTime> {
      INSTANCE;

      private DateToOffsetDateTimeConverter() {
      }

      public OffsetDateTime convert(Date source) {
         return source == null ? null : OffsetDateTime.ofInstant(source.toInstant(), ZoneId.systemDefault());
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
         return source == null ? null : Date.from(source.toInstant());
      }

      // $FF: synthetic method
      private static OffsetDateTimeToDateConverter[] $values() {
         return new OffsetDateTimeToDateConverter[]{INSTANCE};
      }
   }
}
