package com.kuma.boot.data.jpa.model;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.kuma.boot.data.jpa.base.entity.JpaSuperEntity;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public abstract class SelectBuilder<T extends Predicate> {
   public SelectBuilder() {
   }

   public static @NotNull SelectBooleanBuilder booleanBuilder() {
      return new SelectBooleanBuilder();
   }

   public static <I extends Serializable> @NotNull SelectBooleanBuilder booleanBuilder(JpaSuperEntity<I> entity) {
      BooleanBuilder builder = null;
      if (entity != null) {
         builder = entity.booleanBuilder();
      }

      return new SelectBooleanBuilder(builder);
   }

   public abstract @NotNull T getPredicate();
}
