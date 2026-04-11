package com.kuma.boot.flowengine.state.retry;

import com.kuma.boot.flowengine.engine.Execution;
import java.util.ArrayList;
import java.util.List;

public enum RetryFailTypeEnum {
   FAIL_RETREAT("failRetreat", "\u5931\u8d25\u540e\u9000\u907f\u8c03\u5ea6") {
      public String execute(Execution execution) {
         return execution.getFlowTrace().retryRetreat();
      }

      public void prepareNext(Execution execution) {
      }
   },
   FAIL_FAST("failFast", "\u5931\u8d25\u540e\u7acb\u5373") {
      public String execute(Execution execution) {
         return execution.getFlowTrace().retryFast();
      }

      public void prepareNext(Execution execution) {
      }
   },
   FAIL_BOMB("failBomb", "\u5931\u8d25\u540e\u91cd\u8bd5\u5230\u7206") {
      public String execute(Execution execution) {
         return execution.getFlowTrace().retryBomb();
      }

      public void prepareNext(Execution execution) {
      }
   };

   private final String code;
   private final String message;

   public abstract String execute(Execution execution);

   public abstract void prepareNext(Execution execution);

   private RetryFailTypeEnum(String code, String message) {
      this.code = code;
      this.message = message;
   }

   public String getCode() {
      return this.code;
   }

   public String getMessage() {
      return this.message;
   }

   public static RetryFailTypeEnum getByCode(String code) {
      for(RetryFailTypeEnum _enum : values()) {
         if (_enum.getCode().equals(code)) {
            return _enum;
         }
      }

      return null;
   }

   public static List<RetryFailTypeEnum> getAllEnum() {
      List<RetryFailTypeEnum> list = new ArrayList(values().length);

      for(RetryFailTypeEnum _enum : values()) {
         list.add(_enum);
      }

      return list;
   }

   public static List<String> getAllEnumCode() {
      List<String> list = new ArrayList(values().length);

      for(RetryFailTypeEnum _enum : values()) {
         list.add(_enum.getCode());
      }

      return list;
   }

   public static String getMsgByCode(String code) {
      if (code == null) {
         return null;
      } else {
         RetryFailTypeEnum _enum = getByCode(code);
         return _enum == null ? null : _enum.getMessage();
      }
   }

   public static String getCode(RetryFailTypeEnum _enum) {
      return _enum == null ? null : _enum.getCode();
   }

   // $FF: synthetic method
   private static RetryFailTypeEnum[] $values() {
      return new RetryFailTypeEnum[]{FAIL_RETREAT, FAIL_FAST, FAIL_BOMB};
   }
}
