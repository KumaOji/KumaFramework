//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.ddd.model.types;

import com.kuma.boot.ddd.model.domain.ValueObject;

public class Count implements ValueObject<Count> {
   private final Integer value;

   Count(Integer value) {
      this.value = value;
      this.validateSelf();
   }

   public static Count of(Integer count) {
      return new Count(count);
   }

   public Integer getValue() {
      return this.value;
   }

   public boolean sameValueAs(Count other) {
      return false;
   }
}
