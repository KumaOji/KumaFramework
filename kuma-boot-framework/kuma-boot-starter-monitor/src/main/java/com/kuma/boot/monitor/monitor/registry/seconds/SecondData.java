package com.kuma.boot.monitor.monitor.registry.seconds;

import com.kuma.boot.monitor.monitor.monitor.utils.CacheMap;
import java.util.concurrent.ConcurrentHashMap;

public class SecondData {
   private long time;
   private ConcurrentHashMap<String, SourceData> data;
   private CacheMap<String, ConcurrentHashMap<String, SourceData>> tagData;

   public SecondData(long time) {
      this.time = time;
      this.data = new ConcurrentHashMap();
      this.tagData = new CacheMap<String, ConcurrentHashMap<String, SourceData>>((k) -> new ConcurrentHashMap());
   }

   public SourceData get(String key) {
      return (SourceData)this.data.get(key);
   }

   public SourceData get(String key, String tag) {
      return tag != null && !"".equals(tag) ? (SourceData)((ConcurrentHashMap)this.tagData.get(key)).get(tag) : (SourceData)this.data.get(key);
   }

   public SourceData put(String key, SourceData value) {
      this.data.putIfAbsent(key, value);
      return this.get(key);
   }

   public SourceData put(String key, String tag, SourceData value) {
      if (tag != null && !"".equals(tag)) {
         ((ConcurrentHashMap)this.tagData.get(key)).putIfAbsent(tag, value);
      } else {
         this.data.putIfAbsent(key, value);
      }

      return this.get(key, tag);
   }

   public long getTime() {
      return this.time;
   }

   public void setTime(long time) {
      this.time = time;
   }

   public ConcurrentHashMap<String, SourceData> getData() {
      return this.data;
   }

   public void setData(ConcurrentHashMap<String, SourceData> data) {
      this.data = data;
   }

   public CacheMap<String, ConcurrentHashMap<String, SourceData>> getTagData() {
      return this.tagData;
   }

   public void setTagData(CacheMap<String, ConcurrentHashMap<String, SourceData>> tagData) {
      this.tagData = tagData;
   }
}
