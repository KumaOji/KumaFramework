package com.kuma.boot.test.junitperf.core.annotation;

import com.kuma.boot.test.junitperf.core.jupiter.provider.PerfConfigProvider;
import com.kuma.boot.test.junitperf.core.report.Reporter;
import com.kuma.boot.test.junitperf.core.report.impl.ConsoleReporter;
import com.kuma.boot.test.junitperf.core.statistics.StatisticsCalculator;
import com.kuma.boot.test.junitperf.core.statistics.impl.DefaultStatisticsCalculator;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Documented
@API(
   status = Status.MAINTAINED,
   since = "2_0_0"
)
@ExtendWith({PerfConfigProvider.class})
@TestTemplate
public @interface KmcTest {
   int threads() default 1;

   long warmUp() default 0L;

   long duration() default 60000L;

   Class<? extends StatisticsCalculator> statistics() default DefaultStatisticsCalculator.class;

   Class<? extends Reporter>[] reporter() default {ConsoleReporter.class};
}
