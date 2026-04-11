package com.kuma.boot.canal.option;

import com.alibaba.otter.canal.protocol.CanalEntry.EventType;

public abstract class DeleteOption extends AbstractDBOption {
   public DeleteOption() {
   }

   protected void setEventType() {
      this.eventType = EventType.DELETE;
   }
}
