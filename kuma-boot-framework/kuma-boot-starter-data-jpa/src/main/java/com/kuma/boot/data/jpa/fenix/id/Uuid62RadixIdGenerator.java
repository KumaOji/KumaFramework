package com.kuma.boot.data.jpa.fenix.id;

import java.io.Serializable;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class Uuid62RadixIdGenerator implements IdentifierGenerator {
   public Uuid62RadixIdGenerator() {
   }

   public Serializable generate(SharedSessionContractImplementor s, Object obj) {
      return IdWorker.get62RadixUuid();
   }
}
