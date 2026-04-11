package com.kuma.boot.canal.canalquick.event;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.kuma.boot.common.utils.context.ContextUtils;
import java.util.HashMap;
import java.util.Map;

public class EventHandlerFactory {
   private Map<String, IEventHandler> handlerMap = new HashMap();

   public EventHandlerFactory() {
   }

   public IEventHandler getHandler(String key) {
      return (IEventHandler)this.handlerMap.get(key);
   }

   public void setHandler(String key, IEventHandler iEventHandler) {
      this.handlerMap.put(key, iEventHandler);
   }

   public static String createUnionKey(TableInfo tableInfo, CanalEntry.EventType eventType) {
      return createUnionKey(tableInfo.getSchemaName(), tableInfo.getTableName(), eventType);
   }

   public static String createUnionKey(String schemaName, String tableName, CanalEntry.EventType eventType) {
      return schemaName + "-" + tableName + "-" + eventType.getValueDescriptor().getName();
   }

   public static String createUnionKey(CanalEntry.EventType eventType) {
      return createUnionKey("common_table", "common_table", eventType);
   }

   public void autoRegister() {
      Map<String, AbstractEventHandler> eventHandlerMap = ContextUtils.getBeansByType(AbstractEventHandler.class);

      for(Map.Entry<String, AbstractEventHandler> eventHandlerEntry : eventHandlerMap.entrySet()) {
         AbstractEventHandler eventHandler = (AbstractEventHandler)eventHandlerEntry.getValue();
         Class<? extends AbstractEventHandler> eventHandlerClass = eventHandler.getClass();
         TableEvent annotation = (TableEvent)eventHandlerClass.getAnnotation(TableEvent.class);
         if (annotation != null) {
            CanalEntry.EventType[] eventTypes = annotation.eventTypes();

            for(CanalEntry.EventType eventType : eventTypes) {
               this.setHandler(createUnionKey(annotation.schemaName(), annotation.tableName(), eventType), eventHandler);
            }
         }
      }

   }
}
