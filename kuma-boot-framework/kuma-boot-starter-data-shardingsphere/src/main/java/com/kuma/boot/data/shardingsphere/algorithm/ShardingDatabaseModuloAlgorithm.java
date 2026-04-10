package com.kuma.boot.data.shardingsphere.algorithm;

import java.util.Collection;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShardingDatabaseModuloAlgorithm<T extends Comparable<?>> implements StandardShardingAlgorithm<T> {
   public ShardingDatabaseModuloAlgorithm() {
   }

   public String doSharding(Collection<String> collection, PreciseShardingValue<T> preciseShardingValue) {
      Long value = Long.valueOf(((Comparable)preciseShardingValue.getValue()).toString());
      Long mo = value % (long)collection.size() + 1L;
      String db_suffix;
      if (mo < 10L) {
         db_suffix = "_0" + mo;
      } else {
         db_suffix = "_" + mo;
      }

      for(String each : collection) {
         if (each.endsWith(db_suffix)) {
            return each;
         }
      }

      throw new UnsupportedOperationException("\u4e0d\u652f\u6301\u7684\u5e93" + value);
   }

   public Collection<String> doSharding(Collection<String> collection, RangeShardingValue<T> rangeShardingValue) {
      return collection;
   }

   public String getType() {
      return "STANDDARD_DB_MODULO";
   }
}
