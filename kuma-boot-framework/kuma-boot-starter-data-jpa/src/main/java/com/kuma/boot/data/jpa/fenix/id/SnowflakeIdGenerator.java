package com.kuma.boot.data.jpa.fenix.id;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class SnowflakeIdGenerator implements IdentifierGenerator {
   private static final IdWorker idWorker = new IdWorker();

   public SnowflakeIdGenerator() {
   }

   public Object generate(SharedSessionContractImplementor s, Object obj) {
      return idWorker.getId();
   }
}
