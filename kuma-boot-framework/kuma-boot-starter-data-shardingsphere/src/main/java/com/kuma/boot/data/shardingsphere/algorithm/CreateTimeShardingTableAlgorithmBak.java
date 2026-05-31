package com.kuma.boot.data.shardingsphere.algorithm;

import com.kuma.boot.common.utils.date.DateUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

public class CreateTimeShardingTableAlgorithmBak implements StandardShardingAlgorithm<Long> {
   public CreateTimeShardingTableAlgorithmBak() {
   }

   public String doSharding(Collection<String> collection, PreciseShardingValue<Long> preciseShardingValue) {
      Long createTime = (Long)preciseShardingValue.getValue();
      String value = DateUtils.toString(createTime, "MM");
      Integer month = Integer.valueOf(value);
      return "t_order_" + month;
   }

   public Collection<String> doSharding(Collection collection, RangeShardingValue rangeShardingValue) {
      Collection<String> collect = new ArrayList<>();

      for(int i = 1; i <= 12; ++i) {
         collect.add("t_order_" + i);
      }

      return collect;
   }

   public String getType() {
      return null;
   }

   public void init(Properties props) {
   }
}
