package com.kuma.boot.data.jpa.multiple.trino;

import com.kuma.boot.data.datasource.multiple.trino.TrinoDataSourceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@AutoConfiguration(
   after = {TrinoDataSourceConfiguration.class}
)
@ConditionalOnProperty(
   name = {"kuma.boot.data.datasource.multiple.trino.enabled"},
   havingValue = "true"
)
public class TrinoJpaConfiguration {
   private static Logger logger = LoggerFactory.getLogger(TrinoJpaConfiguration.class);

   public TrinoJpaConfiguration() {
   }
}
