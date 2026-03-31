package com.kuma.boot.monitor.alarm.core.loader;

import com.kuma.boot.monitor.alarm.core.exception.NoAlarmLoaderSpecifyException;
import com.kuma.boot.monitor.alarm.core.loader.api.IConfLoader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

public class ConfLoaderFactory {
   private static IConfLoader currentAlarmConfLoader;

   public ConfLoaderFactory() {
   }

   public static IConfLoader loader() {
      if (currentAlarmConfLoader == null) {
         synchronized(ConfLoaderFactory.class) {
            if (currentAlarmConfLoader == null) {
               initConfLoader();
            }
         }
      }

      return currentAlarmConfLoader;
   }

   private static void initConfLoader() {
      Iterator<IConfLoader> iterator = ServiceLoader.load(IConfLoader.class).iterator();
      List<IConfLoader> list = new ArrayList();

      while(iterator.hasNext()) {
         list.add((IConfLoader)iterator.next());
      }

      list.sort(Comparator.comparingInt(IConfLoader::order));
      List<IConfLoader> ans = new ArrayList(list.size());

      for(IConfLoader iConfLoader : list) {
         if (iConfLoader.load()) {
            ans.add(iConfLoader);
         }
      }

      if (ans.isEmpty()) {
         throw new NoAlarmLoaderSpecifyException("no special alarmConfLoader selected!");
      } else {
         currentAlarmConfLoader = new ConfLoaderProxy(ans);
      }
   }
}
