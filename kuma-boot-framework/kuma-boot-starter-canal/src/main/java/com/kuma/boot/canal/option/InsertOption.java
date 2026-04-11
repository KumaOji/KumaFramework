package com.kuma.boot.canal.option;

import com.alibaba.otter.canal.protocol.CanalEntry.EventType;

public abstract class InsertOption extends AbstractDBOption {
   public InsertOption() {
   }

   protected void setEventType() {
      this.eventType = EventType.INSERT;
   }
}
