package com.kuma.boot.data.jpa.fenix.core.concrete;

import com.kuma.boot.data.jpa.fenix.bean.BuildSource;
import com.kuma.boot.data.jpa.fenix.bean.SqlInfo;
import com.kuma.boot.data.jpa.fenix.core.FenixHandler;
import com.kuma.boot.data.jpa.fenix.core.FenixXmlBuilder;

public class TrimWhereHandler implements FenixHandler {
   public TrimWhereHandler() {
   }

   public void buildSqlInfo(BuildSource source) {
      SqlInfo sqlInfo = source.getSqlInfo();
      sqlInfo.setPrependWhere(true);
      FenixXmlBuilder.buildSqlInfo(source.getNamespace(), sqlInfo, source.getNode(), source.getContext());
      if (sqlInfo.isPrependWhere()) {
         sqlInfo.setPrependWhere(false);
      }

   }
}
