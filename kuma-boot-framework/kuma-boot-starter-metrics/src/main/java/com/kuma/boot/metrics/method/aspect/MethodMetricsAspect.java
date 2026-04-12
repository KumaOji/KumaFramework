package com.kuma.boot.metrics.method.aspect;

import com.kuma.boot.metrics.method.annotation.TimedMethod;
import com.kuma.boot.metrics.method.autoconfigure.MethodMetricsProperties;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import java.lang.reflect.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

@Aspect
public class MethodMetricsAspect {
   private static final Logger logger = LoggerFactory.getLogger(MethodMetricsAspect.class);
   private final MeterRegistry meterRegistry;
   private final MethodMetricsProperties properties;

   public MethodMetricsAspect(MeterRegistry meterRegistry, MethodMetricsProperties properties) {
      this.meterRegistry = meterRegistry;
      this.properties = properties;
      logger.info("MethodMetricsAspect initialized with prefix: {}", properties.getPrefix());
   }

   @Around("@annotation(timedMethod)")
   public Object measureExecutionTime(ProceedingJoinPoint joinPoint, TimedMethod timedMethod) throws Throwable {
      if (!this.properties.isEnabled()) {
         return joinPoint.proceed();
      } else {
         String methodName = this.getMethodName(joinPoint, timedMethod);
         String className = joinPoint.getTarget().getClass().getSimpleName();
         Tags baseTags = Tags.of(new String[]{"method", methodName, "class", className});
         Tags tags = this.addExtraTags(timedMethod.extraTags(), baseTags);
         Counter.builder(this.properties.getPrefix() + ".calls.total").description("Total number of method calls").tags(tags).register(this.meterRegistry).increment();
         Timer.Sample sample = Timer.start(this.meterRegistry);

         Object var9;
         try {
            Object result = joinPoint.proceed();
            this.recordSuccess(tags);
            var9 = result;
         } catch (Exception e) {
            this.recordError(e, tags);
            throw e;
         } finally {
            this.recordExecutionTime(sample, tags);
         }

         return var9;
      }
   }

   private String getMethodName(ProceedingJoinPoint joinPoint, TimedMethod timedMethod) {
      if (StringUtils.hasText(timedMethod.value())) {
         return timedMethod.value();
      } else {
         MethodSignature signature = (MethodSignature)joinPoint.getSignature();
         Method method = signature.getMethod();
         return method.getName();
      }
   }

   private Tags addExtraTags(String[] extraTags, Tags baseTags) {
      Tags result = baseTags;

      for(String tag : extraTags) {
         String[] parts = tag.split("=");
         if (parts.length == 2) {
            result = result.and(parts[0].trim(), parts[1].trim());
         }
      }

      return result;
   }

   private void recordSuccess(Tags tags) {
      Counter.builder(this.properties.getPrefix() + ".calls.success").description("Number of successful method calls").tags(tags).register(this.meterRegistry).increment();
   }

   private void recordError(Exception e, Tags tags) {
      Tags errorTags = tags.and("exception", e.getClass().getSimpleName());
      Counter.builder(this.properties.getPrefix() + ".calls.error").description("Number of method calls with errors").tags(errorTags).register(this.meterRegistry).increment();
   }

   private void recordExecutionTime(Timer.Sample sample, Tags tags) {
      Timer.Builder timerBuilder = Timer.builder(this.properties.getPrefix() + ".execution.time").description("Method execution time").tags(tags);
      if (this.properties.isHistogram()) {
         timerBuilder.publishPercentileHistogram(this.properties.isHistogram()).publishPercentiles(this.properties.getPercentiles());
      }

      sample.stop(timerBuilder.register(this.meterRegistry));
   }
}
