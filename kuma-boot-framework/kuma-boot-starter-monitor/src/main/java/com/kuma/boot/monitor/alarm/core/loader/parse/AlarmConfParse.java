package com.kuma.boot.monitor.alarm.core.loader.parse;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.TypeReference;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.monitor.alarm.core.entity.AlarmConfig;
import com.kuma.boot.monitor.alarm.core.entity.AlarmThreshold;
import com.kuma.boot.monitor.alarm.core.entity.BasicAlarmConfig;
import com.kuma.boot.monitor.alarm.core.entity.BasicAlarmThreshold;
import com.kuma.boot.monitor.alarm.core.execut.SimpleExecuteFactory;
import com.kuma.boot.monitor.alarm.core.execut.spi.LogExecute;
import com.kuma.boot.monitor.alarm.core.execut.spi.NoneExecute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlarmConfParse {
   public static final String DEFAULT_ALARM_KEY = "default";
   private static final Logger logger = LoggerFactory.getLogger(AlarmConfParse.class);
   private static final BasicAlarmConfig DEFAULT_ALARM_CONFIG = getDefaultAlarmConfig();
   private static final TypeReference<Map<String, BasicAlarmConfig>> typeReference = new TypeReference<Map<String, BasicAlarmConfig>>() {
   };
   private static List<String> currentUsers;

   public AlarmConfParse() {
   }

   public static Map<String, AlarmConfig> parseConfig(String configs, List<String> defaultUsers) {
      currentUsers = defaultUsers;
      Map<String, BasicAlarmConfig> map = parseStrConfig2Map(configs);
      if (map == null) {
         return null;
      } else {
         ConcurrentHashMap<String, AlarmConfig> backConfigMap = new ConcurrentHashMap();

         for(Map.Entry<String, BasicAlarmConfig> entry : map.entrySet()) {
            AlarmConfig temp = parse2BizConfig((BasicAlarmConfig)entry.getValue());
            if (temp != null) {
               for(String key : StringUtils.split((String)entry.getKey(), ",")) {
                  backConfigMap.put(key, temp);
               }
            }
         }

         return backConfigMap;
      }
   }

   private static Map<String, BasicAlarmConfig> parseStrConfig2Map(String configs) {
      Map<String, BasicAlarmConfig> map = null;
      if (configs != null) {
         try {
            map = (Map)JSON.parseObject(configs, typeReference, new JSONReader.Feature[0]);
         } catch (Exception e) {
            logger.error("ConfigWrapper.parseStrConfig2Map() init config error! configs: {}, e:{}", configs, e);
            return null;
         }
      }

      if (map == null) {
         map = new HashMap(1);
      }

      if (!map.containsKey("default")) {
         map.put("default", DEFAULT_ALARM_CONFIG);
      }

      return map;
   }

   private static AlarmConfig parse2BizConfig(BasicAlarmConfig basicAlarmConfig) {
      if (basicAlarmConfig.getUsers() != null && !basicAlarmConfig.getUsers().isEmpty()) {
         AlarmConfig alarmConfig = new AlarmConfig();
         alarmConfig.setExecutor(SimpleExecuteFactory.getExecute(basicAlarmConfig.getLevel()));
         alarmConfig.setAutoIncEmergency(basicAlarmConfig.isAutoIncEmergency());
         alarmConfig.setUsers(basicAlarmConfig.getUsers());
         alarmConfig.setMaxLimit(basicAlarmConfig.getMax() == null ? 30 : basicAlarmConfig.getMax());
         alarmConfig.setMinLimit(basicAlarmConfig.getMin() == null ? 0 : basicAlarmConfig.getMin());
         List<BasicAlarmThreshold> basicAlarmThresholdList = basicAlarmConfig.getThreshold();
         if (basicAlarmThresholdList == null) {
            basicAlarmThresholdList = Collections.emptyList();
         }

         basicAlarmThresholdList.sort(Comparator.comparingInt(BasicAlarmThreshold::getThreshold));
         List<AlarmThreshold> alarmThresholdList = new ArrayList(basicAlarmThresholdList.size() + 2);
         boolean containDefaultExecute = false;

         for(int i = 0; i < basicAlarmThresholdList.size(); ++i) {
            BasicAlarmThreshold tmpBasicAlarmThreshold = (BasicAlarmThreshold)basicAlarmThresholdList.get(i);
            AlarmThreshold tmpAlarmThreshold = new AlarmThreshold();
            tmpAlarmThreshold.setExecutor(SimpleExecuteFactory.getExecute(tmpBasicAlarmThreshold.getLevel()));
            tmpAlarmThreshold.setUsers(tmpBasicAlarmThreshold.getUsers());
            tmpAlarmThreshold.setMin(tmpBasicAlarmThreshold.getThreshold());
            if (tmpBasicAlarmThreshold.getMax() != null && tmpBasicAlarmThreshold.getMax() > tmpBasicAlarmThreshold.getThreshold()) {
               tmpAlarmThreshold.setMax(tmpBasicAlarmThreshold.getMax());
            } else if (i == basicAlarmThresholdList.size() - 1) {
               tmpAlarmThreshold.setMax(alarmConfig.getMaxLimit());
            } else {
               tmpAlarmThreshold.setMax(((BasicAlarmThreshold)basicAlarmThresholdList.get(i + 1)).getThreshold());
            }

            if (!containDefaultExecute) {
               containDefaultExecute = tmpBasicAlarmThreshold.getLevel().equals(basicAlarmConfig.getLevel());
            }

            alarmThresholdList.add(tmpAlarmThreshold);
         }

         int thresholdSize = alarmThresholdList.size();
         if (thresholdSize == 0) {
            AlarmThreshold tmpAlarmThreshold = new AlarmThreshold();
            tmpAlarmThreshold.setExecutor(alarmConfig.getExecutor());
            tmpAlarmThreshold.setUsers(alarmConfig.getUsers());
            tmpAlarmThreshold.setMin(alarmConfig.getMinLimit());
            tmpAlarmThreshold.setMax(alarmConfig.getMaxLimit());
            alarmThresholdList.add(tmpAlarmThreshold);
         } else if (!containDefaultExecute) {
            AlarmThreshold tmpAlarmThreshold = new AlarmThreshold();
            tmpAlarmThreshold.setExecutor(alarmConfig.getExecutor());
            tmpAlarmThreshold.setUsers(alarmConfig.getUsers());
            tmpAlarmThreshold.setMin(alarmConfig.getMinLimit());
            tmpAlarmThreshold.setMax(((AlarmThreshold)alarmThresholdList.get(0)).getMin());
            alarmThresholdList.add(0, tmpAlarmThreshold);
            if (((AlarmThreshold)alarmThresholdList.get(thresholdSize)).getMax() < alarmConfig.getMaxLimit()) {
               tmpAlarmThreshold = new AlarmThreshold();
               tmpAlarmThreshold.setExecutor(alarmConfig.getExecutor());
               tmpAlarmThreshold.setUsers(alarmConfig.getUsers());
               tmpAlarmThreshold.setMin(((AlarmThreshold)alarmThresholdList.get(thresholdSize)).getMax());
               tmpAlarmThreshold.setMax(alarmConfig.getMaxLimit());
               alarmThresholdList.add(tmpAlarmThreshold);
            }
         }

         alarmConfig.setAlarmThreshold(alarmThresholdList);
         return alarmConfig;
      } else {
         return null;
      }
   }

   private static BasicAlarmConfig getDefaultAlarmConfig() {
      BasicAlarmConfig defaultConfig = new BasicAlarmConfig();
      defaultConfig.setMin(5);
      defaultConfig.setMax(30);
      defaultConfig.setUsers(currentUsers);
      defaultConfig.setLevel(NoneExecute.NAME);
      defaultConfig.setAutoIncEmergency(true);
      BasicAlarmThreshold logThreshold = new BasicAlarmThreshold();
      logThreshold.setThreshold(10);
      logThreshold.setLevel(LogExecute.NAME);
      logThreshold.setUsers(currentUsers);
      defaultConfig.setThreshold(Collections.singletonList(logThreshold));
      return defaultConfig;
   }
}
