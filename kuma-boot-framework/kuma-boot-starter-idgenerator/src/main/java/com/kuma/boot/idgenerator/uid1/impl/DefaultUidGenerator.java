package com.kuma.boot.idgenerator.uid1.impl;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.idgenerator.uid1.BitsAllocator;
import com.kuma.boot.idgenerator.uid1.UidGenerator;
import com.kuma.boot.idgenerator.uid1.exception.UidGenerateException;
import com.kuma.boot.idgenerator.uid1.utils.DateUtils;
import com.kuma.boot.idgenerator.uid1.worker.WorkerIdAssigner;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class DefaultUidGenerator implements UidGenerator, InitializingBean {
   private static final Logger LOGGER = LoggerFactory.getLogger(DefaultUidGenerator.class);
   protected int timeBits = 28;
   protected int workerBits = 22;
   protected int seqBits = 13;
   protected String epochStr = "2016-05-20";
   protected long epochSeconds;
   protected BitsAllocator bitsAllocator;
   protected long workerId;
   protected long sequence;
   protected long lastSecond;
   protected WorkerIdAssigner workerIdAssigner;

   public DefaultUidGenerator() {
      this.epochSeconds = TimeUnit.MILLISECONDS.toSeconds(1463673600000L);
      this.sequence = 0L;
      this.lastSecond = -1L;
   }

   public void afterPropertiesSet() throws Exception {
      this.bitsAllocator = new BitsAllocator(this.timeBits, this.workerBits, this.seqBits);
      this.workerId = this.workerIdAssigner.assignWorkerId();
      if (this.workerId > this.bitsAllocator.getMaxWorkerId()) {
         long var10002 = this.workerId;
         throw new RuntimeException("Worker id " + var10002 + " exceeds the max " + this.bitsAllocator.getMaxWorkerId());
      } else {
         LOGGER.info("Initialized bits(1, {}, {}, {}) for workerID:{}", new Object[]{this.timeBits, this.workerBits, this.seqBits, this.workerId});
      }
   }

   public long getUID() throws UidGenerateException {
      try {
         return this.nextId();
      } catch (Exception e) {
         LOGGER.error("Generate unique id exception. ", e);
         throw new UidGenerateException(e);
      }
   }

   public String parseUID(long uid) {
      long totalBits = 64L;
      long signBits = (long)this.bitsAllocator.getSignBits();
      long timestampBits = (long)this.bitsAllocator.getTimestampBits();
      long workerIdBits = (long)this.bitsAllocator.getWorkerIdBits();
      long sequenceBits = (long)this.bitsAllocator.getSequenceBits();
      long sequence = uid << (int)(totalBits - sequenceBits) >>> (int)(totalBits - sequenceBits);
      long workerId = uid << (int)(timestampBits + signBits) >>> (int)(totalBits - workerIdBits);
      long deltaSeconds = uid >>> (int)(workerIdBits + sequenceBits);
      Date thatTime = new Date(TimeUnit.SECONDS.toMillis(this.epochSeconds + deltaSeconds));
      String thatTimeStr = DateUtils.formatByDateTimePattern(thatTime);
      return String.format("{\"UID\":\"%d\",\"timestamp\":\"%s\",\"workerId\":\"%d\",\"sequence\":\"%d\"}", uid, thatTimeStr, workerId, sequence);
   }

   protected synchronized long nextId() {
      long currentSecond = this.getCurrentSecond();
      if (currentSecond < this.lastSecond) {
         long refusedSeconds = this.lastSecond - currentSecond;
         throw new UidGenerateException("Clock moved backwards. Refusing for %d seconds", new Object[]{refusedSeconds});
      } else {
         if (currentSecond == this.lastSecond) {
            this.sequence = this.sequence + 1L & this.bitsAllocator.getMaxSequence();
            if (this.sequence == 0L) {
               currentSecond = this.getNextSecond(this.lastSecond);
            }
         } else {
            this.sequence = 0L;
         }

         this.lastSecond = currentSecond;
         return this.bitsAllocator.allocate(currentSecond - this.epochSeconds, this.workerId, this.sequence);
      }
   }

   private long getNextSecond(long lastTimestamp) {
      long timestamp;
      for(timestamp = this.getCurrentSecond(); timestamp <= lastTimestamp; timestamp = this.getCurrentSecond()) {
      }

      return timestamp;
   }

   private long getCurrentSecond() {
      long currentSecond = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
      if (currentSecond - this.epochSeconds > this.bitsAllocator.getMaxDeltaSeconds()) {
         throw new UidGenerateException("Timestamp bits is exhausted. Refusing UID generate. Now: " + currentSecond);
      } else {
         return currentSecond;
      }
   }

   public void setWorkerIdAssigner(WorkerIdAssigner workerIdAssigner) {
      this.workerIdAssigner = workerIdAssigner;
   }

   public void setTimeBits(int timeBits) {
      if (timeBits > 0) {
         this.timeBits = timeBits;
      }

   }

   public void setWorkerBits(int workerBits) {
      if (workerBits > 0) {
         this.workerBits = workerBits;
      }

   }

   public void setSeqBits(int seqBits) {
      if (seqBits > 0) {
         this.seqBits = seqBits;
      }

   }

   public void setEpochStr(String epochStr) {
      if (StringUtils.isNotBlank(epochStr)) {
         this.epochStr = epochStr;
         this.epochSeconds = TimeUnit.MILLISECONDS.toSeconds(DateUtils.parseByDayPattern(epochStr).getTime());
      }

   }
}
