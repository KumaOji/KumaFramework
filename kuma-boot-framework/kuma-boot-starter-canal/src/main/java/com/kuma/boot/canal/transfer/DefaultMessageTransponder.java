package com.kuma.boot.canal.transfer;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.CanalEntry.EntryType;
import com.kuma.boot.canal.annotation.ListenPoint;
import com.kuma.boot.canal.autoconfigure.properties.CanalProperties;
import com.kuma.boot.canal.listener.CanalEventListener;
import com.kuma.boot.canal.model.CanalMsg;
import com.kuma.boot.canal.model.ListenerPoint;
import com.kuma.boot.common.utils.lang.StringUtils;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class DefaultMessageTransponder extends AbstractBasicMessageTransponder {
   public DefaultMessageTransponder(CanalConnector connector, Map.Entry<String, CanalProperties.Instance> config, List<CanalEventListener> listeners, List<ListenerPoint> annoListeners) {
      super(connector, config, listeners, annoListeners);
   }

   protected Predicate<Map.Entry<Method, ListenPoint>> getAnnotationFilter(String destination, String schemaName, String tableName, CanalEntry.EventType eventType) {
      Predicate<Map.Entry<Method, ListenPoint>> df = (e) -> StringUtils.isEmpty(((ListenPoint)e.getValue()).destination()) || ((ListenPoint)e.getValue()).destination().equals(destination) || destination == null;
      Predicate<Map.Entry<Method, ListenPoint>> sf = (e) -> ((ListenPoint)e.getValue()).schema().length == 0 || Arrays.asList(((ListenPoint)e.getValue()).schema()).contains(schemaName) || schemaName == null;
      Predicate<Map.Entry<Method, ListenPoint>> tf = (e) -> ((ListenPoint)e.getValue()).table().length == 0 || Arrays.asList(((ListenPoint)e.getValue()).table()).contains(tableName) || tableName == null;
      Predicate<Map.Entry<Method, ListenPoint>> ef = (e) -> ((ListenPoint)e.getValue()).eventType().length == 0 || Arrays.stream(((ListenPoint)e.getValue()).eventType()).anyMatch((ev) -> ev == eventType) || eventType == null;
      return df.and(sf).and(tf).and(ef);
   }

   protected Object[] getInvokeArgs(Method method, CanalMsg canalMsg, CanalEntry.RowChange rowChange) {
      return Arrays.stream(method.getParameterTypes()).map((p) -> p == CanalMsg.class ? canalMsg : (p == CanalEntry.RowChange.class ? rowChange : null)).toArray();
   }

   protected List<CanalEntry.EntryType> getIgnoreEntryTypes() {
      return Arrays.asList(EntryType.TRANSACTIONBEGIN, EntryType.TRANSACTIONEND, EntryType.HEARTBEAT);
   }
}
