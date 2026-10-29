package com.kuma.boot.data.jpa.fenix.jpa;

import com.kuma.boot.data.jpa.fenix.exception.FenixException;
import com.kuma.boot.data.jpa.fenix.helper.StringHelper;
import com.kuma.boot.data.jpa.fenix.jpa.transformer.ColumnAnnotationTransformer;
import com.kuma.boot.data.jpa.fenix.jpa.transformer.PrefixUnderscoreTransformer;
import com.kuma.boot.data.jpa.fenix.jpa.transformer.UnderscoreTransformer;
import jakarta.persistence.Query;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.hibernate.query.NativeQuery;

final class QueryResultContext {
   private static final Map<String, Supplier<AbstractResultTransformer>> buildInTransformerMap = new HashMap(8);

   QueryResultContext() {
   }

   static Query buildTransformer(Query query, String resultTypeClassStr, QueryFenix queryFenix) {
      AbstractResultTransformer transformer = newTransformerInstance(queryFenix);
      transformer.setResultClass(getResultTypeClass(queryFenix.resultType(), resultTypeClassStr));
      transformer.init();
      if (queryFenix.nativeQuery()) {
         ((NativeQuery)query.unwrap(NativeQuery.class)).setResultTransformer(transformer);
      } else {
         ((org.hibernate.query.Query)query.unwrap(org.hibernate.query.Query.class)).setResultTransformer(transformer);
      }

      return query;
   }

   private static AbstractResultTransformer newTransformerInstance(QueryFenix queryFenix) {
      Class<? extends AbstractResultTransformer> transformer = queryFenix.resultTransformer();
      Supplier<AbstractResultTransformer> transformerSupplier = (Supplier)buildInTransformerMap.get(transformer.getName());
      if (transformerSupplier != null) {
         return (AbstractResultTransformer)transformerSupplier.get();
      } else {
         try {
            return (AbstractResultTransformer)transformer.getDeclaredConstructor().newInstance();
         } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
            throw new FenixException(StringHelper.format("【Fenix 异常】通过反射创建【{}】类的对象实例异常，请检查该类的构造方法是否有 public 的无参构造方法，建议你参考【com.kuma.boot.data.jpa.fenix.jpa.FenixResultTransformer】类来实现自己的 ResultTransformer 类。", transformer.getName()), e);
         }
      }
   }

   private static Class<?> getResultTypeClass(Class<?> resultTypeClass, String resultTypeClassStr) {
      if (resultTypeClass != null && resultTypeClass != Void.class) {
         return resultTypeClass;
      } else {
         try {
            return Class.forName(resultTypeClassStr);
         } catch (ClassNotFoundException e) {
            throw new FenixException("【Fenix 异常】未找到【" + resultTypeClassStr + "】对应的 class，请检查！", e);
         }
      }
   }

   static {
      buildInTransformerMap.put(FenixResultTransformer.class.getName(), FenixResultTransformer::new);
      buildInTransformerMap.put(UnderscoreTransformer.class.getName(), UnderscoreTransformer::new);
      buildInTransformerMap.put(PrefixUnderscoreTransformer.class.getName(), PrefixUnderscoreTransformer::new);
      buildInTransformerMap.put(ColumnAnnotationTransformer.class.getName(), ColumnAnnotationTransformer::new);
   }
}
