package com.kuma.boot.idgenerator.uid.buffer;

@FunctionalInterface
public interface RejectedPutBufferHandler {
   void rejectPutBuffer(RingBuffer ringBuffer, long uid);
}
