package com.kuma.boot.canal.option;

import com.alibaba.otter.canal.protocol.CanalEntry.EventType;

public abstract class DropTableOption extends AbstractDBOption {
   public DropTableOption() {
   }

   protected void setEventType() {
      this.eventType = EventType.ERASE;
   }
}
