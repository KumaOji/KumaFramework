package com.kuma.boot.idgenerator.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.datasource.init.StandardDatabaseScript;
import com.kuma.boot.idgenerator.database.IdGeneratorSqlScripter;
import javax.sql.DataSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(
   after = {IdGeneratorAutoConfiguration.class}
)
@ConditionalOnClass({DataSource.class, StandardDatabaseScript.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.idgenerator",
   name = {"enabled"},
   havingValue = "true",
   matchIfMissing = true
)
public class IdGeneratorDatabaseAutoConfiguration implements InitializingBean {
   public IdGeneratorDatabaseAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(IdGeneratorDatabaseAutoConfiguration.class, "kuma-boot-starter-idgenerator", new String[0]);
   }

   @Bean
   @ConditionalOnBean({DataSource.class})
   public IdGeneratorSqlScripter idGeneratorSqlScripter() {
      return new IdGeneratorSqlScripter();
   }
}
