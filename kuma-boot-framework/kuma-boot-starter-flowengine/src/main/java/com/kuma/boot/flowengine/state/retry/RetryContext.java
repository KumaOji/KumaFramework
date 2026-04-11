package com.kuma.boot.flowengine.state.retry;

import com.kuma.boot.flowengine.exception.FlowException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import com.kuma.boot.common.utils.lang.StringUtils;

public class RetryContext {

   static Map<String, Consumer<Object>> FLOW_TERMINAL_CALLBACKS = new ConcurrentHashMap<>();

   public static <T> void registerFlowRetryTerminalCallback(String flowName,
                                                            Consumer<T> callback) {
      if (StringUtils.isEmpty(flowName)) {
         throw new FlowException("flow name 不能为空");
      }

      if (callback == null) {
         throw new FlowException("对应的 retry callback 不能为空");
      }
      FLOW_TERMINAL_CALLBACKS.put(flowName, (Consumer<Object>) callback);
   }

//	public static void ss(String s) {
//		if (s.equals("2222")) {
//			throw new RuntimeException("sss");
//		}
//		System.out.println(s);
//	}

//	public static void main(String[] args) {
//		List<String> s = new ArrayList<>();
//		s.add("2222");
//		s.add("444");
//		try {
//			s.forEach(RetryContext::ss);
//		}
//		catch (Exception e) {
//			System.out.println("ppp");
//		}
//	}


}
