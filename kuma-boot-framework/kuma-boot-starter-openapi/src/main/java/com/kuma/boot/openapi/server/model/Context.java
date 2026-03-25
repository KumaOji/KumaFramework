package com.kuma.boot.openapi.server.model;

import java.util.Collection;
import org.springframework.stereotype.Component;

@Component
public class Context {
   private Collection apiHandlers;

   public Collection getApiHandlers() {
      return this.apiHandlers;
   }

   public void setApiHandlers(Collection apiHandlers) {
      this.apiHandlers = apiHandlers;
   }
}
