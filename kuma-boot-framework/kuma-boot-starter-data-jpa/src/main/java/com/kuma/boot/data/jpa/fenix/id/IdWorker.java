package com.kuma.boot.data.jpa.fenix.id;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public final class IdWorker {
   private static final IdWorker defaultIdWorker = new IdWorker();
   private static final int MAX_WORKER_INDEX = 15;
   private static final int WORKER_LENGTH = 16;
   public static final int DEFAULT_SIZE = 21;
   private static final char[] DEFAULT_ALPHABET = "_-0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
   private final AtomicInteger indexCounter = new AtomicInteger(0);
   private IdGenerator[] generators;

   public IdWorker() {
      this.setWorkerIndexes();
   }

   public IdWorker(int... indexes) {
      this.setWorkerIndexes(indexes);
   }

   private void setWorkerIndexes(int... workIndexes) {
      int[] indexes = workIndexes != null && workIndexes.length != 0 ? this.newSequence(workIndexes) : this.newSequence();
      Map<Integer, IdGenerator> generatorMap = new HashMap(16);
      this.generators = new IdGenerator[16];

      for(int i = 0; i < 16; ++i) {
         int index = indexes[i];
         this.generators[i] = (IdGenerator)generatorMap.computeIfAbsent(index, (k) -> new IdGenerator((long)index));
      }

   }

   private int[] newSequence(int... source) {
      int[] arr = new int[16];
      int len = source.length;
      int i = 0;

      for(int j = 0; i < 16; ++j) {
         if (j >= len) {
            j = 0;
         }

         if (source[j] >= 16) {
            throw new IllegalArgumentException("ID Worker \u7d22\u5f15\u5fc5\u987b\u5c0f\u4e8e16, \u5b9e\u9645\u503c\u662f:" + source[j]);
         }

         arr[i] = source[j];
         ++i;
      }

      return arr;
   }

   private int[] newSequence() {
      int[] arr = new int[16];

      for(int i = 0; i < 16; arr[i] = i++) {
      }

      return arr;
   }

   public long getId() {
      return this.generators[this.indexCounter.incrementAndGet() & 15].nextId();
   }

   public String getIdString() {
      return Long.toString(this.getId());
   }

   public String get36RadixId() {
      return Long.toString(this.getId(), 36);
   }

   public static String get36RadixId(long id) {
      return Long.toString(id, 36);
   }

   public String get62RadixId() {
      return get62RadixId(this.getId());
   }

   public static String get62RadixId(long id) {
      return Radix.toString(id, Radix.RADIX_62);
   }

   public static long getSnowflakeId() {
      return defaultIdWorker.getId();
   }

   public static String getSnowflakeIdString() {
      return defaultIdWorker.getIdString();
   }

   public static String getSnowflake36RadixId() {
      return defaultIdWorker.get36RadixId();
   }

   public static String getSnowflake62RadixId() {
      return defaultIdWorker.get62RadixId();
   }

   public static String getUuid() {
      return UUID.randomUUID().toString().replace("-", "");
   }

   public static String get62RadixUuid() {
      UUID uuid = UUID.randomUUID();
      return Radix.digits(uuid.getMostSignificantBits() >> 32, 8) + Radix.digits(uuid.getMostSignificantBits() >> 16, 4) + Radix.digits(uuid.getMostSignificantBits(), 4) + Radix.digits(uuid.getLeastSignificantBits() >> 48, 4) + Radix.digits(uuid.getLeastSignificantBits(), 12);
   }

   public static String getNanoId() {
      return getNanoId(21);
   }

   public static String getNanoId(final int size) {
      if (size <= 0) {
         throw new IllegalArgumentException("size must be greater than zero.");
      } else {
         int alphaBetaLength = DEFAULT_ALPHABET.length;
         int mask = (2 << (int)Math.floor(Math.log((double)alphaBetaLength - (double)1.0F) / Math.log((double)2.0F))) - 1;
         int step = (int)Math.ceil(1.6 * (double)mask * (double)size / (double)alphaBetaLength);
         StringBuilder builder = new StringBuilder();

         while(true) {
            byte[] bytes = new byte[step];
            ThreadLocalRandom.current().nextBytes(bytes);

            for(int i = 0; i < step; ++i) {
               int alphabetIndex = bytes[i] & mask;
               if (alphabetIndex < alphaBetaLength) {
                  builder.append(DEFAULT_ALPHABET[alphabetIndex]);
                  if (builder.length() == size) {
                     return builder.toString();
                  }
               }
            }
         }
      }
   }

   private static class IdGenerator {
      private final long workerId;
      private static final long EPOCH = 1457258545962L;
      private static final long MAX_WORKER_ID = 15L;
      private static final long WORKER_ID_SHIFT = 10L;
      private static final long TIMESTAMP_LEFT_SHIFT = 14L;
      private static final long SEQUENCE_MASK = 1023L;
      private long sequence = 0L;
      private int vibrance = -1;
      private long lastTimestamp = -1L;

      IdGenerator(final long workerId) {
         if (workerId >= 0L && workerId <= 15L) {
            this.workerId = workerId;
         } else {
            throw new IllegalArgumentException(String.format("worker Id \u4e0d\u80fd\u5c0f\u4e8e0\u6216\u8005\u5927\u4e8e %d", 15L));
         }
      }

      synchronized long nextId() {
         long timestamp = System.currentTimeMillis();
         if (timestamp == this.lastTimestamp) {
            this.sequence = this.sequence + 1L & 1023L;
            if (this.sequence == 0L) {
               timestamp = this.tillNextMillis(this.lastTimestamp);
            }
         } else {
            this.sequence = (long)(this.vibrance = ~this.vibrance & 1);
         }

         this.lastTimestamp = timestamp;
         return timestamp - 1457258545962L << 14 | this.workerId << 10 | this.sequence;
      }

      private long tillNextMillis(final long lastTimestamp) {
         long timestamp;
         for(timestamp = System.currentTimeMillis(); timestamp <= lastTimestamp; timestamp = System.currentTimeMillis()) {
         }

         return timestamp;
      }
   }
}
