package com.kuma.boot.test.junitperf.core.jupiter.context;

import cn.hutool.core.date.DateUtil;
import com.kuma.boot.test.junitperf.core.annotation.KmcTest;
import com.kuma.boot.test.junitperf.core.annotation.KmcTestRequire;
import com.kuma.boot.test.junitperf.core.report.Reporter;
import com.kuma.boot.test.junitperf.core.statistics.StatisticsCalculator;
import com.kuma.boot.test.junitperf.model.evaluation.EvaluationContext;
import com.kuma.boot.test.junitperf.support.exception.JunitPerfRuntimeException;
import com.kuma.boot.test.junitperf.support.statements.PerformanceEvaluationStatement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;

@API(
   status = Status.INTERNAL,
   since = "2_0_0"
)
public class PerfConfigContext implements TestTemplateInvocationContext {
   private static final ConcurrentHashMap<Class<?>, List<EvaluationContext>> ACTIVE_CONTEXTS = new ConcurrentHashMap<>();
   private final Method method;
   private KmcTest perfConfig;
   private KmcTestRequire perfRequire;

   public PerfConfigContext(ExtensionContext context) {
      this.method = context.getRequiredTestMethod();
      this.perfConfig = (KmcTest)this.method.getAnnotation(KmcTest.class);
      this.perfRequire = (KmcTestRequire)this.method.getAnnotation(KmcTestRequire.class);
   }

   public List<Extension> getAdditionalExtensions() {
      return Collections.singletonList((TestInstancePostProcessor)(testInstance, context) -> {
         Class<?> clazz = testInstance.getClass();
         ACTIVE_CONTEXTS.putIfAbsent(clazz, new ArrayList<>());
         EvaluationContext evaluationContext = new EvaluationContext(testInstance, this.method, DateUtil.now().toString());
         evaluationContext.loadConfig(this.perfConfig);
         evaluationContext.loadRequire(this.perfRequire);
         StatisticsCalculator statisticsCalculator = (StatisticsCalculator)this.perfConfig.statistics().getDeclaredConstructor().newInstance();
         Set<Reporter> reporterSet = this.getReporterSet();
         ACTIVE_CONTEXTS.get(clazz).add(evaluationContext);

         try {
            (new PerformanceEvaluationStatement(evaluationContext, statisticsCalculator, reporterSet, ACTIVE_CONTEXTS.get(clazz), clazz)).evaluate();
         } catch (Throwable throwable) {
            throw new JunitPerfRuntimeException(throwable);
         }
      });
   }

   private Set<Reporter> getReporterSet() {
      Set<Reporter> reporterSet = new HashSet<>();
      Class<? extends Reporter>[] reporters = this.perfConfig.reporter();

      for(Class<? extends Reporter> clazz : reporters) {
         try {
            Reporter reporter = clazz.getDeclaredConstructor().newInstance();
            reporterSet.add(reporter);
         } catch (IllegalAccessException | InstantiationException e) {
            throw new JunitPerfRuntimeException(e);
         } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
         } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
         }
      }

      return reporterSet;
   }
}
