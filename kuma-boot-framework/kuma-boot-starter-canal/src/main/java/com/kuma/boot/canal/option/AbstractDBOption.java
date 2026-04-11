package com.kuma.boot.canal.option;

import com.alibaba.otter.canal.protocol.CanalEntry;

public abstract class AbstractDBOption implements IDBOption {
   protected CanalEntry.EventType eventType;
   protected AbstractDBOption next;

   public AbstractDBOption() {
      this.setEventType();
   }

   protected abstract void setEventType();

   public void setNext(AbstractDBOption next) {
      this.next = next;
   }

   public void doChain(String destination, String schemaName, String tableName, CanalEntry.RowChange rowChange) {
      if (this.eventType.equals(rowChange.getEventType())) {
         this.doOption(destination, schemaName, tableName, rowChange);
      } else {
         if (this.next == null) {
            return;
         }

         this.next.doChain(destination, schemaName, tableName, rowChange);
      }

   }
}
