package com.kuma.boot.ddd.domain.support;

import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.messaging.Message;
import org.springframework.util.Assert;

public class TraceHandler {
   protected void putTrace(MessageExt messageExt) {
      String traceId = messageExt.getProperty("trace_id");
      String spanId = messageExt.getProperty("span_id");
   }

   protected void putTrace(Message message) {
      Object obj1 = message.getHeaders().get("trace_id");
      Object obj2 = message.getHeaders().get("span_id");
      Assert.notNull(obj1, "链路ID不为空");
      Assert.notNull(obj2, "标签ID不为空");
   }

   protected void clearTrace() {
   }
}
