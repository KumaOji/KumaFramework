package com.kuma.boot.pinyin.roses.api.context;

import cn.hutool.extra.spring.SpringUtil;
import com.kuma.boot.pinyin.roses.api.PinYinApi;

public class PinyinContext {
   public PinyinContext() {
   }

   public static PinYinApi me() {
      return (PinYinApi)SpringUtil.getBean(PinYinApi.class);
   }
}
