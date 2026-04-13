package com.kuma.boot.data.jpa.fenix.id;

import java.io.Serializable;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class NanoIdGenerator implements IdentifierGenerator {
   public NanoIdGenerator() {
   }

   public Serializable generate(SharedSessionContractImplementor s, Object obj) {
      return IdWorker.getNanoId();
   }
}
