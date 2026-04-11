package com.kuma.boot.sensitive.sensitivejson;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.DesensitizedUtil;
import java.util.function.Function;

public enum SensitiveStrategy {
   ID_CARD((s) -> DesensitizedUtil.idCardNum(s, 3, 4)),
   PHONE(DesensitizedUtil::mobilePhone),
   ADDRESS((s) -> DesensitizedUtil.address(s, 8)),
   EMAIL(DesensitizedUtil::email),
   BANK_CARD(DesensitizedUtil::bankCard),
   USER_NAME(SensitiveStrategy::userName);

   private final Function<String, String> desensitizer;

   private SensitiveStrategy(Function<String, String> desensitizer) {
      this.desensitizer = desensitizer;
   }

   private static String userName(String userName) {
      return CharSequenceUtil.hide(userName, 1, userName.length());
   }

   public Function<String, String> getDesensitizer() {
      return this.desensitizer;
   }

   // $FF: synthetic method
   private static SensitiveStrategy[] $values() {
      return new SensitiveStrategy[]{ID_CARD, PHONE, ADDRESS, EMAIL, BANK_CARD, USER_NAME};
   }
}
