package com.kuma.boot.flowengine.state.retry;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.flowengine.exception.FlowException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class RetryContext {
   static Map<String, Consumer<Object>> FLOW_TERMINAL_CALLBACKS = new ConcurrentHashMap();

   public RetryContext() {
   }

   public static <T> void registerFlowRetryTerminalCallback(String flowName, Consumer<T> callback) {
      if (StringUtils.isEmpty(flowName)) {
         throw new FlowException("flow name \u4e0d\u80fd\u4e3a\u7a7a");
      } else if (callback == null) {
         throw new FlowException("\u5bf9\u5e94\u7684 retry callback \u4e0d\u80fd\u4e3a\u7a7a");
      } else {
         FLOW_TERMINAL_CALLBACKS.put(flowName, callback);
      }
   }
}
