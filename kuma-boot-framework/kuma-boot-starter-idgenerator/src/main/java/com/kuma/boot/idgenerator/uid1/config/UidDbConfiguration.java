package com.kuma.boot.idgenerator.uid1.config;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.idgenerator.uid1.worker.repository.DbWorkerNodeResposity;
import com.kuma.boot.idgenerator.uid1.worker.repository.WorkerNodeResposity;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Configuration
@Component
public class UidDbConfiguration {
   @Value("${spring.datasource.driver-class-name:}")
   private String driverClassName;
   @Value("${spring.datasource.url:}")
   private String url;
   @Value("${spring.datasource.username:}")
   private String username;
   @Value("${spring.datasource.password:}")
   private String password;

   public UidDbConfiguration() {
   }

   @Bean
   @Primary
   public WorkerNodeResposity workerNodeResposity(ObjectProvider<DataSource> dataSourcePvd) {
      DataSource dataSource = null;
      Object var4;
      if (StringUtils.isNotBlank(this.driverClassName) && StringUtils.isNotBlank(this.url) && StringUtils.isNotBlank(this.username) && StringUtils.isNotBlank(this.password)) {
         HikariDataSource hds = new HikariDataSource();
         hds.setDriverClassName(this.driverClassName);
         hds.setJdbcUrl(this.url);
         hds.setUsername(this.username);
         hds.setPassword(this.password);
         var4 = hds;
      } else {
         var4 = (DataSource)dataSourcePvd.getIfAvailable();
      }

      JdbcTemplate jdbcTemplate = new JdbcTemplate((DataSource)var4);
      return new DbWorkerNodeResposity(jdbcTemplate);
   }
}
