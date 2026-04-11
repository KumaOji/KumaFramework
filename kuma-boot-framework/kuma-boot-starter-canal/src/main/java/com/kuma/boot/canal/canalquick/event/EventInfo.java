package com.kuma.boot.canal.canalquick.event;

import com.alibaba.otter.canal.protocol.CanalEntry;

public class EventInfo {
   private CanalEntry.RowData rowData;
   private TableInfo tableInfo;
   private CanalEntry.EventType eventType;

   public EventInfo(CanalEntry.RowData rowData, String schemaName, String tableName, CanalEntry.EventType eventType) {
      this.rowData = rowData;
      this.tableInfo = new TableInfo(schemaName, tableName);
      this.eventType = eventType;
   }

   public String getUnionKey() {
      return EventHandlerFactory.createUnionKey(this.tableInfo, this.eventType);
   }

   public CanalEntry.RowData getRowData() {
      return this.rowData;
   }

   public TableInfo getTableInfo() {
      return this.tableInfo;
   }

   public CanalEntry.EventType getEventType() {
      return this.eventType;
   }
}
