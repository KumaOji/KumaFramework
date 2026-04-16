package com.kuma.boot.data.shardingsphere.algorithm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "kuma.boot.data.shardingsphere", name = "enabled", havingValue = "true")
public class StaticValue {
   public static String userBaseTableMinDate;
   public static String userBaseTableMaxDate;

   public StaticValue() {
   }

   @Value("${sharding.table.user.base.date.min}")
   public void setUserBaseTableMinDate(String userBaseTableMinDate) {
      StaticValue.userBaseTableMinDate = userBaseTableMinDate;
   }

   @Value("${sharding.table.user.base.date.max}")
   public void setUserBaseTableMaxDate(String userBaseTableMaxDate) {
      StaticValue.userBaseTableMaxDate = userBaseTableMaxDate;
   }
}
