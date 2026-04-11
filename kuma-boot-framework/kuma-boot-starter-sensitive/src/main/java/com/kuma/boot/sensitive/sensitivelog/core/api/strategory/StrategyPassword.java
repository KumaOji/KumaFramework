package com.kuma.boot.sensitive.sensitivelog.core.api.strategory;

import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.sensitive.sensitivelog.api.Context;
import com.kuma.boot.sensitive.sensitivelog.api.Strategy;
import com.kuma.boot.sensitive.sensitivelog.core.util.strategy.SensitiveStrategyUtil;

public class StrategyPassword implements Strategy {
   public StrategyPassword() {
   }

   public Object des(Object original, Context context) {
      return SensitiveStrategyUtil.password(ObjectUtils.objectToString(original));
   }
}
