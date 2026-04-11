package com.kuma.boot.eventbus.disruptor.tmp5.enums;

import com.kuma.boot.common.utils.log.LogUtils;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;

public enum BlockingQueueTypeEnum {
   ARRAY_BLOCKING_QUEUE(1, "ArrayBlockingQueue"),
   LINKED_BLOCKING_QUEUE(2, "LinkedBlockingQueue"),
   PRIORITY_BLOCKING_QUEUE(3, "PriorityBlockingQueue"),
   DELAY_QUEUE(4, "DelayQueue"),
   SYNCHRONOUS_QUEUE(5, "SynchronousQueue"),
   LINKED_TRANSFER_QUEUE(6, "LinkedTransferQueue"),
   LINKED_BLOCKING_DEQUE(7, "LinkedBlockingDeque");

   private final Integer code;
   private final String name;

   private BlockingQueueTypeEnum(Integer code, String name) {
      this.code = code;
      this.name = name;
   }

   public static BlockingQueue<Runnable> buildBlockingQueue(String name, int capacity) {
      return buildBq(name, capacity, false);
   }

   public static BlockingQueue<Runnable> buildBq(String name, int capacity, boolean fair) {
      BlockingQueue<Runnable> blockingQueue = null;
      if (Objects.equals(name, ARRAY_BLOCKING_QUEUE.getName())) {
         blockingQueue = new ArrayBlockingQueue(capacity);
      } else if (Objects.equals(name, LINKED_BLOCKING_QUEUE.getName())) {
         blockingQueue = new LinkedBlockingQueue(capacity);
      } else if (Objects.equals(name, PRIORITY_BLOCKING_QUEUE.getName())) {
         blockingQueue = new PriorityBlockingQueue(capacity);
      } else if (Objects.equals(name, DELAY_QUEUE.getName())) {
         blockingQueue = new DelayQueue();
      } else if (Objects.equals(name, SYNCHRONOUS_QUEUE.getName())) {
         blockingQueue = new SynchronousQueue(fair);
      } else if (Objects.equals(name, LINKED_TRANSFER_QUEUE.getName())) {
         blockingQueue = new LinkedTransferQueue();
      } else if (Objects.equals(name, LINKED_BLOCKING_DEQUE.getName())) {
         blockingQueue = new LinkedBlockingDeque(capacity);
      }

      if (blockingQueue != null) {
         return blockingQueue;
      } else {
         LogUtils.error("BlockingQueueTypeEnum.buildBq\u672a\u5339\u914d\u5230\u961f\u5217name:{}", new Object[]{name});
         throw new RuntimeException("BlockingQueueTypeEnum.buildBq\u672a\u5339\u914d\u5230\u961f\u5217name:" + name);
      }
   }

   public Integer getCode() {
      return this.code;
   }

   public String getName() {
      return this.name;
   }

   // $FF: synthetic method
   private static BlockingQueueTypeEnum[] $values() {
      return new BlockingQueueTypeEnum[]{ARRAY_BLOCKING_QUEUE, LINKED_BLOCKING_QUEUE, PRIORITY_BLOCKING_QUEUE, DELAY_QUEUE, SYNCHRONOUS_QUEUE, LINKED_TRANSFER_QUEUE, LINKED_BLOCKING_DEQUE};
   }
}
