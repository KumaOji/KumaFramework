package com.kuma.boot.data.jpa.fenix.jpa;

import com.kuma.boot.common.utils.log.LogUtils;
import java.util.concurrent.atomic.AtomicBoolean;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;

public class FenixJpaClassWriter {
   private static final String JPA_METHOD_FACTORY_NAME = "org.springframework.data.jpa.repository.query.DefaultJpaQueryMethodFactory";
   private static Boolean hasJpaMethodClass;
   private static final AtomicBoolean modified = new AtomicBoolean(false);

   public FenixJpaClassWriter() {
   }

   public static synchronized boolean hasDefaultJpaQueryMethodFactoryClass() {
      if (hasJpaMethodClass != null) {
         return hasJpaMethodClass;
      } else {
         try {
            Thread.currentThread().getContextClassLoader().loadClass("org.springframework.data.jpa.repository.query.DefaultJpaQueryMethodFactory");
            hasJpaMethodClass = true;
         } catch (ClassNotFoundException var1) {
            LogUtils.debug("\u3010Fenix -> 'JPA \u7248\u672c\u68c0\u6d4b' \u63d0\u793a\u3011\u68c0\u67e5\u5230\u4f60\u7684\u9879\u76ee\u4e2d\u6ca1\u6709\u3010{}\u3011\u7c7b\uff0c\u8bf4\u660e\u4f60\u7684 Spring Data JPA \u7248\u672c\u662f v2.3.0 \u4e4b\u524d\u7684\u7248\u672c.", new Object[]{"org.springframework.data.jpa.repository.query.DefaultJpaQueryMethodFactory"});
            hasJpaMethodClass = false;
         } catch (Exception e) {
            if (LogUtils.isDebugEnabled()) {
               LogUtils.debug("\u3010Fenix -> 'JPA \u7248\u672c\u68c0\u6d4b' \u63d0\u793a\u3011\u68c0\u67e5\u4f60\u7684\u9879\u76ee\u4e2d\u662f\u5426\u6709\u3010{}\u3011\u7c7b\u65f6\u51fa\u9519\uff0c\u5c06\u9ed8\u8ba4\u4f60\u7684 Spring Data JPA \u7248\u672c\u662f v2.3.0 \u4e4b\u524d\u7684\u7248\u672c.", new Object[]{"org.springframework.data.jpa.repository.query.DefaultJpaQueryMethodFactory", e});
            } else {
               LogUtils.error("\u3010Fenix -> 'JPA \u7248\u672c\u68c0\u6d4b' \u9519\u8bef\u3011\u68c0\u67e5\u4f60\u7684\u9879\u76ee\u4e2d\u662f\u5426\u6709\u3010{}\u3011\u7c7b\u65f6\u51fa\u9519\uff0c\u5c06\u9ed8\u8ba4\u4f60\u7684 Spring Data JPA \u7248\u672c\u662f v2.3.0 \u4e4b\u524d\u7684\u7248\u672c\uff0c\u68c0\u6d4b\u65f6\u7684\u51fa\u9519\u539f\u56e0\u662f\uff1a\u3010{}\u3011\uff0c\u82e5\u60f3\u770b\u66f4\u5168\u7684\u9519\u8bef\u5806\u6808\u65e5\u5fd7\u4fe1\u606f\uff0c\u8bf7\u5f00\u542f debug \u65e5\u5fd7\u7ea7\u522b.", new Object[]{"org.springframework.data.jpa.repository.query.DefaultJpaQueryMethodFactory", e.getMessage()});
            }

            hasJpaMethodClass = false;
         }

         return hasJpaMethodClass;
      }
   }

   public static synchronized void modify() {
      if (hasDefaultJpaQueryMethodFactoryClass()) {
         LogUtils.debug("\u3010Fenix \u63d0\u793a\u3011\u68c0\u6d4b\u5230\u4f60\u7684 Spring Data JPA \u7248\u672c\u662f v2.3.0 \u53ca\u4ee5\u4e0a\uff0c\u53ef\u4e0d\u7528\u4fee\u6539 class \u6765\u517c\u5bb9\u8001\u7248\u672c\u7684 JPA.", new Object[0]);
      } else if (modified.get()) {
         LogUtils.debug("\u3010Fenix \u63d0\u793a\u3011\u5df2\u7ecf\u4fee\u6539\u8fc7\u4e86\u3010FenixQueryLookupStrategy.class\u3011\u4e2d\u7684\u90e8\u5206\u65b9\u6cd5\uff0c\u5c06\u4e0d\u518d\u4fee\u6539.", new Object[0]);
      } else {
         LogUtils.info("\u3010Fenix \u63d0\u793a\u3011\u68c0\u6d4b\u5230\u4f60\u7684 Spring Data JPA \u7248\u672c\u8f83\u4f4e\uff0c\u4e3a\u4e86\u517c\u5bb9\u8001\u7248\u672c\u7684 JPA\uff0c\u5c06\u4fee\u6539\u90e8\u5206 class \u5b57\u8282\u7801\u6765\u505a\u517c\u5bb9\u3002\u4e0d\u8fc7\u6761\u4ef6\u5141\u8bb8\u7684\u8bdd\uff0c\u6211\u4ecd\u7136\u5efa\u8bae\u4f60\u5c06 Spring Data JPA \u7248\u672c\u5347\u7ea7\u5230 v2.3.0 \u53ca\u4e4b\u540e\u7684\u7248\u672c.", new Object[0]);

         try {
            ClassPool classPool = ClassPool.getDefault();
            classPool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
            CtClass ctClass = classPool.get("com.kuma.boot.data.jpa.fenix.jpa.FenixQueryLookupStrategy");
            CtMethod lookupStrategyMethod = ctClass.getDeclaredMethod("createOldJpaQueryLookupStrategy");
            lookupStrategyMethod.setBody("{return org.springframework.data.jpa.repository.query.JpaQueryLookupStrategy.create($1, $2, $3, $4, $5);}");
            CtMethod ctMethod = ctClass.getDeclaredMethod("createOldFenixJpaQuery");
            ctMethod.setBody("{return new com.kuma.boot.data.jpa.fenix.jpa.FenixJpaQuery(new org.springframework.data.jpa.repository.query.JpaQueryMethod($1, $2, $3, $4), $5);}");
            ctClass.toClass();
            modified.getAndSet(true);
         } catch (Exception e) {
            LogUtils.error("\u3010Fenix \u9519\u8bef\u63d0\u793a\u3011\u4f7f\u7528 Javassist \u4fee\u6539\u3010FenixQueryLookupStrategy\u3011class \u4e2d\u7684\u4ee3\u7801\u51fa\u9519\uff0c\u5efa\u8bae\u5347\u7ea7 Spring Boot \u7684\u7248\u672c\u4e3a v2.3.0 \u53ca\u4e4b\u4e0a.", new Object[]{e});
         }

      }
   }
}
