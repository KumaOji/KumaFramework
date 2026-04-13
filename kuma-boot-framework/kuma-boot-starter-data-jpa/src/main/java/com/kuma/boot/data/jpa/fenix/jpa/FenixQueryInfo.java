package com.kuma.boot.data.jpa.fenix.jpa;

import com.kuma.boot.data.jpa.fenix.bean.SqlInfo;
import java.util.Map;

public final class FenixQueryInfo {
   private Map<String, Object> contextParams;
   private SqlInfo sqlInfo;
   private String querySql;
   private static final ThreadLocal<FenixQueryInfo> fenixThreadLocal = new ThreadLocal();

   public FenixQueryInfo() {
   }

   public static FenixQueryInfo getInstance() {
      FenixQueryInfo fenixQueryInfo = (FenixQueryInfo)fenixThreadLocal.get();
      if (fenixQueryInfo == null) {
         fenixQueryInfo = new FenixQueryInfo();
         fenixThreadLocal.set(fenixQueryInfo);
      }

      return fenixQueryInfo;
   }

   public static FenixQueryInfo getLocalThreadInstance() {
      return (FenixQueryInfo)fenixThreadLocal.get();
   }

   public void remove() {
      fenixThreadLocal.remove();
   }

   public Map<String, Object> getContextParams() {
      return this.contextParams;
   }

   public void setContextParams(Map<String, Object> contextParams) {
      this.contextParams = contextParams;
   }

   public SqlInfo getSqlInfo() {
      return this.sqlInfo;
   }

   public void setSqlInfo(SqlInfo sqlInfo) {
      this.sqlInfo = sqlInfo;
   }

   public String getQuerySql() {
      return this.querySql;
   }

   public void setQuerySql(String querySql) {
      this.querySql = querySql;
   }
}
