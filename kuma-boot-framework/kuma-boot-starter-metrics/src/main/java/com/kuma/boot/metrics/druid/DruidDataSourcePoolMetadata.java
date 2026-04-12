package com.kuma.boot.metrics.druid;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.jdbc.metadata.AbstractDataSourcePoolMetadata;

public class DruidDataSourcePoolMetadata extends AbstractDataSourcePoolMetadata<DruidDataSource> {
   public DruidDataSourcePoolMetadata(DruidDataSource dataSource) {
      super(dataSource);
   }

   public Integer getActive() {
      return ((DruidDataSource)this.getDataSource()).getActiveCount();
   }

   public Integer getMax() {
      return ((DruidDataSource)this.getDataSource()).getMaxActive();
   }

   public Integer getMin() {
      return ((DruidDataSource)this.getDataSource()).getMinIdle();
   }

   public String getValidationQuery() {
      return ((DruidDataSource)this.getDataSource()).getValidationQuery();
   }

   public Boolean getDefaultAutoCommit() {
      return ((DruidDataSource)this.getDataSource()).isDefaultAutoCommit();
   }
}
