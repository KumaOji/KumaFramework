package com.kuma.boot.translation.translationextension.domain;

import com.kuma.boot.translation.translationextension.cache.TranslationCacheLocal;
import java.util.HashSet;
import java.util.Set;

public class Cache {
   private Set<TranslationCacheLocal.DictItem> dictItems = new HashSet();
   private Set<TranslationCacheLocal.TableItem> tableItems = new HashSet();

   public Cache() {
   }

   public String getDictValue() {
      return null;
   }

   public void addDict(String dictCode, String code) {
      this.dictItems.add(new TranslationCacheLocal.DictItem(dictCode, code));
   }

   public void addTable(String table, String key, String param, String select) {
      this.tableItems.add(new TranslationCacheLocal.TableItem(table, key, param, select));
   }

   public Set<TranslationCacheLocal.DictItem> getDictItems() {
      return this.dictItems;
   }

   public void setDictItems(Set<TranslationCacheLocal.DictItem> dictItems) {
      this.dictItems = dictItems;
   }

   public Set<TranslationCacheLocal.TableItem> getTableItems() {
      return this.tableItems;
   }

   public void setTableItems(Set<TranslationCacheLocal.TableItem> tableItems) {
      this.tableItems = tableItems;
   }
}
