package com.kuma.boot.data.jpa.listener;

import com.alibaba.ttl.TransmittableThreadLocal;

public class SqlContextHolder {
   private static final ThreadLocal<String> SQL_CONTEXT = new TransmittableThreadLocal();

   private SqlContextHolder() {
   }

   public static void setSql(String sql) {
      SQL_CONTEXT.set(sql);
   }

   public static String getSql() {
      return (String)SQL_CONTEXT.get();
   }

   public static void clear() {
      SQL_CONTEXT.remove();
   }
}
