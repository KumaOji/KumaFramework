package com.kuma.boot.sensitive.sensitivejson;

public class ConditionAlwaysTrue implements Condition {
   public ConditionAlwaysTrue() {
   }

   public boolean valid() {
      return true;
   }
}
