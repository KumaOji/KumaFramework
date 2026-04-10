package com.kuma.boot.test.kmc;

import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.test.context.ContextLoader;
import org.springframework.test.context.TestContextAnnotationUtils;

public class KmcTestContextBootstrapper extends SpringBootTestContextBootstrapper {
   public KmcTestContextBootstrapper() {
   }

   protected Class<? extends ContextLoader> getDefaultContextLoaderClass(Class<?> testClass) {
      return KmcSpringBootContextLoader.class;
   }

   protected String[] getProperties(Class<?> testClass) {
      KmcBootTest annotation = (KmcBootTest)TestContextAnnotationUtils.findMergedAnnotation(testClass, KmcBootTest.class);
      return annotation != null ? annotation.properties() : null;
   }
}
