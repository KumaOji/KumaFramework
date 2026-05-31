package com.kuma.boot.idgenerator.uid.impl;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.idgenerator.uid.buffer.BufferPaddingExecutor;
import com.kuma.boot.idgenerator.uid.buffer.RejectedPutBufferHandler;
import com.kuma.boot.idgenerator.uid.buffer.RejectedTakeBufferHandler;
import com.kuma.boot.idgenerator.uid.buffer.RingBuffer;
import com.kuma.boot.idgenerator.uid.exception.UidGenerateException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@ConditionalOnProperty(prefix = "kuma.boot.idgenerator", name = "enabled", havingValue = "true", matchIfMissing = true)
public class CachedUidGenerator extends DefaultUidGenerator implements DisposableBean {
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
      this.setBoostPower(this.uidGenProperties.getBoostPower());
      this.setPaddingFactor(this.uidGenProperties.getPaddingFactor());
      if (this.uidGenProperties.getScheduleInterval() != null) {
         this.setScheduleInterval(this.uidGenProperties.getScheduleInterval());
      }

      this.initRingBuffer();
      LogUtils.info("[uid-gen] Initialized RingBuffer successfully.", new Object[0]);
   }

   public long getUID() {
      try {
         return this.ringBuffer.take();
      } catch (Exception e) {
         LogUtils.error("[uid-gen] Generate unique id exception. ", new Object[]{e});
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
      LogUtils.info("[uid-gen] Initialized ring buffer size:{}, paddingFactor:{}", new Object[]{bufferSize, this.paddingFactor});
      boolean usingSchedule = this.scheduleInterval != null;
      this.bufferPaddingExecutor = new BufferPaddingExecutor(this.ringBuffer, this::nextIdsForOneSecond, usingSchedule);
      if (usingSchedule) {
         this.bufferPaddingExecutor.setScheduleInterval(this.scheduleInterval);
      }

      LogUtils.info("[uid-gen] Initialized BufferPaddingExecutor. Using schdule:{}, interval:{}", new Object[]{usingSchedule, this.scheduleInterval});
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
      if (boostPower > 0) {
         this.boostPower = boostPower;
      }
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
      if (scheduleInterval > 0L) {
         this.scheduleInterval = scheduleInterval;
      }
   }

   public void setPaddingFactor(int paddingFactor) {
      if (paddingFactor > 0) {
         this.paddingFactor = paddingFactor;
      }
   }
}
