package com.kuma.boot.data.jpa.event;

import java.util.ArrayList;
import java.util.Collection;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;
import org.springframework.stereotype.Component;

@Component
public class AnAggregateRoot {
   public AnAggregateRoot() {
   }

   @DomainEvents
   public Collection<Object> domainEvents() {
      return new ArrayList();
   }

   @AfterDomainEventPublication
   public void callbackMethod() {
   }
}
