package com.kuma.boot.monitor.alarm.plugin.feishu;

import com.kuma.boot.monitor.alarm.core.execut.api.IExecute;
import com.kuma.boot.monitor.alarm.plugin.feishu.util.FeishuPublisher;
import java.util.List;

public class FeishuExecute implements IExecute {
   public FeishuExecute() {
   }

   public void sendMsg(List<String> users, String title, String msg) {
      users.forEach((user) -> FeishuPublisher.sendMessage(title, msg, user));
   }
}
