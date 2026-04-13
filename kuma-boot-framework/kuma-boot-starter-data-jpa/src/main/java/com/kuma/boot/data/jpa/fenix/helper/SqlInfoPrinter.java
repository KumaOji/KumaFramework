package com.kuma.boot.data.jpa.fenix.helper;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.jpa.fenix.bean.SqlInfo;

public final class SqlInfoPrinter {
   private static final String PRINT_START = "------------------------------------------------------------ Fenix \u751f\u6210\u7684 SQL \u4fe1\u606f ---------------------------------------------------------";
   private static final String PRINT_END = "-------------------------------------------------------------------------------------------------------------------------------------------";
   private static final String LINE_BREAK = "\n";
   private StringBuilder builder = (new StringBuilder("\n")).append("------------------------------------------------------------ Fenix \u751f\u6210\u7684 SQL \u4fe1\u606f ---------------------------------------------------------").append("\n");

   public SqlInfoPrinter() {
   }

   public void print(SqlInfo sqlInfo) {
      LogUtils.info(this.buildSqlAndParams(sqlInfo).builder.toString(), new Object[0]);
   }

   public void print(SqlInfo sqlInfo, String namespace, String fenixId) {
      LogUtils.info(this.buildXmlInfo(namespace, fenixId).buildSqlAndParams(sqlInfo).builder.toString(), new Object[0]);
   }

   private SqlInfoPrinter buildXmlInfo(String namespace, String fenixId) {
      this.builder.append("-- Fenix XML: ").append(namespace).append(".").append(fenixId).append("\n");
      return this;
   }

   private SqlInfoPrinter buildSqlAndParams(SqlInfo sqlInfo) {
      this.builder.append("-------- SQL: ").append(sqlInfo.getSql()).append("\n").append("----- Params: ").append(sqlInfo.getParams().toString()).append("\n").append("-------------------------------------------------------------------------------------------------------------------------------------------").append("\n");
      return this;
   }
}
