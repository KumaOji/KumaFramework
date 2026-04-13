package com.kuma.boot.data.jpa.model;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.SetPath;
import com.querydsl.core.types.dsl.StringPath;
import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.function.Consumer;

public class SelectBooleanBuilder extends SelectBuilder<BooleanBuilder> {
   private final BooleanBuilder builder;

   public SelectBooleanBuilder() {
      this.builder = new BooleanBuilder();
   }

   public SelectBooleanBuilder(BooleanBuilder builder) {
      this.builder = builder == null ? new BooleanBuilder() : builder;
   }

   public BooleanBuilder getPredicate() {
      return this.builder;
   }

   public SelectBooleanBuilder and(@Nullable Predicate right) {
      this.builder.and(right);
      return this;
   }

   public SelectBooleanBuilder andAnyOf(Predicate... args) {
      this.builder.andAnyOf(args);
      return this;
   }

   public SelectBooleanBuilder andNot(Predicate right) {
      return this.and(right.not());
   }

   public SelectBooleanBuilder or(@Nullable Predicate right) {
      this.builder.or(right);
      return this;
   }

   public SelectBooleanBuilder orAllOf(Predicate... args) {
      this.builder.orAllOf(args);
      return this;
   }

   public SelectBooleanBuilder orNot(Predicate right) {
      return this.or(right.not());
   }

   public SelectBooleanBuilder notNullEq(Boolean param, BooleanPath path) {
      if (param != null) {
         this.builder.and(path.eq(param));
      }

      return this;
   }

   public <T extends Number & Comparable<?>> SelectBooleanBuilder notNullEq(T param, NumberPath<T> path) {
      if (param != null) {
         this.builder.and(path.eq(param));
      }

      return this;
   }

   public SelectBooleanBuilder isIdEq(Long param, NumberPath<Long> path) {
      if (param != null && param > 0L) {
         this.builder.and(path.eq(param));
      }

      return this;
   }

   public <T extends Number & Comparable<?>> SelectBooleanBuilder leZeroIsNull(T param, NumberPath<T> path) {
      if (param != null && param.longValue() <= 0L) {
         this.builder.and(path.isNull());
      }

      return this;
   }

   public SelectBooleanBuilder notBlankEq(String param, StringPath path) {
      if (StringUtils.isNotBlank(param)) {
         this.builder.and(path.eq(param));
      }

      return this;
   }

   public SelectBooleanBuilder with(@NotNull Consumer<SelectBooleanBuilder> consumer) {
      if (consumer != null) {
         consumer.accept(this);
      }

      return this;
   }

   public <T extends Enum<T>> SelectBooleanBuilder notNullEq(T param, EnumPath<T> path) {
      if (param != null) {
         this.builder.and(path.eq(param));
      }

      return this;
   }

   public SelectBooleanBuilder notBlankContains(String param, StringPath path) {
      if (StringUtils.isNoneBlank(new CharSequence[]{param})) {
         this.builder.and(path.contains(param));
      }

      return this;
   }

   public SelectBooleanBuilder notNullBefore(LocalDateTime param, DateTimePath<LocalDateTime> path) {
      if (param != null) {
         this.builder.and(path.before(param));
      }

      return this;
   }

   public SelectBooleanBuilder notNullAfter(LocalDateTime param, DateTimePath<LocalDateTime> path) {
      if (param != null) {
         this.builder.and(path.after(param));
      }

      return this;
   }

   public SelectBooleanBuilder notEmptyIn(Collection<? extends Long> param, NumberPath<Long> path) {
      if (CollectionUtils.isNotEmpty(param)) {
         this.builder.and(path.in(param));
      }

      return this;
   }

   public SelectBooleanBuilder findInSet(Long param, SetPath<Long, NumberPath<Long>> path) {
      if (param != null) {
         this.builder.and(Expressions.booleanTemplate("FIND_IN_SET({0}, {1}) > 0", new Object[]{param, path}));
      }

      return this;
   }
}
