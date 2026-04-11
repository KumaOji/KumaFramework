package com.kuma.boot.idgenerator.uid1.buffer;

import com.kuma.boot.idgenerator.uid1.utils.PaddedAtomicLong;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class RingBuffer {
   private static final Logger LOGGER = LoggerFactory.getLogger(RingBuffer.class);
   private static final int START_POINT = -1;
   private static final long CAN_PUT_FLAG = 0L;
   private static final long CAN_TAKE_FLAG = 1L;
   public static final int DEFAULT_PADDING_PERCENT = 50;
   private final int bufferSize;
   private final long indexMask;
   private final long[] slots;
   private final PaddedAtomicLong[] flags;
   private final AtomicLong tail;
   private final AtomicLong cursor;
   private final int paddingThreshold;
   private RejectedPutBufferHandler rejectedPutHandler;
   private RejectedTakeBufferHandler rejectedTakeHandler;
   private BufferPaddingExecutor bufferPaddingExecutor;

   public RingBuffer(int bufferSize) {
      this(bufferSize, 50);
   }

   public RingBuffer(int bufferSize, int paddingFactor) {
      this.tail = new PaddedAtomicLong(-1L);
      this.cursor = new PaddedAtomicLong(-1L);
      this.rejectedPutHandler = this::discardPutBuffer;
      this.rejectedTakeHandler = this::exceptionRejectedTakeBuffer;
      Assert.isTrue((long)bufferSize > 0L, "RingBuffer size must be positive");
      Assert.isTrue(Integer.bitCount(bufferSize) == 1, "RingBuffer size must be a power of 2");
      Assert.isTrue(paddingFactor > 0 && paddingFactor < 100, "RingBuffer size must be positive");
      this.bufferSize = bufferSize;
      this.indexMask = (long)(bufferSize - 1);
      this.slots = new long[bufferSize];
      this.flags = this.initFlags(bufferSize);
      this.paddingThreshold = bufferSize * paddingFactor / 100;
   }

   public synchronized boolean put(long uid) {
      long currentTail = this.tail.get();
      long currentCursor = this.cursor.get();
      long distance = currentTail - (currentCursor == -1L ? 0L : currentCursor);
      if (distance == (long)(this.bufferSize - 1)) {
         this.rejectedPutHandler.rejectPutBuffer(this, uid);
         return false;
      } else {
         int nextTailIndex = this.calSlotIndex(currentTail + 1L);
         if (this.flags[nextTailIndex].get() != 0L) {
            this.rejectedPutHandler.rejectPutBuffer(this, uid);
            return false;
         } else {
            this.slots[nextTailIndex] = uid;
            this.flags[nextTailIndex].set(1L);
            this.tail.incrementAndGet();
            return true;
         }
      }
   }

   public long take() {
      long currentCursor = this.cursor.get();
      long nextCursor = this.cursor.updateAndGet((old) -> old == this.tail.get() ? old : old + 1L);
      Assert.isTrue(nextCursor >= currentCursor, "Curosr can't move back");
      long currentTail = this.tail.get();
      if (currentTail - nextCursor < (long)this.paddingThreshold) {
         LOGGER.info("Reach the padding threshold:{}. tail:{}, cursor:{}, rest:{}", new Object[]{this.paddingThreshold, currentTail, nextCursor, currentTail - nextCursor});
         this.bufferPaddingExecutor.asyncPadding();
      }

      if (nextCursor == currentCursor) {
         this.rejectedTakeHandler.rejectTakeBuffer(this);
      }

      int nextCursorIndex = this.calSlotIndex(nextCursor);
      Assert.isTrue(this.flags[nextCursorIndex].get() == 1L, "Curosr not in can take status");
      long uid = this.slots[nextCursorIndex];
      this.flags[nextCursorIndex].set(0L);
      return uid;
   }

   protected int calSlotIndex(long sequence) {
      return (int)(sequence & this.indexMask);
   }

   protected void discardPutBuffer(RingBuffer ringBuffer, long uid) {
      LOGGER.warn("Rejected putting buffer for uid:{}. {}", uid, ringBuffer);
   }

   protected void exceptionRejectedTakeBuffer(RingBuffer ringBuffer) {
      LOGGER.warn("Rejected take buffer. {}", ringBuffer);
      throw new RuntimeException("Rejected take buffer. " + String.valueOf(ringBuffer));
   }

   private PaddedAtomicLong[] initFlags(int bufferSize) {
      PaddedAtomicLong[] flags = new PaddedAtomicLong[bufferSize];

      for(int i = 0; i < bufferSize; ++i) {
         flags[i] = new PaddedAtomicLong(0L);
      }

      return flags;
   }

   public long getTail() {
      return this.tail.get();
   }

   public long getCursor() {
      return this.cursor.get();
   }

   public int getBufferSize() {
      return this.bufferSize;
   }

   public void setBufferPaddingExecutor(BufferPaddingExecutor bufferPaddingExecutor) {
      this.bufferPaddingExecutor = bufferPaddingExecutor;
   }

   public void setRejectedPutHandler(RejectedPutBufferHandler rejectedPutHandler) {
      this.rejectedPutHandler = rejectedPutHandler;
   }

   public void setRejectedTakeHandler(RejectedTakeBufferHandler rejectedTakeHandler) {
      this.rejectedTakeHandler = rejectedTakeHandler;
   }

   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("RingBuffer [bufferSize=").append(this.bufferSize).append(", tail=").append(this.tail).append(", cursor=").append(this.cursor).append(", paddingThreshold=").append(this.paddingThreshold).append("]");
      return builder.toString();
   }
}
