package com.kuma.boot.ddd.model.domain;

import com.kuma.boot.ddd.model.types.MarkerInterface;
import com.kuma.boot.ddd.util.validation.Validates;
import jakarta.validation.groups.Default;

public interface DomainModelValidate extends MarkerInterface {
   default void validateSelf() {
      Validates.validateEntity(this);
   }

   default void validateSelf(Class... groups) {
      Validates.validateEntity(this, groups);
   }

   default void validateSelfWithDefaultGroup(Class... groups) {
      Class<?>[] concatGroups = new Class[groups.length + 1];
      concatGroups[0] = Default.class;
      System.arraycopy(groups, 0, concatGroups, 1, concatGroups.length - 1);
      Validates.validateEntity(this, concatGroups);
   }
}
