package com.kuma.boot.data.jpa.fenix.core.concrete;

import com.kuma.boot.data.jpa.fenix.bean.BuildSource;
import com.kuma.boot.data.jpa.fenix.consts.LikeTypeEnum;
import java.util.HashMap;
import java.util.Map;

public class EndsWithHandler extends LikeHandler {
   private static final Map<String, Object> endsMap = new HashMap(2);

   public EndsWithHandler() {
   }

   public static Map<String, Object> getEndsMap() {
      return endsMap;
   }

   public void buildSqlInfo(BuildSource source) {
      source.setOthers(endsMap);
      super.buildSqlInfo(source);
   }

   static {
      endsMap.put("type", LikeTypeEnum.ENDS_WITH);
   }
}
