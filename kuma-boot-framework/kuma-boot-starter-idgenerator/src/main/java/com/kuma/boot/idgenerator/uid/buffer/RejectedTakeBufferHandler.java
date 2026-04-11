package com.kuma.boot.idgenerator.uid.buffer;

@FunctionalInterface
public interface RejectedTakeBufferHandler {
   void rejectTakeBuffer(RingBuffer ringBuffer);
}
