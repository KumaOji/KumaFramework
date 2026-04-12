package com.kuma.boot.apollo.spi;

import com.ctrip.framework.apollo.internals.DefaultInjector;
import com.kuma.boot.common.utils.log.LogUtils;

public class KmcInjector extends DefaultInjector {
   public KmcInjector() {
      LogUtils.info("apollo Injector open", new Object[0]);
   }

   public int getOrder() {
      return -1;
   }
}
