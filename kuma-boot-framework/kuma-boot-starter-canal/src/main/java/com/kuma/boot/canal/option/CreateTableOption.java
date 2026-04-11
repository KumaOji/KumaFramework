package com.kuma.boot.canal.option;

import com.alibaba.otter.canal.protocol.CanalEntry.EventType;

public abstract class CreateTableOption extends AbstractDBOption {
   public CreateTableOption() {
   }

   protected void setEventType() {
      this.eventType = EventType.CREATE;
   }
}
