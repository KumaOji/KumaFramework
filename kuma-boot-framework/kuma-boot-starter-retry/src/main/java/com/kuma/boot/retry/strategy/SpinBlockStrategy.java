package com.kuma.boot.retry.strategy;

import com.kuma.boot.common.utils.log.LogUtils;
import io.github.itning.retry.strategy.block.BlockStrategy;
import java.time.Duration;
import java.time.LocalDateTime;

public class SpinBlockStrategy implements BlockStrategy {
   public void block(long sleepTime) throws InterruptedException {
      LocalDateTime startTime = LocalDateTime.now();
      long start = System.currentTimeMillis();
      long end = start;
      LogUtils.info("[SpinBlockStrategy]...begin wait.", new Object[0]);

      while(end - start <= sleepTime) {
         end = System.currentTimeMillis();
      }

      Duration duration = Duration.between(startTime, LocalDateTime.now());
      LogUtils.info("[SpinBlockStrategy]...end wait.duration={}", new Object[]{duration.toMillis()});
   }
}
