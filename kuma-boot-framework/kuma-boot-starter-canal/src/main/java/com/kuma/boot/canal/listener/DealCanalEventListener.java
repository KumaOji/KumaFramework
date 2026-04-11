package com.kuma.boot.canal.listener;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.kuma.boot.canal.option.AbstractDBOption;
import java.util.List;
import org.springframework.util.CollectionUtils;

public class DealCanalEventListener implements CanalEventListener {
   private AbstractDBOption header;

   public DealCanalEventListener(AbstractDBOption... dbOptions) {
      AbstractDBOption tmp = null;

      for(AbstractDBOption dbOption : dbOptions) {
         if (tmp != null) {
            tmp.setNext(dbOption);
         } else {
            this.header = dbOption;
         }

         tmp = dbOption;
      }

   }

   public DealCanalEventListener(List<AbstractDBOption> dbOptions) {
      if (!CollectionUtils.isEmpty(dbOptions)) {
         AbstractDBOption tmp = null;

         for(AbstractDBOption dbOption : dbOptions) {
            if (tmp != null) {
               tmp.setNext(dbOption);
            } else {
               this.header = dbOption;
            }

            tmp = dbOption;
         }

      }
   }

   public void onEvent(String destination, String schemaName, String tableName, CanalEntry.RowChange rowChange) {
      this.header.doChain(destination, schemaName, tableName, rowChange);
   }
}
