package com.kuma.boot.data.jpa.simplestjpa.interceptor;

import java.util.List;
import org.hibernate.resource.jdbc.spi.StatementInspector;

public class TenantInterceptor implements StatementInspector {
   private String tenantId;
   private List<String> tenantTables;
   private String tenantIdColumn = "tenant_id";

   public TenantInterceptor() {
   }

   public String inspect(String sql) {
      return sql;
   }
}
