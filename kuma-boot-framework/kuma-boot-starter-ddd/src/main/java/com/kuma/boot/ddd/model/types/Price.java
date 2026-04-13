//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.ddd.model.types;

import com.kuma.boot.ddd.model.domain.ValueObject;
import java.math.BigDecimal;

public class Price implements ValueObject<Price> {
   private final BigDecimal value;

   Price(BigDecimal value) {
      this.value = value;
      this.validateSelf();
   }

   public static Price of(BigDecimal price) {
      return new Price(price);
   }

   public BigDecimal getValue() {
      return this.value;
   }

   public boolean sameValueAs(Price other) {
      return false;
   }
}
