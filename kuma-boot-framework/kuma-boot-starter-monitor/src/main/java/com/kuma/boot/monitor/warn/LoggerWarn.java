package com.kuma.boot.monitor.warn;

import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.monitor.model.Message;

public class LoggerWarn extends AbstractWarn {
   public LoggerWarn() {
   }

   public void notify(Message message) {
      LogUtils.warn(JacksonUtils.toJSONString(message), new Object[0]);
   }
}
