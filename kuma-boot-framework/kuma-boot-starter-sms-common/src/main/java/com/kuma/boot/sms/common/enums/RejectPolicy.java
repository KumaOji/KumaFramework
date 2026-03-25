package com.kuma.boot.sms.common.enums;

public enum RejectPolicy {
   Abort,
   Caller,
   Discard,
   DiscardOldest;

   // $FF: synthetic method
   private static RejectPolicy[] $values() {
      return new RejectPolicy[]{Abort, Caller, Discard, DiscardOldest};
   }
}
