package com.kuma.boot.monitor.alarm.core.loader.api;

import com.kuma.boot.monitor.alarm.core.entity.AlarmConfig;
import com.kuma.boot.monitor.alarm.core.execut.AlarmExecuteSelector;
import com.kuma.boot.monitor.alarm.core.helper.ExecuteHelper;
import com.kuma.boot.monitor.alarm.core.loader.entity.RegisterInfo;
import java.util.Collections;
import java.util.List;

public interface IConfLoader {
   default boolean load() {
      return true;
   }

   default int order() {
      return 10;
   }

   RegisterInfo getRegisterInfo();

   boolean alarmEnable(String alarmKey);

   default boolean containAlarmConfig(String alarmKey) {
      return true;
   }

   AlarmConfig getAlarmConfigOrDefault(String alarmKey);

   default List<ExecuteHelper> getExecuteHelper(String alarmKey, int count) {
      return this.alarmEnable(alarmKey) ? AlarmExecuteSelector.getExecute(this.getAlarmConfigOrDefault(alarmKey), count) : Collections.singletonList(AlarmExecuteSelector.getDefaultExecute());
   }
}
