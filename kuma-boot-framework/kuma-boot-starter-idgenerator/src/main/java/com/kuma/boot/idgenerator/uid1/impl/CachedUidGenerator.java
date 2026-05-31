package com.kuma.boot.idgenerator.uid1.impl;

import com.kuma.boot.idgenerator.uid1.buffer.BufferPaddingExecutor;
import com.kuma.boot.idgenerator.uid1.buffer.RejectedPutBufferHandler;
import com.kuma.boot.idgenerator.uid1.buffer.RejectedTakeBufferHandler;
import com.kuma.boot.idgenerator.uid1.buffer.RingBuffer;
import com.kuma.boot.idgenerator.uid1.exception.UidGenerateException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.Assert;

public class CachedUidGenerator extends DefaultUidGenerator implements DisposableBean {
   private static final Logger LOGGER = LoggerFactory.getLogger(CachedUidGenerator.class);
   private static final int DEFAULT_BOOST_POWER = 3;
   private int boostPower = 3;
   private int paddingFactor = 50;
   private Long scheduleInterval;
   private RejectedPutBufferHandler rejectedPutBufferHandler;
   private RejectedTakeBufferHandler rejectedTakeBufferHandler;
   private RingBuffer ringBuffer;
   private BufferPaddingExecutor bufferPaddingExecutor;

   public CachedUidGenerator() {
   }

   public void afterPropertiesSet() throws Exception {
      super.afterPropertiesSet();
      this.initRingBuffer();
      LOGGER.info("Initialized RingBuffer successfully.");
   }

   public long getUID() {
      try {
         return this.ringBuffer.take();
      } catch (Exception e) {
         LOGGER.error("Generate unique id exception. ", e);
         throw new UidGenerateException(e);
      }
   }

   public String parseUID(long uid) {
      return super.parseUID(uid);
   }

   public void destroy() throws Exception {
      this.bufferPaddingExecutor.shutdown();
   }

   protected List<Long> nextIdsForOneSecond(long currentSecond) {
      int listSize = (int)this.bitsAllocator.getMaxSequence() + 1;
      List<Long> uidList = new ArrayList<>(listSize);
      long firstSeqUid = this.bitsAllocator.allocate(currentSecond - this.epochSeconds, this.workerId, 0L);

      for(int offset = 0; offset < listSize; ++offset) {
         uidList.add(firstSeqUid + (long)offset);
      }

      return uidList;
   }

   private void initRingBuffer() {
      int bufferSize = (int)this.bitsAllocator.getMaxSequence() + 1 << this.boostPower;
      this.ringBuffer = new RingBuffer(bufferSize, this.paddingFactor);
      LOGGER.info("Initialized ring buffer size:{}, paddingFactor:{}", bufferSize, this.paddingFactor);
      boolean usingSchedule = this.scheduleInterval != null;
      this.bufferPaddingExecutor = new BufferPaddingExecutor(this.ringBuffer, this::nextIdsForOneSecond, usingSchedule);
      if (usingSchedule) {
         this.bufferPaddingExecutor.setScheduleInterval(this.scheduleInterval);
      }

      LOGGER.info("Initialized BufferPaddingExecutor. Using schdule:{}, interval:{}", usingSchedule, this.scheduleInterval);
      this.ringBuffer.setBufferPaddingExecutor(this.bufferPaddingExecutor);
      if (this.rejectedPutBufferHandler != null) {
         this.ringBuffer.setRejectedPutHandler(this.rejectedPutBufferHandler);
      }

      if (this.rejectedTakeBufferHandler != null) {
         this.ringBuffer.setRejectedTakeHandler(this.rejectedTakeBufferHandler);
      }

      this.bufferPaddingExecutor.paddingBuffer();
      this.bufferPaddingExecutor.start();
   }

   public void setBoostPower(int boostPower) {
      Assert.isTrue(boostPower > 0, "Boost power must be positive!");
      this.boostPower = boostPower;
   }

   public void setRejectedPutBufferHandler(RejectedPutBufferHandler rejectedPutBufferHandler) {
      Assert.notNull(rejectedPutBufferHandler, "RejectedPutBufferHandler can't be null!");
      this.rejectedPutBufferHandler = rejectedPutBufferHandler;
   }

   public void setRejectedTakeBufferHandler(RejectedTakeBufferHandler rejectedTakeBufferHandler) {
      Assert.notNull(rejectedTakeBufferHandler, "RejectedTakeBufferHandler can't be null!");
      this.rejectedTakeBufferHandler = rejectedTakeBufferHandler;
   }

   public void setScheduleInterval(long scheduleInterval) {
      Assert.isTrue(scheduleInterval > 0L, "Schedule interval must positive!");
      this.scheduleInterval = scheduleInterval;
   }
}
