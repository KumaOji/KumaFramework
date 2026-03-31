package com.kuma.boot.monitor.alarm.core.loader;

import com.kuma.boot.monitor.alarm.core.entity.AlarmConfig;
import com.kuma.boot.monitor.alarm.core.helper.ExecuteHelper;
import com.kuma.boot.monitor.alarm.core.loader.api.IConfLoader;
import com.kuma.boot.monitor.alarm.core.loader.entity.RegisterInfo;
import java.util.List;

public class ConfLoaderProxy implements IConfLoader {
   private List<IConfLoader> list;

   public ConfLoaderProxy(List<IConfLoader> list) {
      this.list = list;
   }

   public RegisterInfo getRegisterInfo() {
      return ((IConfLoader)this.list.get(0)).getRegisterInfo();
   }

   public boolean alarmEnable(String alarmKey) {
      return this.chooseConfLoader(alarmKey).alarmEnable(alarmKey);
   }

   public AlarmConfig getAlarmConfigOrDefault(String alarmKey) {
      return this.chooseConfLoader(alarmKey).getAlarmConfigOrDefault(alarmKey);
   }

   public List<ExecuteHelper> getExecuteHelper(String alarmKey, int count) {
      IConfLoader loader = this.chooseConfLoader(alarmKey);
      return loader.getExecuteHelper(alarmKey, count);
   }

   private IConfLoader chooseConfLoader(String alarmKey) {
      for(IConfLoader loader : this.list) {
         if (loader.containAlarmConfig(alarmKey)) {
            return loader;
         }
      }

      return (IConfLoader)this.list.get(0);
   }
}
