package com.kuma.boot.seata.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.datasource.init.StandardDatabaseScript;
import com.kuma.boot.seata.database.SeataSqlScripter;
import javax.sql.DataSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass({DataSource.class, StandardDatabaseScript.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.seata",
   name = {"enabled"},
   havingValue = "true"
)
public class SeataDatabaseAutoConfiguration implements InitializingBean {
   public void afterPropertiesSet() throws Exception {
      LogUtils.started(SeataDatabaseAutoConfiguration.class, "kuma-boot-starter-seata", new String[0]);
   }

   @Bean
   @ConditionalOnBean({DataSource.class})
   public SeataSqlScripter seataSqlScripter() {
      return new SeataSqlScripter();
   }
}
