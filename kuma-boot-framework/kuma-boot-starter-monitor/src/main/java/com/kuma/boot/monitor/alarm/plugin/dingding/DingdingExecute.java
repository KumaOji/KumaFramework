package com.kuma.boot.monitor.alarm.plugin.dingding;

import com.kuma.boot.monitor.alarm.core.execut.api.IExecute;
import com.kuma.boot.monitor.alarm.plugin.dingding.util.DingdingPublisher;
import java.util.List;

public class DingdingExecute implements IExecute {
   public DingdingExecute() {
   }

   public void sendMsg(List<String> users, String title, String msg) {
      for(String user : users) {
         DingdingPublisher.sendMessage(title, msg, user);
      }

   }
}
