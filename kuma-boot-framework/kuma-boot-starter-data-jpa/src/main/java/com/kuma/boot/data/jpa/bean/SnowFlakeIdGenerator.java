package com.kuma.boot.data.jpa.bean;

import com.kuma.boot.common.utils.id.IdGeneratorUtils;
import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class SnowFlakeIdGenerator implements IdentifierGenerator {
   public SnowFlakeIdGenerator() {
   }

   public Serializable generate(SharedSessionContractImplementor session, Object o) throws HibernateException {
      return IdGeneratorUtils.getId();
   }
}
