package com.kuma.boot.monitor.alarm.core.execut.spi;

import com.kuma.boot.monitor.alarm.core.execut.api.IExecute;
import com.kuma.boot.monitor.alarm.core.execut.gen.ExecuteNameGenerator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogExecute implements IExecute {
   public static final String NAME = ExecuteNameGenerator.genExecuteName(LogExecute.class);
   private static final Logger logger = LoggerFactory.getLogger("alarm");

   public LogExecute() {
   }

   public void sendMsg(List<String> users, String title, String msg) {
      logger.info("Do send msg by {} to user:{}, title: {}, msg: {}", new Object[]{this.getName(), users, title, msg});
   }
}
