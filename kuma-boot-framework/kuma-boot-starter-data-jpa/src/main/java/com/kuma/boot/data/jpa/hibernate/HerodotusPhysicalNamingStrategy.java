package com.kuma.boot.data.jpa.hibernate;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class HerodotusPhysicalNamingStrategy extends PhysicalNamingStrategyStandardImpl {
   public HerodotusPhysicalNamingStrategy() {
   }

   public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
      return new Identifier(name.getText(), true);
   }
}
