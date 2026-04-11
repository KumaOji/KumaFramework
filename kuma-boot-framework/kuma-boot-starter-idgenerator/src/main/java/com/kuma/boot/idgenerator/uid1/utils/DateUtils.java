package com.kuma.boot.idgenerator.uid1.utils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang3.time.DateFormatUtils;

public abstract class DateUtils extends org.apache.commons.lang3.time.DateUtils {
   public static final String DAY_PATTERN = "yyyy-MM-dd";
   public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
   public static final String DATETIME_MS_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
   public static final Date DEFAULT_DATE = parseByDayPattern("1970-01-01");

   public DateUtils() {
   }

   public static Date parseByDayPattern(String str) {
      return parseDate(str, "yyyy-MM-dd");
   }

   public static Date parseByDateTimePattern(String str) {
      return parseDate(str, "yyyy-MM-dd HH:mm:ss");
   }

   public static Date parseDate(String str, String pattern) {
      try {
         return parseDate(str, new String[]{pattern});
      } catch (ParseException e) {
         throw new RuntimeException(e);
      }
   }

   public static String formatDate(Date date, String pattern) {
      return DateFormatUtils.format(date, pattern);
   }

   public static String formatByDayPattern(Date date) {
      return date != null ? DateFormatUtils.format(date, "yyyy-MM-dd") : null;
   }

   public static String formatByDateTimePattern(Date date) {
      return DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
   }

   public static String getCurrentDayByDayPattern() {
      Calendar cal = Calendar.getInstance();
      return formatByDayPattern(cal.getTime());
   }
}
