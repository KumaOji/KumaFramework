package com.kuma.boot.canal.canalquick;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.CanalEntry.EntryType;
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange;
import com.kuma.boot.canal.canalquick.config.CanalConfigProperties;
import com.kuma.boot.canal.canalquick.event.EventHandleContext;
import com.kuma.boot.canal.canalquick.event.EventHandlerFactory;
import com.kuma.boot.canal.canalquick.event.EventInfo;
import com.kuma.boot.canal.canalquick.event.IEventHandler;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.ArrayList;
import java.util.List;

public class QuickCanalClientListener {
   private CanalConnector canalConnector;
   private CanalConfigProperties canalConfig;
   private EventHandlerFactory eventHandlerFactory;
   private volatile boolean RUNNING = true;

   public QuickCanalClientListener(CanalConnector canalConnector, CanalConfigProperties canalConfig, EventHandlerFactory eventHandlerFactory) {
      this.canalConnector = canalConnector;
      this.canalConfig = canalConfig;
      this.eventHandlerFactory = eventHandlerFactory;
   }

   public void start() {
      while(this.RUNNING) {
         Message message = this.canalConnector.getWithoutAck(this.canalConfig.getBatchSize());
         long batchId = message.getId();
         int size = message.getEntries().size();
         if (batchId != -1L && size != 0) {
            LogUtils.debug("\u5904\u7406 batchId \u4e3a {} \u7684\u6570\u636e", new Object[]{batchId});
            List<EventHandleContext> handlers = this.createHandlers(this.createEventInfos(message.getEntries()));
            if (handlers != null) {
               for(EventHandleContext handler : handlers) {
                  try {
                     handler.handle();
                  } catch (Exception e) {
                     LogUtils.error("\u5904\u7406 batchId \u4e3a {} \u7684\u6570\u636e\u53d1\u751f\u5f02\u5e38 , \u5f02\u5e38 : {} ", new Object[]{batchId, e});
                     this.canalConnector.rollback();
                  }
               }
            }

            this.canalConnector.ack(batchId);
         }
      }

   }

   private List<EventInfo> createEventInfos(List<CanalEntry.Entry> entrys) {
      List<EventInfo> eventInfoList = new ArrayList(entrys.size());

      for(CanalEntry.Entry entry : entrys) {
         if (entry.getEntryType() != EntryType.TRANSACTIONBEGIN && entry.getEntryType() != EntryType.TRANSACTIONEND) {
            CanalEntry.RowChange rowChange = null;

            try {
               rowChange = RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
               throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(), e);
            }

            CanalEntry.EventType eventType = rowChange.getEventType();
            String schemaName = entry.getHeader().getSchemaName();
            String tableName = entry.getHeader().getTableName();
            LogUtils.info("===========binlog[{} : {}] ,tableName[{} : {}] , eventType : {}", new Object[]{entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(), schemaName, tableName, eventType});

            for(CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
               eventInfoList.add(new EventInfo(rowData, schemaName, tableName, eventType));
            }
         }
      }

      return eventInfoList;
   }

   private List<EventHandleContext> createHandlers(List<EventInfo> eventInfos) {
      if (eventInfos != null && eventInfos.size() != 0) {
         List<EventHandleContext> eventHandlerList = new ArrayList(eventInfos.size());

         for(EventInfo eventInfo : eventInfos) {
            IEventHandler handlerByEventType = this.createHandlerByEventType(eventInfo);
            if (handlerByEventType != null) {
               eventHandlerList.add(new EventHandleContext(handlerByEventType, eventInfo));
            }
         }

         return eventHandlerList;
      } else {
         LogUtils.debug("\u65e0\u76d1\u542c\u4e8b\u4ef6\u4ea7\u751f, \u65e0\u9700\u5904\u7406 .................................", new Object[0]);
         return null;
      }
   }

   private IEventHandler createHandlerByEventType(EventInfo eventInfo) {
      Boolean custom = this.canalConfig.getCustom();
      return custom ? this.eventHandlerFactory.getHandler(eventInfo.getUnionKey()) : this.eventHandlerFactory.getHandler(EventHandlerFactory.createUnionKey(eventInfo.getEventType()));
   }

   public void stop() {
      this.RUNNING = false;
   }
}
