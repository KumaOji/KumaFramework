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
         String tableName = preciseShardingValue.getLogicTableName();
         LocalDateTime minBaseDate = LocalDateTime.parse(StaticValue.userBaseTableMinDate, dateFormatter);
         if (date.isAfter(minBaseDate)) {
            String tableSuffix = date.format(monthFormatter);
            tableName = tableName.concat("_").concat(tableSuffix);
         }

         return (String)collection.stream().filter((str) -> str.equals(tableName)).findFirst().orElseThrow(() -> new RuntimeException(tableName + "\u5206\u8868\u4e0d\u5b58\u5728"));
      }
   }

   public Collection<String> doSharding(Collection collection, RangeShardingValue rangeShardingValue) {
      String logicTableName = rangeShardingValue.getLogicTableName();
      Range<LocalDateTime> valueRange = rangeShardingValue.getValueRange();
      Set<String> tableRange = this.extracted(logicTableName, (LocalDateTime)valueRange.lowerEndpoint(), (LocalDateTime)valueRange.upperEndpoint());
      return tableRange;
   }

   private Set<String> extracted(String logicTableName, LocalDateTime lowerEndpoint, LocalDateTime upperEndpoint) {
      Set<String> rangeTable = new HashSet();
      LocalDateTime minBaseDate = LocalDateTime.parse(StaticValue.userBaseTableMinDate, dateFormatter);
      LocalDateTime maxBaseDate = LocalDateTime.parse(StaticValue.userBaseTableMaxDate, dateFormatter);
      if (lowerEndpoint.isBefore(minBaseDate)) {
         lowerEndpoint = minBaseDate;
         rangeTable.add(logicTableName);
      }

      if (upperEndpoint.isAfter(maxBaseDate)) {
         throw new RuntimeException("\u7ed3\u675f\u65f6\u95f4\u4e0d\u5728\u5f53\u524d\u65f6\u95f4\u5185");
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
