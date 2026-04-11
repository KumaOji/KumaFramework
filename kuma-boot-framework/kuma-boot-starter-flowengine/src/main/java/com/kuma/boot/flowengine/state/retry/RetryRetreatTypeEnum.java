package com.kuma.boot.flowengine.state.retry;

import com.kuma.boot.flowengine.state.FlowTrace;
import java.util.ArrayList;
import java.util.List;

public enum RetryRetreatTypeEnum {
   BY_DOUBLE("byDouble", "\u6210\u500d") {
      public void retreatNextTime(FlowTrace flowTrace) {
         int timeUnit = flowTrace.getRetryMeta().getRetreatUnit();
         int skipUnit = timeUnit * (flowTrace.getRetryTimes() + 1);
         flowTrace.getRetryMeta().getRetreatTimeUnit().calUnitTime(flowTrace, skipUnit);
      }
   },
   BY_FIXED("byFixed", "\u56fa\u5b9a") {
      public void retreatNextTime(FlowTrace flowTrace) {
         flowTrace.getRetryMeta().getRetreatTimeUnit().calUnitTime(flowTrace, flowTrace.getRetryMeta().getRetreatUnit());
      }
   };

   private final String code;
   private final String message;

   public abstract void retreatNextTime(FlowTrace flowTrace);

   private RetryRetreatTypeEnum(String code, String message) {
      this.code = code;
      this.message = message;
   }

   public String getCode() {
      return this.code;
   }

   public String getMessage() {
      return this.message;
   }

   public static RetryRetreatTypeEnum getByCode(String code) {
      for(RetryRetreatTypeEnum _enum : values()) {
         if (_enum.getCode().equals(code)) {
            return _enum;
         }
      }

      return null;
   }

   public static List<RetryRetreatTypeEnum> getAllEnum() {
      List<RetryRetreatTypeEnum> list = new ArrayList(values().length);

      for(RetryRetreatTypeEnum _enum : values()) {
         list.add(_enum);
      }

      return list;
   }

   public static List<String> getAllEnumCode() {
      List<String> list = new ArrayList(values().length);

      for(RetryRetreatTypeEnum _enum : values()) {
         list.add(_enum.getCode());
      }

      return list;
   }

   public static String getMsgByCode(String code) {
      if (code == null) {
         return null;
      } else {
         RetryRetreatTypeEnum _enum = getByCode(code);
         return _enum == null ? null : _enum.getMessage();
      }
   }

   public static String getCode(RetryRetreatTypeEnum _enum) {
      return _enum == null ? null : _enum.getCode();
   }

   // $FF: synthetic method
   private static RetryRetreatTypeEnum[] $values() {
      return new RetryRetreatTypeEnum[]{BY_DOUBLE, BY_FIXED};
   }
}
