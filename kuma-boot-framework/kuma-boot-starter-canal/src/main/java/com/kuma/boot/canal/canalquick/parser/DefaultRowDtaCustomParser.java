package com.kuma.boot.canal.canalquick.parser;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;
import com.kuma.boot.canal.canalquick.event.EventInfo;
import com.kuma.boot.common.utils.lang.StringUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DefaultRowDtaCustomParser implements RowDataCustomParser {
   public DefaultRowDtaCustomParser() {
   }

   public JSONObject parse(EventInfo eventInfo) {
      if (eventInfo == null) {
         return null;
      } else {
         CanalEntry.RowData rowData = eventInfo.getRowData();
         if (rowData == null) {
            return null;
         } else {
            CanalEntry.EventType eventType = eventInfo.getEventType();
            if (EventType.INSERT == eventType) {
               Map<String, String> afterColumnMap = this.flat2Map(rowData.getAfterColumnsList());
               return this.addOriginalColumn(this.doParse(eventInfo, afterColumnMap), (Map)null, afterColumnMap);
            } else if (EventType.UPDATE != eventType) {
               if (EventType.DELETE == eventType) {
                  Map<String, String> beforeColumnMap = this.flat2Map(rowData.getBeforeColumnsList());
                  return this.addOriginalColumn(this.doParse(eventInfo, beforeColumnMap), beforeColumnMap, (Map)null);
               } else {
                  return null;
               }
            } else {
               List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
               Map<String, String> afterColumnMap = this.flat2Map(afterColumnsList);
               String deleteFlag = (String)afterColumnMap.get("delete_flag");
               if (1 == Integer.parseInt(deleteFlag)) {
                  Map<String, String> beforeColumnMap = this.flat2Map(rowData.getBeforeColumnsList());
                  return this.addOriginalColumn(this.doParse(eventInfo, beforeColumnMap, EventType.DELETE), beforeColumnMap, (Map)null);
               } else {
                  Map<String, String> updatedColumn = this.flat2Map(afterColumnsList.stream().filter(CanalEntry.Column::getUpdated).toList());
                  if (StringUtils.isEmpty(updatedColumn)) {
                     return null;
                  } else {
                     Map<String, String> beforeColumnMap = this.flat2Map(rowData.getBeforeColumnsList());
                     Map<String, Map<String, String>> changedData = new HashMap(updatedColumn.size());

                     for(Map.Entry<String, String> nameValueEntry : updatedColumn.entrySet()) {
                        String columnName = (String)nameValueEntry.getKey();
                        Map<String, String> beforeAfterMap = new HashMap(2);
                        beforeAfterMap.put("before", (String)beforeColumnMap.get(columnName));
                        beforeAfterMap.put("after", (String)nameValueEntry.getValue());
                        changedData.put(columnName, beforeAfterMap);
                     }

                     return this.addOriginalColumn(this.doParse(eventInfo, changedData), beforeColumnMap, afterColumnMap);
                  }
               }
            }
         }
      }
   }

   private JSONObject doParse(EventInfo eventInfo, Object columnData) {
      return this.doParse(eventInfo, columnData, (CanalEntry.EventType)null);
   }

   private JSONObject doParse(EventInfo eventInfo, Object columnData, CanalEntry.EventType overrideEventTyp) {
      if (columnData == null) {
         return null;
      } else {
         JSONObject parsedJSON = new JSONObject();
         parsedJSON.put("schemaName", eventInfo.getTableInfo().getSchemaName());
         parsedJSON.put("tableName", eventInfo.getTableInfo().getTableName());
         if (overrideEventTyp == null) {
            parsedJSON.put("evenType", eventInfo.getEventType().name().toLowerCase());
         } else {
            parsedJSON.put("evenType", overrideEventTyp.name().toLowerCase());
         }

         parsedJSON.put("changedColumn", columnData);
         return parsedJSON;
      }
   }

   private JSONObject addOriginalColumn(JSONObject parsedJSON, Map<String, String> beforeColumnMap, Map<String, String> afterColumnMap) {
      if (parsedJSON == null) {
         return null;
      } else {
         if (StringUtils.isNotEmpty(beforeColumnMap)) {
            parsedJSON.put("beforeColumn", beforeColumnMap);
         }

         if (StringUtils.isNotEmpty(afterColumnMap)) {
            parsedJSON.put("afterColumn", afterColumnMap);
         }

         return parsedJSON;
      }
   }

   private Map<String, String> flat2Map(List<CanalEntry.Column> columns) {
      return StringUtils.isEmpty(columns) ? null : (Map)columns.stream().collect(Collectors.toMap(CanalEntry.Column::getName, CanalEntry.Column::getValue));
   }
}
