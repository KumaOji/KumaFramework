package com.kuma.boot.monitor.alarm.core.execut.spi;

import com.kuma.boot.monitor.alarm.core.execut.api.IExecute;
import com.kuma.boot.monitor.alarm.core.execut.gen.ExecuteNameGenerator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoneExecute implements IExecute {
   public static final String NAME = ExecuteNameGenerator.genExecuteName(NoneExecute.class);
   private static final Logger logger = LoggerFactory.getLogger("alarm");

   public NoneExecute() {
   }

   public void sendMsg(List<String> users, String title, String msg) {
      if (logger.isDebugEnabled()) {
         logger.debug("{} mock! users: {}, title: {}, msg: {}", new Object[]{this.getName(), users, title, msg});
      }

   }
}
