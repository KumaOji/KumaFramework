package com.kuma.boot.data.shardingsphere.algorithm;

import com.kuma.boot.common.utils.log.LogUtils;
import java.time.LocalDateTime;
import java.util.Collection;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShardingAlgorithmDB implements StandardShardingAlgorithm<LocalDateTime> {
   public ShardingAlgorithmDB() {
   }

   public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<LocalDateTime> shardingValue) {
      LogUtils.info("[分库算法开始]", new Object[0]);
      availableTargetNames.stream().forEach((item) -> LogUtils.info("[存在的库名<{}>]", new Object[]{item}));
      LogUtils.info("[表名<{}> 列名<{}>]", new Object[]{shardingValue.getLogicTableName(), shardingValue.getColumnName()});
      LogUtils.info("[分库列的值<{}>]", new Object[]{shardingValue.getValue()});

      for(String each : availableTargetNames) {
         LogUtils.info("[分库列的值<{}>]", new Object[]{shardingValue.getValue()});
         if (each.equals("db0")) {
            return "db0";
         }
      }

      throw new IllegalArgumentException();
   }

   public Collection<String> doSharding(Collection<String> availableTargetNames, RangeShardingValue<LocalDateTime> shardingValue) {
      return availableTargetNames;
   }

   public String getType() {
      return "COMMON_SHARD";
   }
}
