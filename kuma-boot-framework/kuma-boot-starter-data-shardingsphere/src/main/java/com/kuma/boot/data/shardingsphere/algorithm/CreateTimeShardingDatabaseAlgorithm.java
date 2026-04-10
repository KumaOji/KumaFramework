package com.kuma.boot.data.shardingsphere.algorithm;

import com.google.common.collect.Range;
import com.kuma.boot.common.utils.date.DateUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

public class CreateTimeShardingDatabaseAlgorithm implements StandardShardingAlgorithm<Long> {
   public CreateTimeShardingDatabaseAlgorithm() {
   }

   public String doSharding(Collection<String> collection, PreciseShardingValue<Long> preciseShardingValue) {
      Long createTime = (Long)preciseShardingValue.getValue();
      String value = DateUtils.toString(createTime, "yyyy");
      return "data" + value;
   }

   public Collection<String> doSharding(Collection collection, RangeShardingValue rangeShardingValue) {
      Collection<String> collect = new ArrayList();
      Range<Integer> valueRange = rangeShardingValue.getValueRange();
      String start = DateUtils.toString(((Integer)valueRange.lowerEndpoint()).longValue(), "yyyy");
      String end = DateUtils.toString(((Integer)valueRange.upperEndpoint()).longValue(), "yyyy");

      for(int i = Integer.parseInt(start); i <= Integer.parseInt(end); ++i) {
         collect.add("data" + i);
      }

      return collect;
   }

   public String getType() {
      return null;
   }

   public void init(Properties props) {
   }
}
