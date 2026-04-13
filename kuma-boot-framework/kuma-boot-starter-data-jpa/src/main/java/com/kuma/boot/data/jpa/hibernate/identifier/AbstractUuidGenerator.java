package com.kuma.boot.data.jpa.hibernate.identifier;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.UUID;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.uuid.StandardRandomStrategy;
import org.hibernate.type.descriptor.java.UUIDJavaType;
import org.hibernate.type.descriptor.java.UUIDJavaType.PassThroughTransformer;
import org.hibernate.type.descriptor.java.UUIDJavaType.ToBytesTransformer;
import org.hibernate.type.descriptor.java.UUIDJavaType.ToStringTransformer;

public abstract class AbstractUuidGenerator implements IdentifierGenerator {
   private final StandardRandomStrategy generator;
   private final UUIDJavaType.ValueTransformer valueTransformer;

   public AbstractUuidGenerator(Member idMember) {
      this.generator = StandardRandomStrategy.INSTANCE;
      Class<?> propertyType;
      if (idMember instanceof Method) {
         propertyType = ((Method)idMember).getReturnType();
      } else {
         propertyType = ((Field)idMember).getType();
      }

      if (UUID.class.isAssignableFrom(propertyType)) {
         this.valueTransformer = PassThroughTransformer.INSTANCE;
      } else if (String.class.isAssignableFrom(propertyType)) {
         this.valueTransformer = ToStringTransformer.INSTANCE;
      } else {
         if (!byte[].class.isAssignableFrom(propertyType)) {
            throw new HibernateException("Unanticipated return type [" + propertyType.getName() + "] for UUID conversion");
         }

         this.valueTransformer = ToBytesTransformer.INSTANCE;
      }

   }

   public Object generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
      return this.valueTransformer.transform(this.generator.generateUuid(session));
   }
}
