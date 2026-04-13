package com.kuma.boot.data.jpa.fenix.core.concrete;

import com.kuma.boot.data.jpa.fenix.bean.BuildSource;
import com.kuma.boot.data.jpa.fenix.consts.LikeTypeEnum;
import java.util.HashMap;
import java.util.Map;

public class StartsWithHandler extends LikeHandler {
   private static final Map<String, Object> startMap = new HashMap(2);

   public StartsWithHandler() {
   }

   public static Map<String, Object> getStartMap() {
      return startMap;
   }

   public void buildSqlInfo(BuildSource source) {
      source.setOthers(startMap);
      super.buildSqlInfo(source);
   }

   static {
      startMap.put("type", LikeTypeEnum.STARTS_WITH);
   }
}
