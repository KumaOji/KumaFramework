package com.kuma.boot.canal.option;

import com.alibaba.otter.canal.protocol.CanalEntry.EventType;

public abstract class UpdateOption extends AbstractDBOption {
   public UpdateOption() {
   }

   protected void setEventType() {
      this.eventType = EventType.UPDATE;
   }
}
