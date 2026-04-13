package com.kuma.boot.data.jpa.tenant;

public interface DataConstants extends BaseConstants {
   String AREA_PREFIX = "data:core:";
   String PROPERTY_PREFIX_MULTI_TENANT = "herodotus.multi-tenant";
   String ITEM_SPRING_SQL_INIT_PLATFORM = "spring.sql.init.platform";
   String ITEM_MULTI_TENANT_APPROACH = "herodotus.multi-tenant.approach";
   String ANNOTATION_SQL_INIT_PLATFORM = "${spring.sql.init.platform}";
   String REGION_SYS_TENANT_DATASOURCE = "data:core:sys:tenant:datasource";
}
