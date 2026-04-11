package com.kuma.boot.idgenerator.uid1.buffer;

@FunctionalInterface
public interface RejectedPutBufferHandler {
   void rejectPutBuffer(RingBuffer ringBuffer, long uid);
}
