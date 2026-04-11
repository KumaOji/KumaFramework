package com.kuma.boot.eventbus.disruptor.tmp4;

import com.kuma.boot.eventbus.disruptor.tmp4.event.EventContext;
import com.kuma.boot.eventbus.disruptor.tmp4.util.NumberUtil;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Distributor {
   private static final Logger LOGGER = LoggerFactory.getLogger(Distributor.class);
   private final String name;
   private final List<DistributorLine> lines;

   public Distributor(String name, List<DistributorLine> lines) {
      this.name = name;
      this.lines = lines;
   }

   public void distribute(String targetStation, String targetTopic, EventContext context) {
      ((DistributorLine)this.lines.get(NumberUtil.randomIndex(this.lines.size()))).publish(targetStation, targetTopic, context);
   }

   public Distributor start() {
      this.lines.forEach(DistributorLine::start);
      LOGGER.info("\u3010Distributor\u3011 \"{}\" start to work", this.name);
      return this;
   }

   public void stop() {
      this.lines.forEach(DistributorLine::stop);
      WorkerStationManager.INSTANCE.stopAll();
      LOGGER.info("\u3010Distributor\u3011 \"{}\" has stopped", this.name);
   }

   public long hasPublishNum() {
      return this.lines.stream().mapToLong(DistributorLine::hasPublishNum).sum();
   }
}
