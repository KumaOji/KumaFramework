package com.kuma.boot.data.shardingsphere.algorithm;

import java.util.Collection;
import java.util.Properties;
import org.apache.shardingsphere.sharding.api.sharding.hint.HintShardingAlgorithm;
import org.apache.shardingsphere.sharding.api.sharding.hint.HintShardingValue;

public class DataSourceShardingAlgorithm implements HintShardingAlgorithm {
   public DataSourceShardingAlgorithm() {
   }

   @SuppressWarnings("unchecked")
   public Collection<String> doSharding(Collection collection, HintShardingValue hintShardingValue) {
      return hintShardingValue.getValues();
   }

   public String getType() {
      return null;
   }

   public void init(Properties props) {
   }
}
