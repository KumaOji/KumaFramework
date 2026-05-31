package com.kuma.boot.data.shardingsphere.algorithm;

import cn.hutool.core.convert.Convert;
import com.google.common.collect.Range;
import com.kuma.boot.common.utils.date.DateUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

public class CreateTimeShardingTableAlgorithm implements StandardShardingAlgorithm<Long> {
   public CreateTimeShardingTableAlgorithm() {
   }

   public String doSharding(Collection<String> collection, PreciseShardingValue<Long> preciseShardingValue) {
      Long createTime = (Long)preciseShardingValue.getValue();
      String monthValue = DateUtils.toString(createTime, "MM");
      String yearValue = DateUtils.toString(createTime, "yyyy");
      Integer month = Integer.valueOf(monthValue);
      Integer year = Integer.valueOf(yearValue);
      return "tt_order_" + year + "_" + month;
   }

   @SuppressWarnings("unchecked")
   public Collection<String> doSharding(Collection collection, RangeShardingValue rangeShardingValue) {
      Collection<String> collect = new ArrayList<>();
      Range<Integer> valueRange = rangeShardingValue.getValueRange();
      Integer startMonth = Convert.toInt(DateUtils.toString(((Integer)valueRange.lowerEndpoint()).longValue(), "MM"));
      Integer endMonth = Convert.toInt(DateUtils.toString(((Integer)valueRange.upperEndpoint()).longValue(), "MM"));
      Integer startYear = Convert.toInt(DateUtils.toString(((Integer)valueRange.lowerEndpoint()).longValue(), "yyyy"));
      Integer endYear = Convert.toInt(DateUtils.toString(((Integer)valueRange.upperEndpoint()).longValue(), "yyyy"));
      if (startYear.equals(endYear)) {
         for(Integer i = startYear; i <= endYear; ++i) {
            for(Integer j = startMonth; j <= endMonth; ++j) {
               collect.add("tt_order_" + i + "_" + j);
            }
         }
      } else {
         for(Integer i = startYear; i <= endYear; ++i) {
            if (i.equals(startYear)) {
               for(Integer j = startMonth; j <= 12; ++j) {
                  collect.add("tt_order_" + i + "_" + j);
               }
            } else if (i.equals(endYear)) {
               for(Integer j = 1; j <= endMonth; ++j) {
                  collect.add("tt_order_" + i + "_" + j);
               }
            } else {
               for(int j = 1; j <= 12; ++j) {
                  collect.add("tt_order_" + i + "_" + j);
               }
            }
         }
      }

      return collect;
   }

   public String getType() {
      return null;
   }

   public void init(Properties props) {
   }
}
