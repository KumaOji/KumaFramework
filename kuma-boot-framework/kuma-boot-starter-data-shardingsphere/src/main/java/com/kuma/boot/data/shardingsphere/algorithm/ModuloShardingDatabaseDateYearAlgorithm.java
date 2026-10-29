package com.kuma.boot.data.shardingsphere.algorithm;

import com.google.common.collect.Range;
import com.kuma.boot.common.utils.log.LogUtils;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModuloShardingDatabaseDateYearAlgorithm implements StandardShardingAlgorithm<String> {
   private int ModedID = 5;
   private static final String DataBase_NAME = "ds_";

   public ModuloShardingDatabaseDateYearAlgorithm() {
   }

   @SuppressWarnings("deprecation")
   private int getYearFromString(String value) throws ParseException {
      int nYear = -1;

      try {
         if (null != value && !"".equals(value) && value.length() >= 4) {
            try {
               nYear = Integer.parseInt(value.substring(0, 4));
            } catch (Exception var5) {
               Date date = new Date();
               return date.getYear();
            }

            if (nYear < 1995) {
               nYear = 1995;
            }

            return nYear;
         } else {
            Date date = new Date();
            return date.getYear();
         }
      } catch (Exception var6) {
         Date date = new Date();
         return date.getYear();
      }
   }

   public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<String> shardingValue) {
      if (shardingValue != null) {
         String value = (String)shardingValue.getValue();
         int nYear = -1;

         try {
            nYear = this.getYearFromString(value);
         } catch (ParseException e) {
            LogUtils.error(e);
         }

         for(String each : availableTargetNames) {
            if (each.equals("ds_" + String.valueOf(nYear % 4))) {
               return each;
            }
         }

         throw new UnsupportedOperationException("content_channel没有匹配到可用数据库节点");
      } else {
         throw new UnsupportedOperationException("分片列为空");
      }
   }

   public Collection<String> doSharding(Collection<String> databaseNamescollection, RangeShardingValue<String> rangeShardingValue) {
      Collection<String> collect = new ArrayList<>();
      if (rangeShardingValue != null) {
         Range<String> valueRange = rangeShardingValue.getValueRange();
         String slowerEndpointDate = String.valueOf(valueRange.hasLowerBound() ? valueRange.lowerEndpoint() : "");
         String supperEndpointDate = String.valueOf(valueRange.hasUpperBound() ? valueRange.upperEndpoint() : "");
         int nStartYear = -1;
         int nEndYear = -1;

         try {
            nStartYear = this.getYearFromString(slowerEndpointDate);
            nEndYear = this.getYearFromString(supperEndpointDate);
            if (nStartYear == -1 && nEndYear != -1) {
               nStartYear = nEndYear - this.ModedID;
            } else if (nStartYear != -1 && nEndYear == -1) {
               nEndYear = nStartYear + this.ModedID;
            }
         } catch (ParseException e) {
            LogUtils.error(e);
         }

         for(String each : databaseNamescollection) {
            for(int i = nStartYear; i <= nEndYear; ++i) {
               if (each.equals("ds_" + String.valueOf(i % 4)) && !collect.contains(each)) {
                  collect.add(each);
               }
            }
         }

         return collect;
      } else {
         throw new UnsupportedOperationException("分片列为空");
      }
   }

   public String getType() {
      return null;
   }
}
