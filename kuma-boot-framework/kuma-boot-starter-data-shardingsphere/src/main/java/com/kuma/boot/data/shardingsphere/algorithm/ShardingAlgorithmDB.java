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
      LogUtils.info("[\u5206\u5e93\u7b97\u6cd5\u5f00\u59cb]", new Object[0]);
      availableTargetNames.stream().forEach((item) -> LogUtils.info("[\u5b58\u5728\u7684\u5e93\u540d<{}>]", new Object[]{item}));
      LogUtils.info("[\u8868\u540d<{}> \u5217\u540d<{}>]", new Object[]{shardingValue.getLogicTableName(), shardingValue.getColumnName()});
      LogUtils.info("[\u5206\u5e93\u5217\u7684\u503c<{}>]", new Object[]{shardingValue.getValue()});

      for(String each : availableTargetNames) {
         LogUtils.info("[\u5206\u5e93\u5217\u7684\u503c<{}>]", new Object[]{shardingValue.getValue()});
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
