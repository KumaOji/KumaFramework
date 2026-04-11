package com.kuma.boot.idgenerator.jdbc;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.support.incrementer.MySQLMaxValueIncrementer;

@Configuration
public class MySQLMaxValueIncrementerConfiguration {
   public MySQLMaxValueIncrementerConfiguration() {
   }

   @Bean
   public MySQLMaxValueIncrementer mySQLMaxValueIncrementer(DataSource dataSource) {
      MySQLMaxValueIncrementer mySQLMaxValueIncrementer = new MySQLMaxValueIncrementer();
      mySQLMaxValueIncrementer.setDataSource(dataSource);
      mySQLMaxValueIncrementer.setIncrementerName("T_SEQUENCE");
      mySQLMaxValueIncrementer.setColumnName("val");
      mySQLMaxValueIncrementer.setPaddingLength(10);
      mySQLMaxValueIncrementer.setUseNewConnection(false);
      mySQLMaxValueIncrementer.setCacheSize(1);
      return mySQLMaxValueIncrementer;
   }
}
