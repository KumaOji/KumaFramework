package com.kuma.boot.idgenerator.uid1.buffer;

import com.kuma.boot.idgenerator.uid1.utils.NamingThreadFactory;
import com.kuma.boot.idgenerator.uid1.utils.PaddedAtomicLong;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class BufferPaddingExecutor {
   private static final Logger LOGGER = LoggerFactory.getLogger(RingBuffer.class);
   private static final String WORKER_NAME = "RingBuffer-Padding-Worker";
   private static final String SCHEDULE_NAME = "RingBuffer-Padding-Schedule";
   private static final long DEFAULT_SCHEDULE_INTERVAL = 300L;
   private final AtomicBoolean running;
   private final PaddedAtomicLong lastSecond;
   private final RingBuffer ringBuffer;
   private final BufferedUidProvider uidProvider;
   private final ExecutorService bufferPadExecutors;
   private final ScheduledExecutorService bufferPadSchedule;
   private long scheduleInterval;

   public BufferPaddingExecutor(RingBuffer ringBuffer, BufferedUidProvider uidProvider) {
      this(ringBuffer, uidProvider, true);
   }

   public BufferPaddingExecutor(RingBuffer ringBuffer, BufferedUidProvider uidProvider, boolean usingSchedule) {
      this.scheduleInterval = 300L;
      this.running = new AtomicBoolean(false);
      this.lastSecond = new PaddedAtomicLong(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
      this.ringBuffer = ringBuffer;
      this.uidProvider = uidProvider;
      int cores = Runtime.getRuntime().availableProcessors();
      this.bufferPadExecutors = Executors.newFixedThreadPool(cores * 2, new NamingThreadFactory("RingBuffer-Padding-Worker"));
      if (usingSchedule) {
         this.bufferPadSchedule = Executors.newSingleThreadScheduledExecutor(new NamingThreadFactory("RingBuffer-Padding-Schedule"));
      } else {
         this.bufferPadSchedule = null;
      }

   }

   public void start() {
      if (this.bufferPadSchedule != null) {
         this.bufferPadSchedule.scheduleWithFixedDelay(() -> this.paddingBuffer(), this.scheduleInterval, this.scheduleInterval, TimeUnit.SECONDS);
      }

   }

   public void shutdown() {
      if (!this.bufferPadExecutors.isShutdown()) {
         this.bufferPadExecutors.shutdownNow();
      }

      if (this.bufferPadSchedule != null && !this.bufferPadSchedule.isShutdown()) {
         this.bufferPadSchedule.shutdownNow();
      }

   }

   public boolean isRunning() {
      return this.running.get();
   }

   public void asyncPadding() {
      this.bufferPadExecutors.submit(this::paddingBuffer);
   }

   public void paddingBuffer() {
      LOGGER.info("Ready to padding buffer lastSecond:{}. {}", this.lastSecond.get(), this.ringBuffer);
      if (!this.running.compareAndSet(false, true)) {
         LOGGER.info("Padding buffer is still running. {}", this.ringBuffer);
      } else {
         boolean isFullRingBuffer = false;

         while(!isFullRingBuffer) {
            for(Long uid : this.uidProvider.provide(this.lastSecond.incrementAndGet())) {
               isFullRingBuffer = !this.ringBuffer.put(uid);
               if (isFullRingBuffer) {
                  break;
               }
            }
         }

         this.running.compareAndSet(true, false);
         LOGGER.info("End to padding buffer lastSecond:{}. {}", this.lastSecond.get(), this.ringBuffer);
      }
   }

   public void setScheduleInterval(long scheduleInterval) {
      Assert.isTrue(scheduleInterval > 0L, "Schedule interval must positive!");
      this.scheduleInterval = scheduleInterval;
   }
}
