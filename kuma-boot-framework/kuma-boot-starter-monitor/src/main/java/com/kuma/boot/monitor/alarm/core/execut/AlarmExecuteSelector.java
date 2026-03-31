package com.kuma.boot.monitor.alarm.core.execut;

import com.kuma.boot.monitor.alarm.core.entity.AlarmConfig;
import com.kuma.boot.monitor.alarm.core.entity.AlarmThreshold;
import com.kuma.boot.monitor.alarm.core.execut.spi.NoneExecute;
import com.kuma.boot.monitor.alarm.core.helper.ExecuteHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlarmExecuteSelector {
   public AlarmExecuteSelector() {
   }

   public static ExecuteHelper getDefaultExecute() {
      return ExecuteHelper.DEFAULT_EXECUTE;
   }

   public static List<ExecuteHelper> getExecute(final AlarmConfig alarmConfig, int count) {
      if (count >= alarmConfig.getMinLimit() && count <= alarmConfig.getMaxLimit()) {
         if (!alarmConfig.isAutoIncEmergency()) {
            return Collections.singletonList(new ExecuteHelper(alarmConfig.getExecutor(), alarmConfig.getUsers()));
         } else if (count < ((AlarmThreshold)alarmConfig.getAlarmThreshold().get(0)).getMin()) {
            return Collections.singletonList(new ExecuteHelper(SimpleExecuteFactory.getExecute(NoneExecute.NAME), alarmConfig.getUsers()));
         } else {
            List<ExecuteHelper> list = new ArrayList();

            for(AlarmThreshold alarmThreshold : alarmConfig.getAlarmThreshold()) {
               if (alarmThreshold.getMin() <= count && count < alarmThreshold.getMax()) {
                  list.add(new ExecuteHelper(alarmThreshold.getExecutor(), alarmThreshold.getUsers()));
               }

               if (alarmThreshold.getMin() > count) {
                  break;
               }
            }

            return list;
         }
      } else {
         return Collections.singletonList(new ExecuteHelper(SimpleExecuteFactory.getExecute(NoneExecute.NAME), alarmConfig.getUsers()));
      }
   }
}
