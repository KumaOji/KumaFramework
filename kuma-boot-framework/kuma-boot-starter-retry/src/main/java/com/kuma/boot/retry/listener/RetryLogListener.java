package com.kuma.boot.retry.listener;

import com.kuma.boot.common.utils.log.LogUtils;
import io.github.itning.retry.Attempt;
import io.github.itning.retry.listener.RetryListener;

public class RetryLogListener implements RetryListener {
   public void onRetry(Attempt attempt) {
      LogUtils.info("retry time : [{}]", new Object[]{attempt.getAttemptNumber()});
      LogUtils.info("retry delay : [{}]", new Object[]{attempt.getDelaySinceFirstAttempt()});
      LogUtils.info("hasException={}", new Object[]{attempt.hasException()});
      LogUtils.info("hasResult={}", new Object[]{attempt.hasResult()});
      if (attempt.hasException()) {
         LogUtils.info("causeBy={}", new Object[]{attempt.getExceptionCause().toString()});
      } else {
         LogUtils.info("result={}", new Object[]{attempt.getResult()});
      }

      LogUtils.info("log listen over.", new Object[0]);
   }
}
