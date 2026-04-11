package com.kuma.boot.flowengine.state.retry;

import com.kuma.boot.flowengine.state.FlowTrace;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;

public enum RetryRetreatTimeUnitEnum {
   HOUR("hour", "\u65f6") {
      Date doCalUnitTime(DateTime dateTime, int skipUnit) {
         return dateTime.plusHours(skipUnit).toDate();
      }
   },
   MINUTE("minute", "\u5206") {
      Date doCalUnitTime(DateTime dateTime, int skipUnit) {
         return dateTime.plusMinutes(skipUnit).toDate();
      }
   },
   DAY("day", "\u5929") {
      Date doCalUnitTime(DateTime dateTime, int skipUnit) {
         return dateTime.plusDays(skipUnit).toDate();
      }
   };

   private final String code;
   private final String message;

   public void calUnitTime(FlowTrace flowTrace, int skipUnit) {
      DateTime dateTime = new DateTime(flowTrace.getNextRetryTime());
      flowTrace.setNextRetryTime(this.doCalUnitTime(dateTime, skipUnit));
   }

   abstract Date doCalUnitTime(DateTime dateTime, int skipUnit);

   private RetryRetreatTimeUnitEnum(String code, String message) {
      this.code = code;
      this.message = message;
   }

   public String getCode() {
      return this.code;
   }

   public String getMessage() {
      return this.message;
   }

   public static RetryRetreatTimeUnitEnum getByCode(String code) {
      for(RetryRetreatTimeUnitEnum _enum : values()) {
         if (_enum.getCode().equals(code)) {
            return _enum;
         }
      }

      return null;
   }

   public static List<RetryRetreatTimeUnitEnum> getAllEnum() {
      List<RetryRetreatTimeUnitEnum> list = new ArrayList(values().length);

      for(RetryRetreatTimeUnitEnum _enum : values()) {
         list.add(_enum);
      }

      return list;
   }

   public static List<String> getAllEnumcode() {
      List<String> list = new ArrayList(values().length);

      for(RetryRetreatTimeUnitEnum _enum : values()) {
         list.add(_enum.getCode());
      }

      return list;
   }

   public static String getMsgByCode(String code) {
      if (code == null) {
         return null;
      } else {
         RetryRetreatTimeUnitEnum _enum = getByCode(code);
         return _enum == null ? null : _enum.getMessage();
      }
   }

   public static String getCode(RetryRetreatTimeUnitEnum _enum) {
      return _enum == null ? null : _enum.getCode();
   }

   // $FF: synthetic method
   private static RetryRetreatTimeUnitEnum[] $values() {
      return new RetryRetreatTimeUnitEnum[]{HOUR, MINUTE, DAY};
   }
}
