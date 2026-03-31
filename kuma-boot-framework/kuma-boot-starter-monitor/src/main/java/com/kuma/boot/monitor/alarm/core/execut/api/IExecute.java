package com.kuma.boot.monitor.alarm.core.execut.api;

import com.kuma.boot.monitor.alarm.core.execut.gen.ExecuteNameGenerator;
import java.util.List;

public interface IExecute {
   void sendMsg(List<String> users, String title, String msg);

   default String getName() {
      return ExecuteNameGenerator.genExecuteName(this.getClass());
   }
}
