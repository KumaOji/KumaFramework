//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.ddd.model.types;

import cn.hutool.core.util.IdUtil;
import com.kuma.boot.ddd.model.domain.Identifier;
import com.kuma.boot.ddd.util.validation.Validates;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class BizId extends Identifier<Long> {
   private @NotNull @Positive Long id;

   BizId() {
   }

   BizId(Long id) {
      this.id = id;
      this.validateSelf();
   }

   public Long getId() {
      return this.id;
   }

   public static BizId newBizId() {
      return new BizId(IdUtil.getSnowflakeNextId());
   }

   public static Set<Long> toValues(Collection<BizId> bizIds) {
      Validates.notEmpty(bizIds, "BizId list is empty");
      return (Set)bizIds.stream().map(BizId::getId).collect(Collectors.toSet());
   }

   public static BizId fromValue(Long id) {
      return new BizId(id);
   }

   public static Set<BizId> fromValues(Collection<Long> values) {
      Validates.notEmpty(values, "BizId value list is empty");
      return (Set)values.stream().map(BizId::new).collect(Collectors.toSet());
   }

   public static Set<BizId> fromNullableValues(Collection<Long> values) {
      return (Set<BizId>)(!Objects.isNull(values) && !values.isEmpty() ? (Set)values.stream().map(BizId::new).collect(Collectors.toSet()) : new HashSet());
   }

   public String toString() {
      return String.valueOf(this.id);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         BizId bizId = (BizId)o;
         return Objects.equals(this.id, bizId.id);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.id});
   }
}
