package com.kuma.boot.data.shardingsphere.algorithm;

import com.google.common.collect.Range;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderTableAlgorithm implements StandardShardingAlgorithm<LocalDateTime> {
   private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
   private static final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyyMM");

   public OrderTableAlgorithm() {
   }

   public String doSharding(Collection<String> collection, PreciseShardingValue<LocalDateTime> preciseShardingValue) {
      LocalDateTime date = (LocalDateTime)preciseShardingValue.getValue();
      if (date == null) {
         return (String)collection.stream().findFirst().get();
      } else {
         String logicTableName = preciseShardingValue.getLogicTableName();
         LocalDateTime minBaseDate = LocalDateTime.parse(StaticValue.userBaseTableMinDate, dateFormatter);
         String tableName;
         if (date.isAfter(minBaseDate)) {
            String tableSuffix = date.format(monthFormatter);
            tableName = logicTableName.concat("_").concat(tableSuffix);
         } else {
            tableName = logicTableName;
         }

         final String finalTableName = tableName;
         return (String)collection.stream().filter((str) -> str.equals(finalTableName)).findFirst().orElseThrow(() -> new RuntimeException(finalTableName + "分表不存在"));
      }
   }

   @SuppressWarnings("unchecked")
   public Collection<String> doSharding(Collection collection, RangeShardingValue rangeShardingValue) {
      String logicTableName = rangeShardingValue.getLogicTableName();
      Range<LocalDateTime> valueRange = rangeShardingValue.getValueRange();
      Set<String> tableRange = this.extracted(logicTableName, (LocalDateTime)valueRange.lowerEndpoint(), (LocalDateTime)valueRange.upperEndpoint());
      return tableRange;
   }

   private Set<String> extracted(String logicTableName, LocalDateTime lowerEndpoint, LocalDateTime upperEndpoint) {
      Set<String> rangeTable = new HashSet<>();
      LocalDateTime minBaseDate = LocalDateTime.parse(StaticValue.userBaseTableMinDate, dateFormatter);
      LocalDateTime maxBaseDate = LocalDateTime.parse(StaticValue.userBaseTableMaxDate, dateFormatter);
      if (lowerEndpoint.isBefore(minBaseDate)) {
         lowerEndpoint = minBaseDate;
         rangeTable.add(logicTableName);
      }

      if (upperEndpoint.isAfter(maxBaseDate)) {
         throw new RuntimeException("结束时间不在当前时间内");
      } else {
         while(lowerEndpoint.isBefore(upperEndpoint)) {
            String tableName = logicTableName.concat("_").concat(lowerEndpoint.format(monthFormatter));
            rangeTable.add(tableName);
            lowerEndpoint = lowerEndpoint.plusMonths(1L);
         }

         String tableName = logicTableName.concat("_").concat(upperEndpoint.format(monthFormatter));
         rangeTable.add(tableName);
         return rangeTable;
      }
   }

   public String getType() {
      return null;
   }
}
