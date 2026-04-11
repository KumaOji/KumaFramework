package com.kuma.boot.canal.transfer;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange;
import com.kuma.boot.canal.annotation.ListenPoint;
import com.kuma.boot.canal.autoconfigure.properties.CanalProperties;
import com.kuma.boot.canal.exception.CanalClientException;
import com.kuma.boot.canal.listener.CanalEventListener;
import com.kuma.boot.canal.model.CanalMsg;
import com.kuma.boot.canal.model.ListenerPoint;
import com.kuma.boot.common.utils.log.LogUtils;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.springframework.util.CollectionUtils;

public abstract class AbstractBasicMessageTransponder extends AbstractMessageTransponder {
   public AbstractBasicMessageTransponder(CanalConnector connector, Map.Entry<String, CanalProperties.Instance> config, List<CanalEventListener> listeners, List<ListenerPoint> annoListeners) {
      super(connector, config, listeners, annoListeners);
   }

   protected void distributeEvent(Message message) {
      for(CanalEntry.Entry entry : message.getEntries()) {
         List<CanalEntry.EntryType> ignoreEntryTypes = this.getIgnoreEntryTypes();
         if (ignoreEntryTypes == null || !ignoreEntryTypes.stream().anyMatch((t) -> entry.getEntryType() == t)) {
            CanalEntry.RowChange rowChange;
            try {
               rowChange = RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
               throw new CanalClientException("\u9519\u8bef ##\u8f6c\u6362\u9519\u8bef , \u6570\u636e\u4fe1\u606f:" + entry.toString(), e);
            }

            this.distributeByAnnotation(this.destination, entry.getHeader().getSchemaName(), entry.getHeader().getTableName(), rowChange);
            this.distributeByImpl(this.destination, entry.getHeader().getSchemaName(), entry.getHeader().getTableName(), rowChange);
         }
      }

   }

   protected void distributeByAnnotation(String destination, String schemaName, String tableName, CanalEntry.RowChange rowChange) {
      if (!CollectionUtils.isEmpty(this.annotationListeners)) {
         this.annotationListeners.forEach((point) -> point.getInvokeMap().entrySet().stream().filter(this.getAnnotationFilter(destination, schemaName, tableName, rowChange.getEventType())).forEach((entry) -> {
               Method method = (Method)entry.getKey();
               method.setAccessible(true);

               try {
                  CanalMsg canalMsg = new CanalMsg();
                  canalMsg.setDestination(destination);
                  canalMsg.setSchemaName(schemaName);
                  canalMsg.setTableName(tableName);
                  Object[] args = this.getInvokeArgs(method, canalMsg, rowChange);
                  method.invoke(point.getTarget(), args);
               } catch (Exception var10) {
                  LogUtils.error("{}: \u59d4\u6258 canal \u76d1\u542c\u5668\u53d1\u751f\u9519\u8bef! \u9519\u8bef\u7c7b:{}, \u65b9\u6cd5\u540d:{}", new Object[]{Thread.currentThread().getName(), point.getTarget().getClass().getName(), method.getName()});
               }

            }));
      }

   }

   protected void distributeByImpl(String destination, String schemaName, String tableName, CanalEntry.RowChange rowChange) {
      if (this.implListeners != null) {
         for(CanalEventListener listener : this.implListeners) {
            listener.onEvent(destination, schemaName, tableName, rowChange);
         }
      }

   }

   protected abstract Predicate<Map.Entry<Method, ListenPoint>> getAnnotationFilter(String destination, String schemaName, String tableName, CanalEntry.EventType eventType);

   protected abstract Object[] getInvokeArgs(Method method, CanalMsg canalMsg, CanalEntry.RowChange rowChange);

   protected List<CanalEntry.EntryType> getIgnoreEntryTypes() {
      return Collections.emptyList();
   }
}
