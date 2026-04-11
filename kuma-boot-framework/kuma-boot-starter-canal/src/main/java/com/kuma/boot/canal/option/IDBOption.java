package com.kuma.boot.canal.option;

import com.alibaba.otter.canal.protocol.CanalEntry;

@FunctionalInterface
public interface IDBOption {
   void doOption(String destination, String schemaName, String tableName, CanalEntry.RowChange rowChange);
}
