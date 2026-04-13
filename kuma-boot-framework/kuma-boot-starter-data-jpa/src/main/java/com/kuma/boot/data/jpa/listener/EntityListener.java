package com.kuma.boot.data.jpa.listener;

import com.kuma.boot.data.jpa.tenant.BaseEntity;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

public class EntityListener {
   public EntityListener() {
   }

   @PrePersist
   public void prePersist(Object object) {
      if (object instanceof BaseEntity) {
      }

   }

   @PostPersist
   public void postPersist(Object object) {
   }

   @PreUpdate
   public void preUpdate(Object object) {
      if (object instanceof BaseEntity) {
      }

   }

   @PostUpdate
   public void postUpdate(Object object) {
   }
}
