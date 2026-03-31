package com.kuma.boot.monitor.alarm.core.execut;

import com.kuma.boot.monitor.alarm.core.exception.DuplicatedAlarmExecuteDefinedException;
import com.kuma.boot.monitor.alarm.core.execut.api.IExecute;
import com.kuma.boot.monitor.alarm.core.execut.spi.LogExecute;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class SimpleExecuteFactory {
   private static Map<String, IExecute> cacheMap;

   public SimpleExecuteFactory() {
   }

   private static void loadAlarmExecute() {
      Map<String, IExecute> map = new HashMap();

      for(IExecute tmp : ServiceLoader.load(IExecute.class)) {
         if (map.containsKey(tmp.getName())) {
            String var10002 = tmp.getName();
            throw new DuplicatedAlarmExecuteDefinedException("duplicated alarm executor defined!\n>>name:" + var10002 + ">>>clz:" + String.valueOf(tmp.getClass()) + ">>>clz:" + String.valueOf(map.get(tmp.getName())));
         }

         map.put(tmp.getName(), tmp);
      }

      cacheMap = map;
   }

   public static IExecute getExecute(String execute) {
      if (cacheMap == null) {
         synchronized(SimpleExecuteFactory.class) {
            if (cacheMap == null) {
               loadAlarmExecute();
            }
         }
      }

      IExecute e = (IExecute)cacheMap.get(execute);
      return e == null ? (IExecute)cacheMap.get(LogExecute.NAME) : e;
   }
}
