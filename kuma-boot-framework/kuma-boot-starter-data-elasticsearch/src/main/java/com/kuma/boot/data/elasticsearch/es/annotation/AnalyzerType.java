package com.kuma.boot.data.elasticsearch.es.annotation;

public enum AnalyzerType {
   NO("不使用分词"),
   STANDARD("standard"),
   IK_SMART("ik_smart"),
   IK_MAX_WORD("ik_max_word");

   private String type;

   private AnalyzerType(String type) {
      this.type = type;
   }

   public String getType() {
      return this.type;
   }

   // $FF: synthetic method
   private static AnalyzerType[] $values() {
      return new AnalyzerType[]{NO, STANDARD, IK_SMART, IK_MAX_WORD};
   }
}
