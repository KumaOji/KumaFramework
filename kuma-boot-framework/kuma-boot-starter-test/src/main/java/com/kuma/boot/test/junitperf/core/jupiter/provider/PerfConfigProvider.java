package com.kuma.boot.test.junitperf.core.jupiter.provider;

import com.kuma.boot.test.junitperf.core.annotation.KmcTest;
import com.kuma.boot.test.junitperf.core.jupiter.context.PerfConfigContext;
import java.util.stream.Stream;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.junit.platform.commons.support.AnnotationSupport;

@API(
   status = Status.INTERNAL
)
public class PerfConfigProvider implements TestTemplateInvocationContextProvider {
   public PerfConfigProvider() {
   }

   public boolean supportsTestTemplate(ExtensionContext context) {
      return context.getTestMethod().filter((m) -> AnnotationSupport.isAnnotated(m, KmcTest.class)).isPresent();
   }

   public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
      return Stream.of(new PerfConfigContext(context));
   }
}
