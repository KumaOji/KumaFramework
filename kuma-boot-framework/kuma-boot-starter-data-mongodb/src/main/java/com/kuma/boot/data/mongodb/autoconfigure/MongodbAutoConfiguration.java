/*
 * Copyright (c) 2020-2030, Shuigedeng (981376577@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.data.mongodb.autoconfigure;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.mongodb.converter.DBObjectToJsonNodeConverter;
import com.kuma.boot.data.mongodb.converter.JsonNodeToDocumentConverter;
import com.kuma.boot.data.mongodb.converter.LocalDateTimeToString;
import com.kuma.boot.data.mongodb.converter.LocalDateToString;
import com.kuma.boot.data.mongodb.converter.StringToLocalDate;
import com.kuma.boot.data.mongodb.converter.StringToLocalDateTime;
import com.kuma.boot.data.mongodb.helper.config.MongoStartedEventListener;
import com.kuma.boot.data.mongodb.helper.utils.ImportExportUtil;
import com.kuma.boot.data.mongodb.helper.utils.MongoHelper;
import com.kuma.boot.data.mongodb.autoconfigure.properties.MongodbProperties;
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

/**
 * es配置类
 *
 * @author shuigedeng
 * @version 2022.03
 * @since 2020/5/3 06:47
 */
@AutoConfiguration
@EnableConfigurationProperties({MongodbProperties.class})
@ConditionalOnProperty(prefix = MongodbProperties.PREFIX, name = "enabled", havingValue = "true")
public class MongodbAutoConfiguration implements InitializingBean {

   @Override
   public void afterPropertiesSet() throws Exception {
      LogUtils.started(MongodbAutoConfiguration.class, StarterNameConstants.DATA_MONGODB_STARTER);
   }

   @Primary
   @Bean
   public MongoCustomConversions customConversions() {
      List<Converter<?, ?>> converters = new ArrayList<>(2);
      converters.add(DBObjectToJsonNodeConverter.INSTANCE);
      converters.add(JsonNodeToDocumentConverter.INSTANCE);
      converters.add(new LocalDateTimeToString());
      converters.add(new LocalDateToString());
      converters.add(new StringToLocalDateTime());
      converters.add(new StringToLocalDate());
      return new MongoCustomConversions(converters);
   }

   // @Bean
   // @ConditionalOnBean(MongoTemplate.class)
   // public BaseMongoDAO baseMongoDAO(MongoTemplate mongoTemplate) {
   //	return new MongoDaoSupport(mongoTemplate);
   // }

   /**
    * MongodbHelperAutoConfiguration
    *
    * @author shuigedeng
    * @version 2026.03
    * @since 2025-12-19 09:30:45
    */
   @Configuration
   public static class MongodbHelperAutoConfiguration {

      @Autowired
      private MongoDatabaseFactory mongoDatabaseFactory;

      @Autowired
      private MongoMappingContext mongoMappingContext;

      @Bean
      public MappingMongoConverter mappingMongoConverter() {
         DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDatabaseFactory);
         MappingMongoConverter converter =
                 new MappingMongoConverter(dbRefResolver, mongoMappingContext);

         // 此处是去除插入数据库的 _class 字段
         converter.setTypeMapper(new DefaultMongoTypeMapper(null));
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
