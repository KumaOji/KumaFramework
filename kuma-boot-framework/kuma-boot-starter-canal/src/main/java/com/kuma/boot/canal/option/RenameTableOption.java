package com.kuma.boot.canal.option;

import com.alibaba.otter.canal.protocol.CanalEntry.EventType;

public abstract class RenameTableOption extends AbstractDBOption {
   public RenameTableOption() {
   }

   protected void setEventType() {
      this.eventType = EventType.RENAME;
   }
}
