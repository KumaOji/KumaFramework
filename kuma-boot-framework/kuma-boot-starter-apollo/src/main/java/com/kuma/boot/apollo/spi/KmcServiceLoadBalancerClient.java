package com.kuma.boot.apollo.spi;

import com.ctrip.framework.apollo.spi.RandomConfigServiceLoadBalancerClient;
import com.kuma.boot.common.utils.log.LogUtils;

public class KmcServiceLoadBalancerClient extends RandomConfigServiceLoadBalancerClient {
   public KmcServiceLoadBalancerClient() {
      LogUtils.info("apollo ServiceLoadBalancerClient open", new Object[0]);
   }

   public int getOrder() {
      return -1;
   }
}
