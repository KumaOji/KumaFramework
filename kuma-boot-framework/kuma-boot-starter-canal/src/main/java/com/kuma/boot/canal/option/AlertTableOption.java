package com.kuma.boot.canal.option;

import com.alibaba.otter.canal.protocol.CanalEntry.EventType;

public abstract class AlertTableOption extends AbstractDBOption {
   public AlertTableOption() {
   }

   protected void setEventType() {
      this.eventType = EventType.ALTER;
   }
}
