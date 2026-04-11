package com.kuma.boot.canal.canalquick.event;

import com.alibaba.fastjson2.JSONObject;
import com.kuma.boot.canal.canalquick.parser.RowDataCustomParser;

public abstract class AbstractEventHandler implements IEventHandler {
   private RowDataCustomParser rowDataCustomParser;

   public AbstractEventHandler(RowDataCustomParser rowDataCustomParser) {
      if (rowDataCustomParser == null) {
         throw new RuntimeException("\u7f3a\u5c11\u884c\u6570\u636e\u89e3\u6790\u5668 !!!");
      } else {
         this.rowDataCustomParser = rowDataCustomParser;
      }
   }

   public void handle(EventInfo eventInfo) {
      JSONObject parsedData = this.rowDataCustomParser.parse(eventInfo);
      this.doHandle(parsedData);
   }

   public abstract void doHandle(JSONObject jsonObject);

   public RowDataCustomParser getRowDataCustomParser() {
      return this.rowDataCustomParser;
   }
}
