package com.kuma.boot.canal.transfer;

import com.alibaba.otter.canal.client.CanalConnector;
import com.kuma.boot.canal.autoconfigure.properties.CanalProperties;
import com.kuma.boot.canal.listener.CanalEventListener;
import com.kuma.boot.canal.model.ListenerPoint;
import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface TransponderFactory {
   MessageTransponder newTransponder(CanalConnector connector, Map.Entry<String, CanalProperties.Instance> config, List<CanalEventListener> listeners, List<ListenerPoint> annoListeners);
}
