package com.kuma.boot.translation.translationextension.cache;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.google.common.base.Objects;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class TranslationCacheLocal {
   private static final ThreadLocal<Cache> THREAD_LOCAL = new TransmittableThreadLocal();

   public TranslationCacheLocal() {
   }

   public static void put(Cache cache) {
      THREAD_LOCAL.set(cache);
   }

   public static Cache get() {
      Cache cache = (Cache)Optional.ofNullable((Cache)THREAD_LOCAL.get()).orElse(new Cache());
      THREAD_LOCAL.set(cache);
      return cache;
   }

   public static void clear() {
      THREAD_LOCAL.remove();
   }

   public static class Cache {
      private final Set<DictItem> dictItems = new HashSet();
      private final Set<TableItem> tableItems = new HashSet();
      private final Set<DictItemValue> dictItemCaches = new HashSet();
      private final Set<TableItemValue> tableItemCaches = new HashSet();

      public Cache() {
      }

      public String getDictValue(String dicCode, String code) {
         return (String)this.dictItemCaches.stream().filter((o) -> Objects.equal(dicCode, o.getDictCode()) && Objects.equal(code, o.getCode())).findFirst().map(DictItemValue::getValue).orElse((Object)null);
      }

      public Object getTableValue(String table, String key, String param, String select) {
         return null;
      }

      public void addDict(String dictCode, String code) {
         this.dictItems.add(new DictItem(dictCode, code));
      }

      public void addTable(String table, String key, String param, String select) {
         this.tableItems.add(new TableItem(table, key, param, select));
      }

      public void addDictCache(String dictCode, String code, String value) {
         this.dictItemCaches.add(new DictItemValue(dictCode, code, value));
      }

      public void addTableCache(String table, String key, String param, String select, String value) {
         this.tableItemCaches.add(new TableItemValue(table, key, param, select, value));
      }

      public Set<DictItem> getDictItems() {
         return this.dictItems;
      }

      public Set<TableItem> getTableItems() {
         return this.tableItems;
      }

      public Set<DictItemValue> getDictItemCaches() {
         return this.dictItemCaches;
      }

      public Set<TableItemValue> getTableItemCaches() {
         return this.tableItemCaches;
      }
   }

   public static class DictItem {
      private String dictCode;
      private String code;

      public DictItem(String dictCode, String code) {
         this.dictCode = dictCode;
         this.code = code;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            DictItem dictItem = (DictItem)o;
            return java.util.Objects.equals(this.dictCode, dictItem.dictCode) && java.util.Objects.equals(this.code, dictItem.code);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return java.util.Objects.hash(new Object[]{this.dictCode, this.code});
      }

      public String getDictCode() {
         return this.dictCode;
      }

      public void setDictCode(String dictCode) {
         this.dictCode = dictCode;
      }

      public String getCode() {
         return this.code;
      }

      public void setCode(String code) {
         this.code = code;
      }
   }

   public static class DictItemValue extends DictItem {
      private final String value;

      public DictItemValue(String dictCode, String code, String value) {
         super(dictCode, code);
         this.value = value;
      }

      public String getValue() {
         return this.value;
      }
   }

   public static class TableItem {
      private String table;
      private String key;
      private Object param;
      private String select;

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            TableItem tableItem = (TableItem)o;
            return java.util.Objects.equals(this.table, tableItem.table) && java.util.Objects.equals(this.key, tableItem.key) && java.util.Objects.equals(this.param, tableItem.param) && java.util.Objects.equals(this.select, tableItem.select);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return java.util.Objects.hash(new Object[]{this.table, this.key, this.param, this.select});
      }

      public String getTable() {
         return this.table;
      }

      public void setTable(String table) {
         this.table = table;
      }

      public String getKey() {
         return this.key;
      }

      public void setKey(String key) {
         this.key = key;
      }

      public Object getParam() {
         return this.param;
      }

      public void setParam(Object param) {
         this.param = param;
      }

      public String getSelect() {
         return this.select;
      }

      public void setSelect(String select) {
         this.select = select;
      }

      public TableItem(String table, String key, Object param, String select) {
         this.table = table;
         this.key = key;
         this.param = param;
         this.select = select;
      }
   }

   public static class TableItemValue extends TableItem {
      private Object value;

      public TableItemValue(String table, String key, Object param, String select, Object value) {
         super(table, key, param, select);
         this.value = value;
      }

      public Object getValue() {
         return this.value;
      }

      public void setValue(Object value) {
         this.value = value;
      }
   }
}
