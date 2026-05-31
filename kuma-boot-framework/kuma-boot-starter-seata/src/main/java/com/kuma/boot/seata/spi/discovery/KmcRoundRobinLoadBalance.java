package com.kuma.boot.seata.spi.discovery;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.seata.common.loader.LoadLevel;
import org.apache.seata.discovery.loadbalance.LoadBalance;

@LoadLevel(
   name = "KmcRoundRobinLoadBalance"
)
public class KmcRoundRobinLoadBalance implements LoadBalance {
   private final AtomicInteger sequence = new AtomicInteger();

   public <T> T select(List<T> invokers, String xid) {
      int length = invokers.size();
      return invokers.get(this.getPositiveSequence() % length);
   }

   private int getPositiveSequence() {
      int current;
      int next;
      do {
         current = this.sequence.get();
         next = current >= Integer.MAX_VALUE ? 0 : current + 1;
      } while(!this.sequence.compareAndSet(current, next));

      return current;
   }
}
