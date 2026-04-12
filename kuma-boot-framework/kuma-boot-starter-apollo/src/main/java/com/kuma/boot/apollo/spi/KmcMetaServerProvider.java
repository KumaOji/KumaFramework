package com.kuma.boot.apollo.spi;

import com.ctrip.framework.apollo.internals.DefaultMetaServerProvider;
import com.kuma.boot.common.utils.log.LogUtils;

public class KmcMetaServerProvider extends DefaultMetaServerProvider {
   public KmcMetaServerProvider() {
      LogUtils.info("apollo MetaServerProvider open", new Object[0]);
   }

   public int getOrder() {
      return -1;
   }
}
