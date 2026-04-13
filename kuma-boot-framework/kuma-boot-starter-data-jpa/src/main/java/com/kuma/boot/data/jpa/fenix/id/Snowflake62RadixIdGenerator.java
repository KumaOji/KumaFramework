package com.kuma.boot.data.jpa.fenix.id;

import java.io.Serializable;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class Snowflake62RadixIdGenerator implements IdentifierGenerator {
   private static final IdWorker idWorker = new IdWorker();

   public Snowflake62RadixIdGenerator() {
   }

   public Serializable generate(SharedSessionContractImplementor s, Object obj) {
      return idWorker.get62RadixId();
   }
}
