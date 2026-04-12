package com.kuma.boot.ddd.model.types;

import com.kuma.boot.ddd.model.domain.ValueObject;
import java.math.BigDecimal;

public class Price implements ValueObject {
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
