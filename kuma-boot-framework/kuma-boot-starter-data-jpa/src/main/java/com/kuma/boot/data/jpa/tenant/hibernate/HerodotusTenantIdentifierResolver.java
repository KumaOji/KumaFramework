package com.kuma.boot.data.jpa.tenant.hibernate;

import com.kuma.boot.common.holder.TenantContextHolder;
import java.util.Map;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

@Component
public class HerodotusTenantIdentifierResolver implements CurrentTenantIdentifierResolver, HibernatePropertiesCustomizer {
   private static final Logger log = LoggerFactory.getLogger(HerodotusTenantIdentifierResolver.class);

   public HerodotusTenantIdentifierResolver() {
   }

   public String resolveCurrentTenantIdentifier() {
      String currentTenantId = TenantContextHolder.getTenant();
      log.trace("[ttc] |- Resolve Current Tenant Identifier is : [{}]", currentTenantId);
      return currentTenantId;
   }

   public boolean validateExistingCurrentSessions() {
      return true;
   }

   public void customize(Map<String, Object> hibernateProperties) {
      log.debug("[ttc] |- Apply hibernate properties [MULTI_TENANT_IDENTIFIER_RESOLVER]");
      hibernateProperties.put("hibernate.tenant_identifier_resolver", this);
   }
}
