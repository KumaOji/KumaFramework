package com.kuma.boot.retry.aop;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.retry.annotation.GuavaRetrying;
import com.kuma.boot.retry.listener.RetryLogListener;
import com.kuma.boot.retry.strategy.SpinBlockStrategy;
import io.github.itning.retry.Attempt;
import io.github.itning.retry.Retryer;
import io.github.itning.retry.RetryerBuilder;
import io.github.itning.retry.listener.RetryListener;
import io.github.itning.retry.strategy.stop.StopStrategies;
import io.github.itning.retry.strategy.wait.WaitStrategies;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
public class GuavaRetryingAspect {
   @Around("@annotation(com.kuma.boot.retry.annotation.GuavaRetrying)")
   public Object monitorAround(ProceedingJoinPoint pjp) throws Throwable {
      Signature var4 = pjp.getSignature();
      if (var4 instanceof MethodSignature signature) {
         Method method = signature.getMethod();
         GuavaRetrying annotation = (GuavaRetrying)method.getDeclaredAnnotation(GuavaRetrying.class);
         if (annotation.duration() <= 0L && annotation.attemptNumber() <= 1) {
            return pjp.proceed();
         } else {
            RetryerBuilder<Object> builder = RetryerBuilder.newBuilder();
            if (annotation.attemptNumber() > 0) {
               builder.withStopStrategy(StopStrategies.stopAfterAttempt(annotation.attemptNumber()));
            }

            if (annotation.duration() > 0L) {
               builder.withStopStrategy(StopStrategies.stopAfterDelay(annotation.duration(), TimeUnit.MILLISECONDS));
            }

            if (annotation.waitStrategySleepTime() > 0L) {
               builder.withWaitStrategy(WaitStrategies.fixedWait(annotation.waitStrategySleepTime(), TimeUnit.SECONDS));
            }

            builder.withBlockStrategy(new SpinBlockStrategy());
            builder.withRetryListener(new RetryLogListener());

            for(Class retryThrowable : annotation.exceptionClass()) {
               if (retryThrowable != null && Throwable.class.isAssignableFrom(retryThrowable)) {
                  builder.retryIfExceptionOfType(retryThrowable);
               }
            }

            return builder.build().call(() -> {
               try {
                  return pjp.proceed();
               } catch (Throwable throwable) {
                  throw new Exception(throwable);
               }
            });
         }
      } else {
         return null;
      }
   }

   public void guavaRetry() {
      Retryer<Boolean> retryer = RetryerBuilder.newBuilder().retryIfExceptionOfType(IOException.class).retryIfResult((res) -> !res).withWaitStrategy(WaitStrategies.exponentialWait(100L, 5L, TimeUnit.MINUTES)).withStopStrategy(StopStrategies.stopAfterAttempt(3)).withRetryListener(new RetryListener() {
         {
            Objects.requireNonNull(GuavaRetryingAspect.this);
         }

         public void onRetry(Attempt attempt) {
            LogUtils.info("number: {}", new Object[]{attempt.getAttemptNumber()});
         }
      }).build();

      try {
         retryer.call(() -> {
            LogUtils.info("sdlf", new Object[0]);
            return false;
         });
      } catch (Exception e) {
         LogUtils.error(e);
      }

   }
}
