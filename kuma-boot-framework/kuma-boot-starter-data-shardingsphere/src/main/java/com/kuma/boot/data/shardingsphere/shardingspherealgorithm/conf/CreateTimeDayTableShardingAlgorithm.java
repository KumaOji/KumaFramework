package com.kuma.boot.data.shardingsphere.shardingspherealgorithm.conf;

import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Range;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

public class CreateTimeDayTableShardingAlgorithm implements StandardShardingAlgorithm<Timestamp>, CreateTimeShardingAlgorithm {
   private static final String FORMAT_LINK_DAY = "yyyyMMdd";

   public CreateTimeDayTableShardingAlgorithm() {
   }

   public Collection<String> doSharding(Collection<String> availableTargetNames, RangeShardingValue<Timestamp> shardingValue) {
      Range<Timestamp> valueRange = shardingValue.getValueRange();
      LocalDateTime start = null;

      try {
         start = ((Timestamp)valueRange.lowerEndpoint()).toLocalDateTime();
      } catch (Exception var13) {
         start = LocalDateTime.now().minusDays(15L);
      }

      LocalDateTime end = null;

      try {
         end = ((Timestamp)valueRange.upperEndpoint()).toLocalDateTime();
      } catch (Exception var12) {
         end = LocalDateTime.now();
      }

      Set<String> suffixList = new HashSet<>();
      Iterator<String> iterator = availableTargetNames.iterator();
      String tableName = (String)iterator.next();
      String name = tableName.substring(0, tableName.lastIndexOf("_"));
      if (start != null && end != null) {
         String startName = DateUtil.format(start, "yyyyMMdd");

         String endName;
         for(endName = DateUtil.format(end, "yyyyMMdd"); !startName.equals(endName); startName = DateUtil.format(start, "yyyyMMdd")) {
            if (availableTargetNames.contains(name + "_" + startName)) {
               suffixList.add(name + "_" + startName);
            }

            start = start.plusDays(1L);
         }

         if (availableTargetNames.contains(name + "_" + endName)) {
            suffixList.add(name + "_" + endName);
         }
      }

      return (Collection<String>)(CollectionUtils.isNotEmpty(suffixList) ? suffixList : availableTargetNames);
   }

   public void init(Properties properties) {
   }

   public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Timestamp> shardingValue) {
      LocalDateTime time = ((Timestamp)shardingValue.getValue()).toLocalDateTime();
      DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyyMMdd");
      String format = dtf2.format(time);

      for(String str : availableTargetNames) {
         if (str.endsWith(format)) {
            return str;
         }
      }

      return null;
   }

   public String buildNodesSuffix(LocalDate date) {
      DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
      return date.format(dateFormatter);
   }

   public LocalDate buildNodesBeforeDate(LocalDate date) {
      return date.minusDays(1L);
   }

   public LocalDate buildNodesAfterDate(LocalDate date) {
      return date.plusDays(1L);
   }
}
