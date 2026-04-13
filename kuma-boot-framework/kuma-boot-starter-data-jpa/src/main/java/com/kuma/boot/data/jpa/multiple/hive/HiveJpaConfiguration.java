package com.kuma.boot.data.jpa.multiple.hive;

import com.kuma.boot.data.datasource.multiple.hive.HiveDataSourceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@AutoConfiguration(
   after = {HiveDataSourceConfiguration.class}
)
@ConditionalOnProperty(
   name = {"kuma.boot.data.datasource.multiple.clickhouse.enabled"},
   havingValue = "true"
)
public class HiveJpaConfiguration {
   private static Logger logger = LoggerFactory.getLogger(HiveJpaConfiguration.class);

   public HiveJpaConfiguration() {
   }
}
