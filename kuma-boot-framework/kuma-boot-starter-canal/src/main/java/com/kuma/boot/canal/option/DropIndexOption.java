package com.kuma.boot.canal.option;

import com.alibaba.otter.canal.protocol.CanalEntry.EventType;

public abstract class DropIndexOption extends AbstractDBOption {
   public DropIndexOption() {
   }

   protected void setEventType() {
      this.eventType = EventType.DINDEX;
   }
}
