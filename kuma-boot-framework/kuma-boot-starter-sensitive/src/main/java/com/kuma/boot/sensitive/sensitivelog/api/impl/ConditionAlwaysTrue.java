package com.kuma.boot.sensitive.sensitivelog.api.impl;

import com.kuma.boot.sensitive.sensitivelog.api.Condition;
import com.kuma.boot.sensitive.sensitivelog.api.Context;

public class ConditionAlwaysTrue implements Condition {
   public ConditionAlwaysTrue() {
   }

   public boolean valid(Context context) {
      return true;
   }
}
