package com.kuma.boot.openapi.client.model;

import com.kuma.boot.openapi.common.model.Binary;
import java.util.List;

public class BinaryParam {
   private List binaries;
   private String binariesStr;

   public List getBinaries() {
      return this.binaries;
   }

   public void setBinaries(List binaries) {
      this.binaries = binaries;
   }

   public String getBinariesStr() {
      return this.binariesStr;
   }

   public void setBinariesStr(String binariesStr) {
      this.binariesStr = binariesStr;
   }
}
