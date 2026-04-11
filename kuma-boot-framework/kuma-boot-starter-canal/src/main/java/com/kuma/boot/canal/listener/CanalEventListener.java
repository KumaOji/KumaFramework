package com.kuma.boot.canal.listener;

import com.alibaba.otter.canal.protocol.CanalEntry;

@FunctionalInterface
public interface CanalEventListener {
   void onEvent(String destination, String schemaName, String tableName, CanalEntry.RowChange rowChange);
}
