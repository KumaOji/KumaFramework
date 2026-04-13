package com.kuma.boot.data.jpa.autoconfigure;

import java.util.Map;
import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.event.internal.DefaultMergeEventListener;
import org.hibernate.event.spi.MergeContext;
import org.hibernate.jpa.event.spi.CallbackRegistry;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.property.access.internal.PropertyAccessStrategyBackRefImpl;
import org.hibernate.type.Type;
import org.hibernate.type.TypeHelper;

public class IgnoreNullMergeEventListener extends DefaultMergeEventListener {
   private static final long serialVersionUID = 1039333630147106744L;

   public IgnoreNullMergeEventListener() {
   }

   public IgnoreNullMergeEventListener(CallbackRegistry callbackRegistry) {
      super.injectCallbackRegistry(callbackRegistry);
   }

   protected void copyValues(EntityPersister persister, Object entity, Object target, SessionImplementor source, MergeContext copyCache) {
      if (entity == target) {
         TypeHelper.replace(persister, entity, source, entity, copyCache);
      } else {
         Object[] copiedValues = replace(persister.getValues(entity), persister.getValues(target), persister.getPropertyTypes(), source, target, copyCache);
         persister.setValues(target, copiedValues);
      }

   }

   public static Object[] replace(final Object[] original, final Object[] target, final Type[] types, final SharedSessionContractImplementor session, final Object owner, final Map<Object, Object> copyCache) {
      Object[] copied = new Object[original.length];

      for(int i = 0; i < types.length; ++i) {
         if (original[i] != null && original[i] != LazyPropertyInitializer.UNFETCHED_PROPERTY && original[i] != PropertyAccessStrategyBackRefImpl.UNKNOWN) {
            if (target[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY) {
               copied[i] = types[i].replace(original[i], (Object)null, session, owner, copyCache);
            } else {
               copied[i] = types[i].replace(original[i], target[i], session, owner, copyCache);
            }
         } else {
            copied[i] = target[i];
         }
      }

      return copied;
   }
}
