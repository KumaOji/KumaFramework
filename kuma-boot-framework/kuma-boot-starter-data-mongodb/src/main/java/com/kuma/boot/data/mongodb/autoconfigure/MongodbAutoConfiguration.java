package com.kuma.boot.data.mongodb.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.mongodb.autoconfigure.properties.MongodbProperties;
import com.kuma.boot.data.mongodb.converter.DBObjectToJsonNodeConverter;
import com.kuma.boot.data.mongodb.converter.JsonNodeToDocumentConverter;
import com.kuma.boot.data.mongodb.converter.LocalDateTimeToString;
import com.kuma.boot.data.mongodb.converter.LocalDateToString;
import com.kuma.boot.data.mongodb.converter.StringToLocalDate;
import com.kuma.boot.data.mongodb.converter.StringToLocalDateTime;
import com.kuma.boot.data.mongodb.helper.config.MongoStartedEventListener;
import com.kuma.boot.data.mongodb.helper.utils.ImportExportUtil;
import com.kuma.boot.data.mongodb.helper.utils.MongoHelper;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@AutoConfiguration
@EnableConfigurationProperties({MongodbProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.data.mongodb",
   name = {"enabled"},
   havingValue = "true"
)
public class MongodbAutoConfiguration implements InitializingBean {
   public MongodbAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(MongodbAutoConfiguration.class, "kuma-boot-starter-data-mongodb", new String[0]);
   }

   @Primary
   @Bean
   public MongoCustomConversions customConversions() {
      List<Converter<?, ?>> converters = new ArrayList(2);
      converters.add(DBObjectToJsonNodeConverter.INSTANCE);
      converters.add(JsonNodeToDocumentConverter.INSTANCE);
      converters.add(new LocalDateTimeToString());
      converters.add(new LocalDateToString());
      converters.add(new StringToLocalDateTime());
      converters.add(new StringToLocalDate());
      return new MongoCustomConversions(converters);
   }

   @Configuration
   public static class MongodbHelperAutoConfiguration {
      @Autowired
      private MongoDatabaseFactory mongoDatabaseFactory;
      @Autowired
      private MongoMappingContext mongoMappingContext;

      public MongodbHelperAutoConfiguration() {
      }

      @Bean
      public MappingMongoConverter mappingMongoConverter() {
         DbRefResolver dbRefResolver = new DefaultDbRefResolver(this.mongoDatabaseFactory);
         MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, this.mongoMappingContext);
         converter.setTypeMapper(new DefaultMongoTypeMapper((String)null));
         return converter;
      }

      @Bean
      public MongoHelper mongoHelper() {
         return new MongoHelper();
      }

      @Bean
      public ImportExportUtil importExportUtil() {
         return new ImportExportUtil();
      }

      @Bean
      public MongoStartedEventListener mongoStartedEventListener() {
         return new MongoStartedEventListener();
      }
   }
}
