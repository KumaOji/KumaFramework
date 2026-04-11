package com.kuma.boot.idgenerator.uid1.buffer;

@FunctionalInterface
public interface RejectedTakeBufferHandler {
   void rejectTakeBuffer(RingBuffer ringBuffer);
}
