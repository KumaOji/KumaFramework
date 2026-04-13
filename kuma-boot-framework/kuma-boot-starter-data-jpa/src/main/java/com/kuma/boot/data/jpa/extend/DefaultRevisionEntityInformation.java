package com.kuma.boot.data.jpa.extend;

import org.hibernate.envers.DefaultRevisionEntity;
import org.springframework.data.repository.history.support.RevisionEntityInformation;

public class DefaultRevisionEntityInformation implements RevisionEntityInformation {
   public DefaultRevisionEntityInformation() {
   }

   public Class<?> getRevisionNumberType() {
      return Integer.class;
   }

   public boolean isDefaultRevisionEntity() {
      return true;
   }

   public Class<?> getRevisionEntityClass() {
      return DefaultRevisionEntity.class;
   }
}
