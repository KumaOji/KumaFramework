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
            throw new FenixException(StringHelper.format("\u3010Fenix \u5f02\u5e38\u3011\u901a\u8fc7\u53cd\u5c04\u521b\u5efa\u3010{}\u3011\u7c7b\u7684\u5bf9\u8c61\u5b9e\u4f8b\u5f02\u5e38\uff0c\u8bf7\u68c0\u67e5\u8be5\u7c7b\u7684\u6784\u9020\u65b9\u6cd5\u662f\u5426\u6709 public \u7684\u65e0\u53c2\u6784\u9020\u65b9\u6cd5\uff0c\u5efa\u8bae\u4f60\u53c2\u8003\u3010com.kuma.boot.data.jpa.fenix.jpa.FenixResultTransformer\u3011\u7c7b\u6765\u5b9e\u73b0\u81ea\u5df1\u7684 ResultTransformer \u7c7b\u3002", transformer.getName()), e);
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
            throw new FenixException("\u3010Fenix \u5f02\u5e38\u3011\u672a\u627e\u5230\u3010" + resultTypeClassStr + "\u3011\u5bf9\u5e94\u7684 class\uff0c\u8bf7\u68c0\u67e5\uff01", e);
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
