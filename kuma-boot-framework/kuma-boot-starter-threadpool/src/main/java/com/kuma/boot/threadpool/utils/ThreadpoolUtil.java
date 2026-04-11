package com.kuma.boot.threadpool.utils;

import org.dromara.dynamictp.core.executor.DtpExecutor;
import org.dromara.dynamictp.core.support.ThreadPoolCreator;

public class ThreadpoolUtil {
   public ThreadpoolUtil() {
   }

   public static DtpExecutor dtpExecutor() {
      return ThreadPoolCreator.createDynamicFast("commonExecutor");
   }
}
