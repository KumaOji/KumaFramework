package com.kuma.boot.openapi.server.model;

import java.util.Collection;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "kuma.boot.openapi", name = "enabled", havingValue = "true", matchIfMissing = false)
public class Context {
   private Collection apiHandlers;

   public Collection getApiHandlers() {
      return this.apiHandlers;
   }

   public void setApiHandlers(Collection apiHandlers) {
      this.apiHandlers = apiHandlers;
   }
}
