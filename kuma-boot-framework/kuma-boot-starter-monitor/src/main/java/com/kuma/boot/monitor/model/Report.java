package com.kuma.boot.monitor.model;

import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.number.NumberUtils;
import com.kuma.boot.monitor.annotation.FieldReport;
import com.kuma.boot.monitor.collect.CollectInfo;
import com.kuma.boot.monitor.strategy.Rule;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Report extends LinkedHashMap<String, Object> implements Serializable {
   private String desc;
   private String name;

   public Report avgReport(List<Report> reportList) {
      Map<String, Object> sums = new HashMap();

      for(Report r : reportList) {
         this.eachReport(r, (fieldName, itemx) -> {
            Object value = itemx.value;
            if (sums.containsKey(fieldName)) {
               Object fieldValue = sums.get(fieldName);
               if (fieldValue instanceof Number && value instanceof Number) {
                  sums.replace(fieldName, ((Number)fieldValue).doubleValue() + ((Number)value).doubleValue());
               }
            } else if (value instanceof Number) {
               sums.put(fieldName, ((Number)value).doubleValue());
            }

            return itemx;
         });
      }

      for(Map.Entry<String, Object> item : sums.entrySet()) {
         Object value = item.getValue();
         if (value instanceof Number && !reportList.isEmpty()) {
            sums.replace((String)item.getKey(), NumberUtils.scale(((Number)value).doubleValue() / (double)reportList.size(), 2));
         }
      }

      Report report = ((Report)reportList.get(reportList.size() - 1)).clone();
      this.eachReport(report, (fieldName, itemx) -> {
         if (itemx.value instanceof Number) {
            itemx.setValue(sums.get(fieldName));
         }

         return itemx;
      });
      return report;
   }

   public String toHtml() {
      StringBuilder stringBuilder = new StringBuilder(String.format("<b>[%s(%s)]</b>\r\n", this.getName(), this.getDesc()));

      for(Map.Entry<String, Object> item : this.entrySet()) {
         Object value = item.getValue();
         if (value instanceof ReportItem reportItem) {
            Object itemValue = reportItem.getValue();
            if (Objects.nonNull(itemValue)) {
               if (itemValue instanceof Number) {
                  itemValue = NumberUtils.scale((Number)itemValue, 2);
               } else if (itemValue instanceof String) {
                  String text = StringUtils.nullToEmpty(itemValue);
                  if (((String)item.getKey()).contains(".detail") && !text.isEmpty()) {
                     itemValue = "<span style='color:blue;cursor:pointer' title='{title}' onclick='this.innerHTML=(this.textContent==\"\u663e\u793a\u8be6\u60c5\"?this.title.replace(/\\r/g,\"\").replace(/\\n/g,\"<br/>\"):\"\u663e\u793a\u8be6\u60c5\");'>\u663e\u793a\u8be6\u60c5</span>".replace("{title}", this.htmlEncode(text).replace("\r", "/r").replace("\n", "/n"));
                  }
               } else {
                  itemValue = JacksonUtils.toJSONString(itemValue);
               }
            } else {
               itemValue = "NULL";
            }

            stringBuilder.append(String.format("%s(%s):%s%s\r\n", item.getKey(), reportItem.getDesc(), itemValue, reportItem.isWarn() ? "<font color=\"#FF0000\">[\u62a5\u8b66]</font>" : ""));
         } else if (value instanceof Report) {
            stringBuilder.append(((Report)value).toHtml());
         }
      }

      stringBuilder.append("\r\n");
      return stringBuilder.toString();
   }

   public String htmlEncode(String source) {
      if (source == null) {
         return "";
      } else {
         StringBuilder buffer = new StringBuilder();

         for(int i = 0; i < source.length(); ++i) {
            char c = source.charAt(i);
            switch (c) {
               case '"':
                  buffer.append("&quot;");
                  break;
               case '&':
                  buffer.append("&amp;");
                  break;
               case '\'':
                  buffer.append("&apos;");
                  break;
               case '<':
                  buffer.append("&lt;");
                  break;
               case '>':
                  buffer.append("&gt;");
                  break;
               default:
                  buffer.append(c);
            }
         }

         return buffer.toString();
      }
   }

   public String toJson() {
      return JacksonUtils.toJSONString(this);
   }

   public void eachReport(ReportItemEachCallBack callBack) {
      this.eachReport(this, callBack);
   }

   public ReportItem getByKey(String key) {
      for(Map.Entry<String, Object> item : this.entrySet()) {
         Object value = item.getValue();
         String key1 = (String)item.getKey();
         if (key1.equals(key) && value instanceof ReportItem reportItem) {
            return reportItem;
         }

         if (value instanceof Report report) {
            ReportItem reportItem = report.getByKey(key);
            if (reportItem != null) {
               return reportItem;
            }
         }
      }

      return null;
   }

   public List<String> getKeys() {
      List<String> list = new ArrayList();

      for(Map.Entry<String, Object> item : this.entrySet()) {
         Object value = item.getValue();
         if (value instanceof ReportItem) {
            list.add((String)item.getKey());
         } else if (value instanceof Report) {
            Report report = (Report)value;

            for(Map.Entry<String, Object> stringObjectEntry : report.entrySet()) {
               Object value1 = stringObjectEntry.getValue();
               if (value1 instanceof ReportItem) {
                  list.add((String)stringObjectEntry.getKey());
               }
            }
         }
      }

      return list;
   }

   private void eachReport(Report report, ReportItemEachCallBack callBack) {
      for(Map.Entry<String, Object> item : report.entrySet()) {
         Object value = item.getValue();
         if (value instanceof ReportItem) {
            callBack.run((String)item.getKey(), (ReportItem)value);
         } else if (value instanceof Report) {
            this.eachReport((Report)value, callBack);
         }
      }

   }

   public Report(CollectInfo info) {
      this.parseObject(this, info);
   }

   private void parseObject(Report report, CollectInfo obj) {
      for(Field field : obj.getClass().getDeclaredFields()) {
         FieldReport fieldReport = (FieldReport)field.getAnnotation(FieldReport.class);
         if (fieldReport != null) {
            Object value = this.tryGet(field, obj);
            if (value != null) {
               if (value instanceof Number) {
                  report.put(fieldReport.name(), new ReportItem(fieldReport.desc(), value, "", (Rule.RuleInfo)null));
               } else if (value instanceof CollectInfo) {
                  Report report2 = (new Report()).setDesc(fieldReport.desc()).setName(fieldReport.name());
                  report.put(fieldReport.name(), report2);
                  this.parseObject(report2, (CollectInfo)value);
               } else {
                  report.put(fieldReport.name(), new ReportItem(fieldReport.desc(), JacksonUtils.toJSONString(value), "", (Rule.RuleInfo)null));
               }
            }
         }
      }

   }

   private Object tryGet(Field field, Object obj) {
      try {
         field.setAccessible(true);
         return field.get(obj);
      } catch (Exception var4) {
         return null;
      }
   }

   public Report() {
   }

   public Report clone() {
      Report report = (new Report()).setName(this.name).setDesc(this.desc);

      for(Map.Entry<String, Object> item : this.entrySet()) {
         Object value = item.getValue();
         if (value instanceof ReportItem) {
            report.put((String)item.getKey(), ((ReportItem)value).clone());
         } else if (value instanceof Report) {
            report.put((String)item.getKey(), ((Report)value).clone());
         }
      }

      return report;
   }

   public String getDesc() {
      return this.desc;
   }

   public Report setDesc(String desc) {
      this.desc = desc;
      return this;
   }

   public String getName() {
      return this.name;
   }

   public Report setName(String name) {
      this.name = name;
      return this;
   }

   public static class ReportItem implements Serializable {
      private String desc;
      private Object value;
      private String warn;
      private transient Rule.RuleInfo rule;

      public ReportItem(String desc, Object value, String warn, Rule.RuleInfo rule) {
         this.desc = desc;
         this.value = value;
         this.warn = warn;
         this.rule = rule;
      }

      public boolean isWarn() {
         return this.warn != null && !this.warn.isEmpty();
      }

      public String toString() {
         String var10000 = this.desc;
         return "ReportItem{desc='" + var10000 + "', value=" + String.valueOf(this.value) + ", warn='" + this.warn + "', rule=" + String.valueOf(this.rule) + "}";
      }

      public ReportItem clone() {
         return new ReportItem(this.desc, this.value, this.warn, this.rule);
      }

      public String getDesc() {
         return this.desc;
      }

      public void setDesc(String desc) {
         this.desc = desc;
      }

      public Object getValue() {
         return this.value;
      }

      public void setValue(Object value) {
         this.value = value;
      }

      public String getWarn() {
         return this.warn;
      }

      public void setWarn(String warn) {
         this.warn = warn;
      }

      public Rule.RuleInfo getRule() {
         return this.rule;
      }

      public void setRule(Rule.RuleInfo rule) {
         this.rule = rule;
      }
   }

   public interface ReportItemEachCallBack {
      ReportItem run(String field, ReportItem reportItem);
   }
}
