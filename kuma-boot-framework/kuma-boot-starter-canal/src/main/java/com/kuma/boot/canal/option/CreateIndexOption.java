package com.kuma.boot.canal.option;

import com.alibaba.otter.canal.protocol.CanalEntry.EventType;

public abstract class CreateIndexOption extends AbstractDBOption {
   public CreateIndexOption() {
   }

   protected void setEventType() {
      this.eventType = EventType.CINDEX;
   }
}
