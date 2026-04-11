package com.kuma.boot.idgenerator.uid1.utils;

import java.util.concurrent.atomic.AtomicLong;

public class PaddedAtomicLong extends AtomicLong {
   private static final long serialVersionUID = -3415778863941386253L;
   public volatile long p1;
   public volatile long p2;
   public volatile long p3;
   public volatile long p4;
   public volatile long p5;
   public volatile long p6 = 7L;

   public PaddedAtomicLong() {
   }

   public PaddedAtomicLong(long initialValue) {
      super(initialValue);
   }

   public long sumPaddingToPreventOptimization() {
      return this.p1 + this.p2 + this.p3 + this.p4 + this.p5 + this.p6;
   }
}
