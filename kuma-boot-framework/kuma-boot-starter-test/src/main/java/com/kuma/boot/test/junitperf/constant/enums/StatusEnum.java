package com.kuma.boot.test.junitperf.constant.enums;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

@API(
   status = Status.INTERNAL,
   since = "2_0_0"
)
public enum StatusEnum {
   PASSED("PASSED"),
   FAILED("FAILED");

   private String status;

   private StatusEnum(String status) {
      this.status = status;
   }

   public String getStatus() {
      return this.status;
   }

   // $FF: synthetic method
   private static StatusEnum[] $values() {
      return new StatusEnum[]{PASSED, FAILED};
   }
}
