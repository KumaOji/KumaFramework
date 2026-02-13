/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.date;

import com.kuma.boot.common.exception.BaseException;
import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.model.DatePattern;
import com.kuma.boot.common.utils.date.DateParser;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class DateUtils {
    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_DATE = "yyyy-MM-dd";
    public static final String PATTERN_TIME = "HH:mm:ss";
    public static final DateTimeFormatter DATETIME_FORMATTER = DatePattern.NORM_DATETIME_FORMAT;
    public static final DateTimeFormatter DATE_FORMATTER = DatePattern.NORM_DATE_FORMAT;
    public static final DateTimeFormatter TIME_FORMATTER = DatePattern.NORM_TIME_FORMAT;
    public static final String MATCH_TIME_24 = "(([0-1][0-9])|2[0-3]):[0-5][0-9]:[0-5][0-9]";
    public static final String REGEX_DATA = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))";
    public static final String DEFAULT_YEAR_FORMAT = "yyyy";
    public static final String DEFAULT_MONTH_FORMAT = "yyyy-MM";
    public static final String DEFAULT_MONTH_FORMAT_SLASH = "yyyy/MM";
    public static final String DEFAULT_MONTH_FORMAT_EN = "yyyy\u5e74MM\u6708";
    public static final String DEFAULT_WEEK_FORMAT = "yyyy-ww";
    public static final String DEFAULT_WEEK_FORMAT_EN = "yyyy\u5e74ww\u5468";
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_DATE_FORMAT_EN = "yyyy\u5e74MM\u6708dd\u65e5";
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_DATE_TIME_FORMAT_EN = "";
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
    public static final String DAY = "DAY";
    public static final String MONTH = "MONTH";
    public static final String WEEK = "WEEK";
    public static final String DEFAULT_DATE_FORMAT_MATCHES = "^\\d{4}-\\d{1,2}-\\d{1,2}$";
    public static final String DEFAULT_DATE_TIME_FORMAT_MATCHES = "^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$";
    public static final String DEFAULT_DATE_FORMAT_EN_MATCHES = "^\\d{4}\u5e74\\d{1,2}\u6708\\d{1,2}\u65e5$";
    public static final String DEFAULT_DATE_TIME_FORMAT_EN_MATCHES = "^\\d{4}\u5e74\\d{1,2}\u6708\\d{1,2}\u65e5\\d{1,2}\u65f6\\d{1,2}\u5206\\d{1,2}\u79d2$";
    public static final String SLASH_DATE_FORMAT_MATCHES = "^\\d{4}/\\d{1,2}/\\d{1,2}$";
    public static final String SLASH_DATE_TIME_FORMAT_MATCHES = "^\\d{4}/\\d{1,2}/\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$";
    public static final String SLASH_DATE_FORMAT = "yyyy/MM/dd";
    public static final String SLASH_DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";
    public static final String CRON_FORMAT = "ss mm HH dd MM ? yyyy";
    public static final long MAX_MONTH_DAY = 30L;
    public static final long MAX_3_MONTH_DAY = 90L;
    public static final long MAX_YEAR_DAY = 365L;
    protected static final Map<String, String> DATE_FORMAT = new LinkedHashMap<String, String>(5);
    public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String STANDARD_DATE_FORMAT = "yyyy-MM-dd";
    public static final String STANDARD_DATE_NO_UNDERLINE_FORMAT = "yyyyMMdd";
    public static final String FULL_DATE = "yyyyMMddHHmmss";
    public static final String PURE_DATE_FORMAT = "yyyyMMdd";
    public static final String DATE_ZH_FORMAT = "yyyy\u5e74MM\u6708dd\u65e5";
    public static final String PURE_TIME_FORMAT = "HHmmss";
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String TIME_ZH_FORMAT = "HH\u65f6mm\u5206ss\u79d2";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DATE_TIME_SEC_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String TIMESTAMP_FORMAT_17 = "yyyyMMddHHmmssSSS";
    public static final String TIMESTAMP_FORMAT_14 = "yyyyMMddHHmmss";
    public static final String TIMESTAMP_FORMAT_15 = "yyMMddHHmmssSSS";
    private static final Map<DateParser, String> dateParsersMap;

    private DateUtils() {
    }

    public static List<String> getBetweenWeek(java.util.Date start, java.util.Date end) {
        return DateUtils.getBetweenWeek(DateUtils.date2LocalDate(start), DateUtils.date2LocalDate(end));
    }

    public static String toDateFormatter(TemporalAccessor date) {
        return DATE_FORMATTER.format(date);
    }

    public static List<String> getBetweenWeek(String start, String end) {
        return DateUtils.getBetweenWeek(LocalDate.parse(start), LocalDate.parse(end));
    }

    public static List<String> getBetweenWeek(LocalDate startDate, LocalDate endDate) {
        return DateUtils.getBetweenWeek(startDate, endDate, DEFAULT_WEEK_FORMAT);
    }

    public static List<String> getBetweenWeek(LocalDate startDate, LocalDate endDate, String pattern) {
        ArrayList<String> list = new ArrayList<String>();
        long distance = ChronoUnit.WEEKS.between(startDate, endDate);
        if (distance < 1L) {
            return list;
        }
        Stream.iterate(startDate, d -> d.plusWeeks(1L)).limit(distance + 1L).forEach(f -> list.add(f.format(DateTimeFormatter.ofPattern(pattern))));
        return list;
    }

    public static List<String> getBetweenMonth(java.util.Date start, java.util.Date end) {
        return DateUtils.getBetweenMonth(DateUtils.date2LocalDate(start), DateUtils.date2LocalDate(end));
    }

    public static List<String> getBetweenMonth(String start, String end) {
        return DateUtils.getBetweenMonth(LocalDate.parse(start), LocalDate.parse(end));
    }

    public static List<String> getBetweenMonth(LocalDate startDate, LocalDate endDate) {
        return DateUtils.getBetweenMonth(startDate, endDate, DEFAULT_MONTH_FORMAT);
    }

    public static List<String> getBetweenMonth(LocalDate startDate, LocalDate endDate, String pattern) {
        ArrayList<String> list = new ArrayList<String>();
        long distance = ChronoUnit.MONTHS.between(startDate, endDate);
        if (distance < 1L) {
            return list;
        }
        Stream.iterate(startDate, d -> d.plusMonths(1L)).limit(distance + 1L).forEach(f -> list.add(f.format(DateTimeFormatter.ofPattern(pattern))));
        return list;
    }

    public static String calculationEn(LocalDateTime startTime, LocalDateTime endTime, List<String> dateList) {
        if (startTime == null) {
            startTime = LocalDateTime.now();
        }
        if (endTime == null) {
            endTime = LocalDateTime.now().plusDays(30L);
        }
        return DateUtils.calculationEn(startTime.toLocalDate(), endTime.toLocalDate(), dateList);
    }

    public static String calculation(LocalDate startDate, LocalDate endDate, List<String> dateList) {
        String dateType;
        long day;
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        if (endDate == null) {
            endDate = LocalDate.now().plusDays(30L);
        }
        if (dateList == null) {
            dateList = new ArrayList<String>();
        }
        if ((day = DateUtils.until(startDate, endDate)) >= 0L && day <= 30L) {
            dateType = DAY;
            dateList.addAll(DateUtils.getBetweenDay(startDate, endDate, "yyyy-MM-dd"));
        } else if (day > 30L && day <= 90L) {
            dateType = WEEK;
            dateList.addAll(DateUtils.getBetweenWeek(startDate, endDate, DEFAULT_WEEK_FORMAT));
        } else if (day > 90L && day <= 365L) {
            dateType = MONTH;
            dateList.addAll(DateUtils.getBetweenMonth(startDate, endDate, DEFAULT_MONTH_FORMAT));
        } else {
            throw new BaseException("\u65e5\u671f\u53c2\u6570\u53ea\u80fd\u4ecb\u4e8e0-365\u5929\u4e4b\u95f4");
        }
        return dateType;
    }

    public static String calculationEn(LocalDate startDate, LocalDate endDate, List<String> dateList) {
        String dateType;
        long day;
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        if (endDate == null) {
            endDate = LocalDate.now().plusDays(30L);
        }
        if (dateList == null) {
            dateList = new ArrayList<String>();
        }
        if ((day = DateUtils.until(startDate, endDate)) >= 0L && day <= 30L) {
            dateType = DAY;
            dateList.addAll(DateUtils.getBetweenDay(startDate, endDate, "yyyy\u5e74MM\u6708dd\u65e5"));
        } else if (day > 30L && day <= 90L) {
            dateType = WEEK;
            dateList.addAll(DateUtils.getBetweenWeek(startDate, endDate, DEFAULT_WEEK_FORMAT_EN));
        } else if (day > 90L && day <= 365L) {
            dateType = MONTH;
            dateList.addAll(DateUtils.getBetweenMonth(startDate, endDate, DEFAULT_MONTH_FORMAT_EN));
        } else {
            throw new BaseException("\u65e5\u671f\u53c2\u6570\u53ea\u80fd\u4ecb\u4e8e0-365\u5929\u4e4b\u95f4");
        }
        return dateType;
    }

    public static LocalDateTime getStartTime(String time) {
        Object startTime = time;
        if (time.matches("^\\d{4}-\\d{1,2}$")) {
            startTime = time + "-01 00:00:00";
        } else if (time.matches(DEFAULT_DATE_FORMAT_MATCHES)) {
            startTime = time + " 00:00:00";
        } else if (time.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}$")) {
            startTime = time + ":00";
        } else if (time.matches("^\\d{4}-\\d{1,2}-\\d{1,2}T{1}\\d{1,2}:\\d{1,2}:\\d{1,2}.\\d{3}Z$")) {
            startTime = time.replace("T", " ").substring(0, time.indexOf(46));
        }
        return LocalDateTime.parse((CharSequence)startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static LocalDateTime getEndTime(String time) {
        Object startTime = time;
        if (time.matches("^\\d{4}-\\d{1,2}$")) {
            java.util.Date date = DateUtils.parse(time, DEFAULT_MONTH_FORMAT);
            date = DateUtils.getLastDateOfMonth(date);
            startTime = DateUtils.formatAsDate(date) + " 23:59:59";
        } else if (time.matches(DEFAULT_DATE_FORMAT_MATCHES)) {
            startTime = time + " 23:59:59";
        } else if (time.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}$")) {
            startTime = time + ":59";
        } else if (time.matches("^\\d{4}-\\d{1,2}-\\d{1,2}T{1}\\d{1,2}:\\d{1,2}:\\d{1,2}.\\d{3}Z$")) {
            if ((time = time.replace("T", " ").substring(0, time.indexOf(46))).endsWith("00:00:00")) {
                time = time.replace("00:00:00", "23:59:59");
            }
            startTime = time;
        }
        return LocalDateTime.parse((CharSequence)startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static boolean between(LocalTime from, LocalTime to) {
        if (from == null) {
            throw new IllegalArgumentException("\u5f00\u59cb\u65f6\u95f4\u4e0d\u80fd\u4e3a\u7a7a");
        }
        if (to == null) {
            throw new IllegalArgumentException("\u7ed3\u675f\u65f6\u95f4\u4e0d\u80fd\u4e3a\u7a7a");
        }
        LocalTime now = LocalTime.now();
        return now.isAfter(from) && now.isBefore(to);
    }

    public static java.util.Date getDate2359(LocalDateTime value) {
        return DateUtils.getDate2359(value.toLocalDate());
    }

    public static java.util.Date getDate2359(java.util.Date value) {
        return DateUtils.getDate2359(DateUtils.date2LocalDate(value));
    }

    public static java.util.Date getDate2359(LocalDate value) {
        LocalDateTime dateEnd = LocalDateTime.of(value, LocalTime.MAX);
        return DateUtils.localDateTime2Date(dateEnd);
    }

    public static java.util.Date localDateTime2Date(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        return java.util.Date.from(zdt.toInstant());
    }

    public static LocalDateTime date2LocalDateTime(java.util.Date date) {
        if (date == null) {
            return LocalDateTime.now();
        }
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDateTime();
    }

    public static LocalDate date2LocalDate(java.util.Date date) {
        if (date == null) {
            return LocalDate.now();
        }
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDate();
    }

    public static LocalTime date2LocalTime(java.util.Date date) {
        if (date == null) {
            return LocalTime.now();
        }
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalTime();
    }

    public static LocalDateTime getDateTimeOfTimestamp(long epochMilli) {
        Instant instant = Instant.ofEpochMilli(epochMilli);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static LocalDateTime getDateTimeOfSecond(long epochSecond) {
        Instant instant = Instant.ofEpochSecond(epochSecond);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static long until(java.util.Date endDate) {
        return LocalDateTime.now().until(DateUtils.date2LocalDateTime(endDate), ChronoUnit.DAYS);
    }

    public static long until(java.util.Date startDate, java.util.Date endDate) {
        return DateUtils.date2LocalDateTime(startDate).until(DateUtils.date2LocalDateTime(endDate), ChronoUnit.DAYS);
    }

    public static long until(LocalDateTime startDate, LocalDateTime endDate) {
        return startDate.until(endDate, ChronoUnit.DAYS);
    }

    public static long until(LocalDate startDate, LocalDate endDate) {
        return startDate.until(endDate, ChronoUnit.DAYS);
    }

    public static List<String> getBetweenDay(java.util.Date start, java.util.Date end) {
        return DateUtils.getBetweenDay(DateUtils.date2LocalDate(start), DateUtils.date2LocalDate(end));
    }

    public static List<String> getBetweenDay(String start, String end) {
        return DateUtils.getBetweenDay(LocalDate.parse(start), LocalDate.parse(end));
    }

    public static List<String> getBetweenDay(LocalDate startDate, LocalDate endDate) {
        return DateUtils.getBetweenDay(startDate, endDate, "yyyy-MM-dd");
    }

    public static List<String> getBetweenDayEn(LocalDate startDate, LocalDate endDate) {
        return DateUtils.getBetweenDay(startDate, endDate, "yyyy\u5e74MM\u6708dd\u65e5");
    }

    public static List<String> getBetweenDay(LocalDate startDate, LocalDate endDate, String pattern) {
        if (pattern == null) {
            pattern = "yyyy-MM-dd";
        }
        ArrayList<String> list = new ArrayList<String>();
        long distance = ChronoUnit.DAYS.between(startDate, endDate);
        if (distance < 1L) {
            return list;
        }
        String finalPattern = pattern;
        Stream.iterate(startDate, d -> d.plusDays(1L)).limit(distance + 1L).forEach(f -> list.add(f.format(DateTimeFormatter.ofPattern(finalPattern))));
        return list;
    }

    public static LocalDate parse(String source) {
        String sourceTrim = source.trim();
        Set<Map.Entry<String, String>> entries = DATE_FORMAT.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            if (!sourceTrim.matches(entry.getValue())) continue;
            return LocalDate.parse(source, DateTimeFormatter.ofPattern(entry.getKey()));
        }
        throw new BaseException("\u89e3\u6790\u65e5\u671f\u5931\u8d25, \u8bf7\u4f20\u9012\u6b63\u786e\u7684\u65e5\u671f\u683c\u5f0f");
    }

    public static String getCron(java.util.Date date) {
        return DateUtils.format(date, CRON_FORMAT);
    }

    public static String getCron(LocalDateTime date) {
        return DateUtils.format(date, CRON_FORMAT);
    }

    public static String format(LocalDateTime date, String pattern) {
        if (date == null) {
            date = LocalDateTime.now();
        }
        if (pattern == null) {
            pattern = DEFAULT_MONTH_FORMAT;
        }
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String format(LocalDate date, String pattern) {
        if (date == null) {
            date = LocalDate.now();
        }
        if (pattern == null) {
            pattern = DEFAULT_MONTH_FORMAT;
        }
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String format(java.util.Date d, String f) {
        java.util.Date date = d;
        String format = f;
        if (date == null) {
            date = new java.util.Date();
        }
        if (format == null) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    public static String formatAsDate(LocalDateTime date) {
        return DateUtils.format(date, "yyyy-MM-dd");
    }

    public static String formatAsDate(LocalDate date) {
        return DateUtils.format(date, "yyyy-MM-dd");
    }

    public static String formatAsDateEn(LocalDateTime date) {
        return DateUtils.format(date, "yyyy\u5e74MM\u6708dd\u65e5");
    }

    public static String formatAsYearMonth(LocalDateTime date) {
        return DateUtils.format(date, DEFAULT_MONTH_FORMAT);
    }

    public static String formatAsYearMonthEn(LocalDateTime date) {
        return DateUtils.format(date, DEFAULT_MONTH_FORMAT_EN);
    }

    public static String formatAsYearWeek(LocalDateTime date) {
        return DateUtils.format(date, DEFAULT_WEEK_FORMAT);
    }

    public static String formatAsYearWeekEn(LocalDateTime date) {
        return DateUtils.format(date, DEFAULT_WEEK_FORMAT_EN);
    }

    public static String formatAsYearMonth(java.util.Date date) {
        SimpleDateFormat df = new SimpleDateFormat(DEFAULT_MONTH_FORMAT);
        return df.format(date);
    }

    public static String formatAsYearWeek(java.util.Date date) {
        SimpleDateFormat df = new SimpleDateFormat(DEFAULT_WEEK_FORMAT);
        return df.format(date);
    }

    public static String formatAsTime(java.util.Date date) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        return df.format(date);
    }

    public static String formatAsDate(java.util.Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }

    public static String formatAsDateTime(java.util.Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(date);
    }

    public static String formatAsDay(java.util.Date date) {
        SimpleDateFormat df = new SimpleDateFormat("dd");
        return df.format(date);
    }

    public static java.util.Date parse(String dateStr, String format) {
        java.util.Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setLenient(false);
        try {
            date = dateFormat.parse(dateStr);
        }
        catch (Exception e) {
            LogUtils.error("DateUtil error", e);
        }
        return date;
    }

    public static java.util.Date getLastDateOfMonth(java.util.Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(2, 1);
        calendar.set(5, 0);
        return calendar.getTime();
    }

    public static java.util.Date parseAsDate(String source) {
        String sourceTrim = source.trim();
        Set<Map.Entry<String, String>> entries = DATE_FORMAT.entrySet();
        try {
            for (Map.Entry<String, String> entry : entries) {
                if (!sourceTrim.matches(entry.getValue())) continue;
                return new SimpleDateFormat(entry.getKey()).parse(source);
            }
        }
        catch (ParseException e) {
            throw new BaseException("\u89e3\u6790\u65e5\u671f\u5931\u8d25, \u8bf7\u4f20\u9012\u6b63\u786e\u7684\u65e5\u671f\u683c\u5f0f");
        }
        throw new BaseException("\u89e3\u6790\u65e5\u671f\u5931\u8d25, \u8bf7\u4f20\u9012\u6b63\u786e\u7684\u65e5\u671f\u683c\u5f0f");
    }

    public static java.util.Date parseAsDateTime(String dateTime) {
        SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return simpledateformat.parse(dateTime);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public static java.util.Date getDate0000(LocalDateTime value) {
        return DateUtils.getDate0000(value.toLocalDate());
    }

    public static java.util.Date getDate0000(java.util.Date value) {
        return DateUtils.getDate0000(DateUtils.date2LocalDate(value));
    }

    public static java.util.Date getDate0000(LocalDate value) {
        LocalDateTime todayStart = LocalDateTime.of(value, LocalTime.MIN);
        return DateUtils.localDateTime2Date(todayStart);
    }

    public static long getTimestamp() {
        return Instant.now().toEpochMilli();
    }

    public static int getCurrentYear() {
        return LocalDate.now().getYear();
    }

    public static String getCurrentYearMonth() {
        return DateUtils.getCurrentDate(DEFAULT_MONTH_FORMAT);
    }

    public static String getCurrentDate() {
        return DateUtils.getCurrentDate("yyyy-MM-dd");
    }

    public static String getNextDate() {
        return DateUtils.getNextDate("yyyy-MM-dd");
    }

    public static String getCurrentTime() {
        return DateUtils.getCurrentTime("HH:mm:ss");
    }

    public static String getCurrentDateTime() {
        return DateUtils.getCurrentDateTime("yyyy-MM-dd HH:mm:ss");
    }

    public static String getCurrentYearMonthShort() {
        return DateUtils.getCurrentDate("yyyyMM");
    }

    public static String getCurrentDateShort() {
        return DateUtils.getCurrentDate("yyyyMMdd");
    }

    public static String getNextDateShort() {
        return DateUtils.getNextDate("yyyyMMdd");
    }

    public static String getCurrentTimeShort() {
        return DateUtils.getCurrentTime(PURE_TIME_FORMAT);
    }

    public static String getCurrentDateTimeShort() {
        return DateUtils.getCurrentDateTime("yyyyMMddHHmmss");
    }

    public static String getCurrentDate(String pattern) {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String getNextDate(String pattern) {
        return LocalDate.now().plusDays(1L).format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String getCurrentTime(String pattern) {
        return LocalTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String getCurrentDateTime(String pattern) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }

    public static LocalDateTime timestampToLocalDateTime(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static long localDateTimeToTimestamp(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static String formatTimestamp(long timestamp) {
        return DateUtils.formatTimestamp(timestamp, "yyyy-MM-dd HH:mm:ss");
    }

    public static String formatTimestampShort(long timestamp) {
        return DateUtils.formatTimestamp(timestamp, "yyyyMMddHHmmss");
    }

    public static String formatTimestamp(long timestamp, String pattern) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return DateUtils.formatLocalDateTime(localDateTime, pattern);
    }

    public static String formatLocalDate(LocalDate localDate) {
        return DateUtils.formatLocalDate(localDate, "yyyy-MM-dd");
    }

    public static String formatLocalDateShort(LocalDate localDate) {
        return DateUtils.formatLocalDate(localDate, "yyyyMMdd");
    }

    public static String formatLocalTime(LocalTime localTime) {
        return DateUtils.formatLocalTime(localTime, "HH:mm:ss");
    }

    public static String formatLocalTimeShort(LocalTime localTime) {
        return DateUtils.formatLocalTime(localTime, PURE_TIME_FORMAT);
    }

    public static String formatLocalDateTime(LocalDateTime localDateTime) {
        return DateUtils.formatLocalDateTime(localDateTime, "yyyy-MM-dd HH:mm:ss");
    }

    public static String formatLocalDateTimeShort(LocalDateTime localDateTime) {
        return DateUtils.formatLocalDateTime(localDateTime, "yyyyMMddHHmmss");
    }

    public static String formatLocalDate(LocalDate localDate, String pattern) {
        return localDate.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String formatLocalTime(LocalTime localTime, String pattern) {
        return localTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String formatLocalDateTime(LocalDateTime localDateTime, String pattern) {
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static LocalDate parseLocalDate(String date, String pattern) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern));
    }

    public static LocalTime parseLocalTime(String time, String pattern) {
        return LocalTime.parse(time, DateTimeFormatter.ofPattern(pattern));
    }

    public static LocalDateTime parseLocalDateTime(String dateTime, String pattern) {
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(pattern));
    }

    public static LocalDate getCurrentWeekFirstDate() {
        return LocalDate.now().minusWeeks(0L).with(DayOfWeek.MONDAY);
    }

    public static LocalDate getCurrentWeekLastDate() {
        return LocalDate.now().minusWeeks(0L).with(DayOfWeek.SUNDAY);
    }

    public static LocalDate getCurrentMonthFirstDate() {
        return LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
    }

    public static LocalDate getCurrentMonthLastDate() {
        return LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
    }

    public static LocalDate getWeekFirstDate(String date, String pattern) {
        return DateUtils.parseLocalDate(date, pattern).minusWeeks(0L).with(DayOfWeek.MONDAY);
    }

    public static LocalDate getWeekFirstDate(LocalDate localDate) {
        return localDate.minusWeeks(0L).with(DayOfWeek.MONDAY);
    }

    public static LocalDate getWeekLastDate(String date, String pattern) {
        return DateUtils.parseLocalDate(date, pattern).minusWeeks(0L).with(DayOfWeek.SUNDAY);
    }

    public static LocalDate getWeekLastDate(LocalDate localDate) {
        return localDate.minusWeeks(0L).with(DayOfWeek.SUNDAY);
    }

    public static LocalDate getMonthFirstDate(String date, String pattern) {
        return DateUtils.parseLocalDate(date, pattern).with(TemporalAdjusters.firstDayOfMonth());
    }

    public static LocalDate getMonthFirstDate(LocalDate localDate) {
        return localDate.with(TemporalAdjusters.firstDayOfMonth());
    }

    public static int getCurrentWeek() {
        return LocalDate.now().getDayOfWeek().getValue();
    }

    public static int getWeek(LocalDate localDate) {
        return localDate.getDayOfWeek().getValue();
    }

    public static int getWeek(String date, String pattern) {
        return DateUtils.parseLocalDate(date, pattern).getDayOfWeek().getValue();
    }

    public static long intervalDays(LocalDate startLocalDate, LocalDate endLocalDate) {
        return endLocalDate.toEpochDay() - startLocalDate.toEpochDay();
    }

    public static long intervalHours(LocalDateTime startLocalDateTime, LocalDateTime endLocalDateTime) {
        return Duration.between(startLocalDateTime, endLocalDateTime).toHours();
    }

    public static long intervalMinutes(LocalDateTime startLocalDateTime, LocalDateTime endLocalDateTime) {
        return Duration.between(startLocalDateTime, endLocalDateTime).toMinutes();
    }

    public static long intervalMillis(LocalDateTime startLocalDateTime, LocalDateTime endLocalDateTime) {
        return Duration.between(startLocalDateTime, endLocalDateTime).toMillis();
    }

    public static boolean isCurrentLeapYear() {
        return LocalDate.now().isLeapYear();
    }

    public static boolean isLeapYear(LocalDate localDate) {
        return localDate.isLeapYear();
    }

    public static boolean isToday(LocalDate localDate) {
        return LocalDate.now().equals(localDate);
    }

    public static Long toEpochMilli(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static Long toSelectEpochMilli(LocalDateTime localDateTime, ZoneId zoneId) {
        return localDateTime.atZone(zoneId).toInstant().toEpochMilli();
    }

    public static String getDateAfterDays(int days) {
        Calendar date = Calendar.getInstance();
        date.add(5, days);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDate.format(date.getTime());
    }

    public static String addDays(String source, int days) {
        java.util.Date date = DateUtils.localDateToDate(DateUtils.parseLocalDate(source, "yyyy-MM-dd"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(5, days);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(calendar.getTime());
    }

    public static java.util.Date localDateToDate(LocalDate localDate) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        return java.util.Date.from(instant);
    }

    public static boolean isValidate24(String time) {
        Pattern p = Pattern.compile(MATCH_TIME_24);
        return p.matcher(time).matches();
    }

    public static boolean isDate(String date) {
        Pattern pat = Pattern.compile(REGEX_DATA);
        Matcher mat = pat.matcher(date);
        return mat.matches();
    }

    public static java.util.Date startOfTodDayTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar.getTime();
    }

    public static java.util.Date startOfTodDayTime(java.util.Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar.getTime();
    }

    public static long startOfTodDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        java.util.Date date = calendar.getTime();
        return date.getTime() / 1000L;
    }

    public static java.util.Date endOfDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(11, 23);
        calendar.set(12, 59);
        calendar.set(13, 59);
        calendar.set(14, 999);
        return calendar.getTime();
    }

    public static java.util.Date endOfDate(java.util.Date date) {
        if (date == null) {
            date = new java.util.Date();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(11, 23);
        calendar.set(12, 59);
        calendar.set(13, 59);
        calendar.set(14, 999);
        return calendar.getTime();
    }

    public static Map<String, Object> getYearMonthAndDay(int dayUntilNow) {
        HashMap<String, Object> map = new HashMap<String, Object>(3);
        Calendar calendar = Calendar.getInstance();
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        calendar.add(5, -dayUntilNow);
        map.put("year", calendar.get(1));
        map.put("month", calendar.get(2) + 1);
        map.put("day", calendar.get(5));
        return map;
    }

    public static java.util.Date toDate(String date, String pattern) {
        if (DEFAULT_DATE_TIME_FORMAT_EN.equals(date)) {
            return null;
        }
        if (pattern == null) {
            pattern = "yyyy-MM-dd";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.ENGLISH);
        java.util.Date newDate = new java.util.Date();
        try {
            newDate = sdf.parse(date);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return newDate;
    }

    public static Long[] getLastMonth() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(1);
        int month = cal.get(2) + 1;
        cal.set(5, 1);
        cal.add(5, -1);
        int day = cal.get(5);
        Object months = DEFAULT_DATE_TIME_FORMAT_EN;
        Object days = DEFAULT_DATE_TIME_FORMAT_EN;
        if (month > 1) {
            --month;
        } else {
            --year;
            month = 12;
        }
        months = String.valueOf(month).length() <= 1 ? "0" + month : String.valueOf(month);
        days = String.valueOf(day).length() <= 1 ? "0" + day : String.valueOf(day);
        String firstDay = year + "-" + (String)months + "-01";
        String lastDay = year + "-" + (String)months + "-" + (String)days + " 23:59:59";
        Long[] lastMonth = new Long[]{DateUtils.getDateline(firstDay), DateUtils.getDateline(lastDay, "yyyy-MM-dd HH:mm:ss")};
        return lastMonth;
    }

    public static String toString(java.util.Date date) {
        return DateUtils.toString(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String toString(Long date) {
        return DateUtils.toString(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String toString(java.util.Date date, String pattern) {
        if (date == null) {
            return DEFAULT_DATE_TIME_FORMAT_EN;
        }
        if (pattern == null) {
            pattern = "yyyy-MM-dd";
        }
        String dateString = DEFAULT_DATE_TIME_FORMAT_EN;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            dateString = sdf.format(date);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return dateString;
    }

    public static String toString(Long time, String pattern) {
        if (time > 0L) {
            if (time.toString().length() == 10) {
                time = time * 1000L;
            }
            java.util.Date date = new java.util.Date(time);
            return DateUtils.toString(date, pattern);
        }
        return DEFAULT_DATE_TIME_FORMAT_EN;
    }

    public static boolean inRangeOf(long start, long end) {
        long now = DateUtils.getDateline();
        return start <= now && end >= now;
    }

    public static long getDateline(String date) {
        return Objects.requireNonNull(DateUtils.toDate(date, "yyyy-MM-dd")).getTime() / 1000L;
    }

    public static long getDateline() {
        return System.currentTimeMillis() / 1000L;
    }

    public static String getCurrentDateStr(String format) {
        return DateUtils.toString(new java.util.Date(), format);
    }

    public static long getDateline(String date, String pattern) {
        return Objects.requireNonNull(DateUtils.toDate(date, pattern)).getTime() / 1000L;
    }

    public static long getBeforeMonthDateline(int beforeMonth) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTime(new java.util.Date());
        c.add(2, 0 - beforeMonth);
        java.util.Date m = c.getTime();
        String mon = format.format(m);
        return DateUtils.getDateline(mon, "yyyy-MM-dd HH:mm:ss");
    }

    public static java.util.Date getCurrentDayEndTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(13, 0);
        cal.set(14, 0);
        cal.set(5, cal.get(5) + 1);
        cal.set(13, cal.get(13) - 1);
        return cal.getTime();
    }

    public static Integer getDelayTime(Long startTime) {
        int time = Math.toIntExact((startTime - System.currentTimeMillis()) / 1000L);
        if (time <= 0) {
            time = 1;
        }
        return time;
    }

    public static java.util.Date getBeginTime(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate localDate = yearMonth.atDay(1);
        LocalDateTime startOfDay = localDate.atStartOfDay();
        ZonedDateTime zonedDateTime = startOfDay.atZone(ZoneId.of("Asia/Shanghai"));
        return java.util.Date.from(zonedDateTime.toInstant());
    }

    public static java.util.Date getEndTime(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();
        LocalDateTime localDateTime = endOfMonth.atTime(23, 59, 59, 999);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Shanghai"));
        return java.util.Date.from(zonedDateTime.toInstant());
    }

    public static String getDateFormat(java.util.Date date, String format) {
        if (ObjectUtils.isNull(date)) {
            return null;
        }
        return new SimpleDateFormat(format).format(date);
    }

    public static String getDateFormat17(java.util.Date date) {
        return DateUtils.getDateFormat(date, TIMESTAMP_FORMAT_17);
    }

    public static String getDateFormat14(java.util.Date date) {
        return DateUtils.getDateFormat(date, "yyyyMMddHHmmss");
    }

    public static java.util.Date getFormatDate(String dateStr, String format) {
        if (StringUtils.isEmptyTrim(dateStr)) {
            return null;
        }
        try {
            return new SimpleDateFormat(format).parse(dateStr);
        }
        catch (ParseException e) {
            throw new BootException(e);
        }
    }

    public static java.util.Date getFormatDate17(String dateStr) {
        return DateUtils.getFormatDate(dateStr, TIMESTAMP_FORMAT_17);
    }

    public static java.util.Date getFormatDate14(String dateStr) {
        return DateUtils.getFormatDate(dateStr, "yyyyMMddHHmmss");
    }

    public static String getCurrentDatePureStr() {
        java.util.Date now = new java.util.Date();
        return new SimpleDateFormat("yyyyMMdd").format(now);
    }

    public static String getYesterdayPureStr() {
        java.util.Date now = new java.util.Date();
        java.util.Date yesterday = DateUtils.addDay(now, -1);
        return new SimpleDateFormat("yyyyMMdd").format(yesterday);
    }

    public static String getCurrentTimeStampStr() {
        java.util.Date now = new java.util.Date();
        return new SimpleDateFormat(TIMESTAMP_FORMAT_17).format(now);
    }

    public static String getCurrentTime17() {
        java.util.Date now = new java.util.Date();
        return new SimpleDateFormat(TIMESTAMP_FORMAT_17).format(now);
    }

    public static String getCurrentTime14() {
        java.util.Date now = new java.util.Date();
        return new SimpleDateFormat("yyyyMMddHHmmss").format(now);
    }

    public static String getCurrentTimeStampStr15() {
        java.util.Date now = new java.util.Date();
        return new SimpleDateFormat(TIMESTAMP_FORMAT_15).format(now);
    }

    public static String getCurrentTimeMills() {
        return String.valueOf(System.currentTimeMillis());
    }

    public static String getCurrentDateTimeStr() {
        java.util.Date now = new java.util.Date();
        return new SimpleDateFormat(DATE_TIME_FORMAT).format(now);
    }

    public static long convertMsToNs(long ms) {
        return TimeUnit.NANOSECONDS.convert(ms > 0L ? ms : 0L, TimeUnit.MILLISECONDS);
    }

    public static java.util.Date now() {
        return new java.util.Date();
    }

    public static long costTimeInMills(java.util.Date start, java.util.Date end) {
        return end.getTime() - start.getTime();
    }

    public static void sleep(long pauseMills) {
        DateUtils.sleep(TimeUnit.MILLISECONDS, pauseMills);
    }

    public static void sleep(TimeUnit unit, long timeout) {
        if (timeout <= 0L) {
            return;
        }
        try {
            unit.sleep(timeout);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BootException(e);
        }
    }

    public static java.util.Date fromSql(Date date) {
        if (null == date) {
            return null;
        }
        return new java.util.Date(date.getTime());
    }

    public static Date toSqlDate(java.util.Date date) {
        if (null == date) {
            return null;
        }
        return new Date(date.getTime());
    }

    public static Time toSqlTime(java.util.Date date) {
        if (null == date) {
            return null;
        }
        return new Time(date.getTime());
    }

    public static Timestamp toSqlTimestamp(java.util.Date date) {
        if (null == date) {
            return null;
        }
        return new Timestamp(date.getTime());
    }

    public static java.util.Date addYear(java.util.Date date, int year) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        ((Calendar)calendar).add(1, year);
        return calendar.getTime();
    }

    public static java.util.Date addMonth(java.util.Date date, int month) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        ((Calendar)calendar).add(2, month);
        return calendar.getTime();
    }

    public static java.util.Date addDay(java.util.Date date, int day) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        ((Calendar)calendar).add(5, day);
        return calendar.getTime();
    }

    public static java.util.Date addHour(java.util.Date date, int hour) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        ((Calendar)calendar).add(11, hour);
        return calendar.getTime();
    }

    public static java.util.Date addMinute(java.util.Date date, int minute) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        ((Calendar)calendar).add(12, minute);
        return calendar.getTime();
    }

    public static java.util.Date addSecond(java.util.Date date, int second) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        ((Calendar)calendar).add(13, second);
        return calendar.getTime();
    }

    public static java.util.Date getCurrentDateDate() {
        return new java.util.Date();
    }

    public static Integer getDateHours(java.util.Date date) {
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(11);
    }

    public static int getCurrentDateHours() {
        java.util.Date now = DateUtils.getCurrentDateDate();
        return DateUtils.getDateHours(now);
    }

    public static boolean isAm() {
        int hours = DateUtils.getCurrentDateHours();
        return 0 <= hours && hours <= 12;
    }

    public static boolean isPm() {
        return !DateUtils.isAm();
    }

    public static List<String> derive(String str) {
        java.util.Date date = null;
        String code = null;
        for (Map.Entry<DateParser, String> entry : dateParsersMap.entrySet()) {
            DateParser dateParser = entry.getKey();
            date = dateParser.parse(str);
            if (date == null) continue;
            code = entry.getValue();
            break;
        }
        if (date == null) {
            return Collections.emptyList();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        ArrayList<String> list = new ArrayList<String>();
        for (Map.Entry<DateParser, String> entry : dateParsersMap.entrySet()) {
            DateParser dateParser;
            String value = entry.getValue();
            if (!code.contains(value) || (dateParser = entry.getKey()).getAmPm() != null && (dateParser.getAmPm().booleanValue() && calendar.get(9) == 1 || !dateParser.getAmPm().booleanValue() && calendar.get(9) == 0)) continue;
            String formatWithZeroPadding = dateParser.format(date, true);
            list.add(formatWithZeroPadding);
            String format = dateParser.format(date, false);
            if (format.equals(formatWithZeroPadding)) continue;
            list.add(format);
        }
        return list;
    }

    static {
        DATE_FORMAT.put("yyyy-MM-dd", DEFAULT_DATE_FORMAT_MATCHES);
        DATE_FORMAT.put(SLASH_DATE_FORMAT, SLASH_DATE_FORMAT_MATCHES);
        DATE_FORMAT.put("yyyy\u5e74MM\u6708dd\u65e5", DEFAULT_DATE_FORMAT_EN_MATCHES);
        dateParsersMap = new LinkedHashMap<DateParser, String>();
        dateParsersMap.put(new DateParser(DATE_TIME_FORMAT), "yMdHmsS");
        dateParsersMap.put(new DateParser("dd/MM/yyyy HH:mm:ss.SSS"), "yMdHmsS");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5HH\u65f6mm\u5206ss\u79d2SSS"), "yMdHmsS");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5HH\u70b9mm\u5206ss\u79d2SSS"), "yMdHmsS");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 HH\u65f6mm\u5206ss\u79d2SSS"), "yMdHmsS");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 HH\u70b9mm\u5206ss\u79d2SSS"), "yMdHmsS");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7HH\u65f6mm\u5206ss\u79d2SSS"), "yMdHmsS");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7HH\u70b9mm\u5206ss\u79d2SSS"), "yMdHmsS");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 HH\u65f6mm\u5206ss\u79d2SSS"), "yMdHmsS");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 HH\u70b9mm\u5206ss\u79d2SSS"), "yMdHmsS");
        dateParsersMap.put(new DateParser("yyyy-MM-dd HH:mm:ss"), "yMdHms");
        dateParsersMap.put(new DateParser("dd/MM/yyyy HH:mm:ss"), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5HH\u65f6mm\u5206ss\u79d2"), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5HH\u70b9mm\u5206ss\u79d2"), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 HH\u65f6mm\u5206ss\u79d2"), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 HH\u70b9mm\u5206ss\u79d2"), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5HH\u65f6mm\u5206ss"), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5HH\u70b9mm\u5206ss"), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 HH\u65f6mm\u5206ss"), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 HH\u70b9mm\u5206ss"), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7HH\u65f6mm\u5206ss\u79d2"), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7HH\u70b9mm\u5206ss\u79d2"), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 HH\u65f6mm\u5206ss\u79d2"), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 HH\u70b9mm\u5206ss\u79d2"), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7HH\u65f6mm\u5206ss"), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7HH\u70b9mm\u5206ss"), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 HH\u65f6mm\u5206ss"), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 HH\u70b9mm\u5206ss"), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy-MM-dd AM hh:mm:ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy-MM-dd PM hh:mm:ss", false), "yMdHms");
        dateParsersMap.put(new DateParser("dd/MM/yyyy AM hh:mm:ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("dd/MM/yyyy PM hh:mm:ss", false), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u51cc\u6668hh\u65f6mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u51cc\u6668hh\u70b9mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u65e9\u4e0ahh\u65f6mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u65e9\u4e0ahh\u70b9mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u65e9\u6668hh\u65f6mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u65e9\u6668hh\u70b9mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u65e9hh\u65f6mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u65e9hh\u70b9mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u4e0a\u5348hh\u65f6mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u4e0a\u5348hh\u70b9mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u4e0b\u5348hh\u65f6mm\u5206ss\u79d2", false), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u4e0b\u5348hh\u70b9mm\u5206ss\u79d2", false), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u51cc\u6668hh\u65f6mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u51cc\u6668hh\u70b9mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u65e9\u4e0ahh\u65f6mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u65e9\u4e0ahh\u70b9mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u65e9\u6668hh\u65f6mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u65e9\u6668hh\u70b9mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u65e9hh\u65f6mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u65e9hh\u70b9mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u4e0a\u5348hh\u65f6mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u4e0a\u5348hh\u70b9mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u4e0b\u5348hh\u65f6mm\u5206ss", false), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u4e0b\u5348hh\u70b9mm\u5206ss", false), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u51cc\u6668hh\u65f6mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u51cc\u6668hh\u70b9mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u65e9\u4e0a\u6668hh\u65f6mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u65e9\u4e0ahh\u70b9mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u65e9\u6668\u6668hh\u65f6mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u65e9\u6668hh\u70b9mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u65e9hh\u65f6mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u65e9hh\u70b9mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u4e0a\u5348hh\u65f6mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u4e0a\u5348hh\u70b9mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u4e0b\u5348hh\u65f6mm\u5206ss\u79d2", false), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u4e0b\u5348hh\u70b9mm\u5206ss\u79d2", false), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u51cc\u6668hh\u65f6mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u51cc\u6668hh\u70b9mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u65e9\u6668hh\u65f6mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u65e9\u6668hh\u70b9mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u65e9hh\u65f6mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u65e9hh\u70b9mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u4e0a\u5348hh\u65f6mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u4e0a\u5348hh\u70b9mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u4e0b\u5348hh\u65f6mm\u5206ss", false), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u4e0b\u5348hh\u70b9mm\u5206ss", false), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u51cc\u6668hh\u65f6mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u51cc\u6668hh\u70b9mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u65e9\u4e0ahh\u65f6mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u65e9\u4e0ahh\u70b9mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u65e9\u6668hh\u65f6mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u65e9\u6668hh\u70b9mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u65e9hh\u65f6mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u65e9hh\u70b9mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u4e0a\u5348hh\u65f6mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u4e0a\u5348hh\u70b9mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u4e0b\u5348hh\u65f6mm\u5206ss\u79d2", false), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u4e0b\u5348hh\u70b9mm\u5206ss\u79d2", false), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u51cc\u6668hh\u65f6mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u51cc\u6668hh\u70b9mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u65e9\u4e0ahh\u65f6mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u65e9\u4e0ahh\u70b9mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u65e9\u6668hh\u65f6mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u65e9\u6668hh\u70b9mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u65e9hh\u65f6mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u65e9hh\u70b9mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u4e0a\u5348hh\u65f6mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u4e0a\u5348hh\u70b9mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u4e0b\u5348hh\u65f6mm\u5206ss", false), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u4e0b\u5348hh\u70b9mm\u5206ss", false), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u51cc\u6668hh\u65f6mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u51cc\u6668hh\u70b9mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u65e9\u4e0ahh\u65f6mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u65e9\u4e0ahh\u70b9mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u65e9\u6668hh\u65f6mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u65e9\u6668hh\u70b9mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u65e9hh\u65f6mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u65e9hh\u70b9mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u4e0a\u5348hh\u65f6mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u4e0a\u5348hh\u70b9mm\u5206ss\u79d2", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u4e0b\u5348hh\u65f6mm\u5206ss\u79d2", false), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u4e0b\u5348hh\u70b9mm\u5206ss\u79d2", false), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u51cc\u6668hh\u65f6mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u51cc\u6668hh\u70b9mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u65e9\u4e0ahh\u65f6mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u65e9\u4e0ahh\u70b9mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u65e9\u6668hh\u65f6mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u65e9\u6668hh\u70b9mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u65e9hh\u65f6mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u65e9hh\u70b9mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u4e0a\u5348hh\u65f6mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u4e0a\u5348hh\u70b9mm\u5206ss", true), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u4e0b\u5348hh\u65f6mm\u5206ss", false), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u4e0b\u5348hh\u70b9mm\u5206ss", false), "yMdHms");
        dateParsersMap.put(new DateParser("yyyy-MM-dd HH:mm"), "yMdHm");
        dateParsersMap.put(new DateParser("dd/MM/yyyy HH:mm"), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5HH\u65f6mm\u5206"), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5HH\u70b9mm\u5206"), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 HH\u65f6mm\u5206"), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 HH\u70b9mm\u5206"), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5HH\u65f6mm"), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5HH\u70b9mm"), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 HH\u65f6mm"), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 HH\u70b9mm"), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7HH\u65f6mm\u5206"), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7HH\u70b9mm\u5206"), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 HH\u65f6mm\u5206"), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 HH\u70b9mm\u5206"), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7HH\u65f6mm"), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7HH\u70b9mm"), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 HH\u65f6mm"), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 HH\u70b9mm"), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy-MM-dd AM hh:mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy-MM-dd PM hh:mm", false), "yMdHm");
        dateParsersMap.put(new DateParser("dd/MM/yyyy AM hh:mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("dd/MM/yyyy PM hh:mm", false), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u51cc\u6668hh\u65f6mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u51cc\u6668hh\u70b9mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u65e9\u4e0ahh\u65f6mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u65e9\u4e0ahh\u70b9mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u65e9\u6668hh\u65f6mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u65e9\u6668hh\u70b9mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u65e9hh\u65f6mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u65e9hh\u70b9mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u4e0a\u5348hh\u65f6mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u4e0a\u5348hh\u70b9mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u4e0b\u5348hh\u65f6mm\u5206", false), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u4e0b\u5348hh\u70b9mm\u5206", false), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u51cc\u6668hh\u65f6mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u51cc\u6668hh\u70b9mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u65e9\u4e0ahh\u65f6mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u65e9\u4e0ahh\u70b9mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u65e9\u6668hh\u65f6mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u65e9\u6668hh\u70b9mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u65e9hh\u65f6mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u65e9hh\u70b9mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u4e0a\u5348hh\u65f6mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u4e0a\u5348hh\u70b9mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u4e0b\u5348hh\u65f6mm", false), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u4e0b\u5348hh\u70b9mm", false), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u51cc\u6668hh\u65f6mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u51cc\u6668hh\u70b9mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u65e9\u4e0ahh\u65f6mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u65e9\u4e0ahh\u70b9mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u65e9\u6668hh\u65f6mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u65e9\u6668hh\u70b9mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u65e9hh\u65f6mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u65e9hh\u70b9mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u4e0a\u5348hh\u65f6mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u4e0a\u5348hh\u70b9mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u4e0b\u5348hh\u65f6mm\u5206", false), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u4e0b\u5348hh\u70b9mm\u5206", false), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u51cc\u6668hh\u65f6mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u51cc\u6668hh\u70b9mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u65e9\u4e0ahh\u65f6mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u65e9\u4e0ahh\u70b9mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u65e9\u6668hh\u65f6mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u65e9\u6668hh\u70b9mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u65e9hh\u65f6mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u65e9hh\u70b9mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u4e0a\u5348hh\u65f6mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u4e0a\u5348hh\u70b9mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u4e0b\u5348hh\u65f6mm", false), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u4e0b\u5348hh\u70b9mm", false), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u51cc\u6668hh\u65f6mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u51cc\u6668hh\u70b9mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u65e9\u4e0ahh\u65f6mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u65e9\u4e0ahh\u70b9mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u65e9\u6668hh\u65f6mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u65e9\u6668hh\u70b9mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u65e9hh\u65f6mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u65e9hh\u70b9mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u4e0a\u5348hh\u65f6mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u4e0a\u5348hh\u70b9mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u4e0b\u5348hh\u65f6mm\u5206", false), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u4e0b\u5348hh\u70b9mm\u5206", false), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u51cc\u6668hh\u65f6mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u51cc\u6668hh\u70b9mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u65e9\u4e0ahh\u65f6mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u65e9\u4e0ahh\u70b9mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u65e9\u6668hh\u65f6mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u65e9\u6668hh\u70b9mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u65e9hh\u65f6mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u65e9hh\u70b9mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u4e0a\u5348hh\u65f6mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u4e0a\u5348hh\u70b9mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u4e0b\u5348hh\u65f6mm", false), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u4e0b\u5348hh\u70b9mm", false), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u51cc\u6668hh\u65f6mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u51cc\u6668hh\u70b9mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u65e9\u4e0ahh\u65f6mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u65e9\u4e0ahh\u70b9mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u65e9\u6668hh\u65f6mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u65e9\u6668hh\u70b9mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u65e9hh\u65f6mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u65e9hh\u70b9mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u4e0a\u5348hh\u65f6mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u4e0a\u5348hh\u70b9mm\u5206", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u4e0b\u5348hh\u65f6mm\u5206", false), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u4e0b\u5348hh\u70b9mm\u5206", false), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u51cc\u6668hh\u65f6mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u51cc\u6668hh\u70b9mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u65e9\u4e0ahh\u65f6mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u65e9\u4e0ahh\u70b9mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u65e9\u6668hh\u65f6mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u65e9\u6668hh\u70b9mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u65e9hh\u65f6mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u65e9hh\u70b9mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u4e0a\u5348hh\u65f6mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u4e0a\u5348hh\u70b9mm", true), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u4e0b\u5348hh\u65f6mm", false), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u4e0b\u5348hh\u70b9mm", false), "yMdHm");
        dateParsersMap.put(new DateParser("yyyy-MM-dd HH o'clock"), "yMdH");
        dateParsersMap.put(new DateParser("dd/MM/yyyy HH o'clock"), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5HH\u65f6"), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5HH\u70b9"), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 HH\u65f6"), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 HH\u70b9"), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5HH\u65f6"), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5HH\u70b9"), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 HH\u65f6"), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 HH\u70b9"), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7HH\u65f6"), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7HH\u70b9"), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 HH\u65f6"), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 HH\u70b9"), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7HH\u65f6"), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7HH\u70b9"), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 HH\u65f6"), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 HH\u70b9"), "yMdH");
        dateParsersMap.put(new DateParser("yyyy-MM-dd AM hh o'clock", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy-MM-dd PM hh o'clock", false), "yMdH");
        dateParsersMap.put(new DateParser("dd/MM/yyyy AM hh o'clock", true), "yMdH");
        dateParsersMap.put(new DateParser("dd/MM/yyyy PM hh o'clock", false), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u51cc\u6668hh\u65f6", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u51cc\u6668hh\u70b9", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u65e9\u4e0ahh\u65f6", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u65e9\u4e0ahh\u70b9", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u65e9\u6668hh\u65f6", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u65e9\u6668hh\u70b9", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u65e9hh\u65f6", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u65e9hh\u70b9", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u4e0a\u5348hh\u65f6", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u4e0a\u5348hh\u70b9", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u4e0b\u5348hh\u65f6", false), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5\u4e0b\u5348hh\u70b9", false), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u51cc\u6668hh\u65f6", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u51cc\u6668hh\u70b9", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u65e9\u4e0ahh\u65f6", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u65e9\u4e0ahh\u70b9", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u65e9\u6668hh\u65f6", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u65e9\u6668hh\u70b9", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u65e9hh\u65f6", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u65e9hh\u70b9", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u4e0a\u5348hh\u65f6", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u4e0a\u5348hh\u70b9", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u4e0b\u5348hh\u65f6", false), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5 \u4e0b\u5348hh\u70b9", false), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u51cc\u6668hh\u65f6", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u51cc\u6668hh\u70b9", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u65e9\u4e0ahh\u65f6", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u65e9\u4e0ahh\u70b9", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u65e9\u6668hh\u65f6", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u65e9\u6668hh\u70b9", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u65e9hh\u65f6", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u65e9hh\u70b9", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u4e0a\u5348hh\u65f6", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u4e0a\u5348hh\u70b9", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u4e0b\u5348hh\u65f6", false), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7\u4e0b\u5348hh\u70b9", false), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u51cc\u6668hh\u65f6", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u51cc\u6668hh\u70b9", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u65e9\u4e0ahh\u65f6", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u65e9\u4e0ahh\u70b9", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u65e9\u6668hh\u65f6", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u65e9\u6668hh\u70b9", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u65e9hh\u65f6", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u65e9hh\u70b9", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u4e0a\u5348hh\u65f6", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u4e0a\u5348hh\u70b9", true), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u4e0b\u5348hh\u65f6", false), "yMdH");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7 \u4e0b\u5348hh\u70b9", false), "yMdH");
        dateParsersMap.put(new DateParser("yyyyMMdd"), "yMd");
        dateParsersMap.put(new DateParser("yyyy-MM-dd"), "yMd");
        dateParsersMap.put(new DateParser("yyyy.MM.dd"), "yMd");
        dateParsersMap.put(new DateParser("MM.dd,yyyy"), "yMd");
        dateParsersMap.put(new DateParser("MM.dd, yyyy"), "yMd");
        dateParsersMap.put(new DateParser("dd/MM/yyyy"), "yMd");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u65e5"), "yMd");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd\u53f7"), "yMd");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM\u6708dd"), "yMd");
        dateParsersMap.put(new DateParser("yyyy\u00b7MM\u00b7dd"), "yMd");
        dateParsersMap.put(new DateParser("yyyy\u5e74\u00b7MM\u6708\u00b7dd\u65e5"), "yMd");
        dateParsersMap.put(new DateParser("yyyy\u5e74\u00b7MM\u6708\u00b7dd\u53f7"), "yMd");
        dateParsersMap.put(new DateParser(DEFAULT_MONTH_FORMAT_EN), "yM");
        dateParsersMap.put(new DateParser("yyyy\u5e74MM"), "yM");
        dateParsersMap.put(new DateParser("yyyy\u00b7MM"), "yM");
        dateParsersMap.put(new DateParser("yyyy\u5e74\u00b7MM\u6708"), "yM");
        dateParsersMap.put(new DateParser("MM-dd HH:mm:ss.SSS"), "MdHmsS");
        dateParsersMap.put(new DateParser("dd/MM HH:mm:ss.SSS"), "MdHmsS");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5HH\u65f6mm\u5206ss\u79d2SSS"), "MdHmsS");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5HH\u70b9mm\u5206ss\u79d2SSS"), "MdHmsS");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 HH\u65f6mm\u5206ss\u79d2SSS"), "MdHmsS");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 HH\u70b9mm\u5206ss\u79d2SSS"), "MdHmsS");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7HH\u65f6mm\u5206ss\u79d2SSS"), "MdHmsS");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7HH\u70b9mm\u5206ss\u79d2SSS"), "MdHmsS");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 HH\u65f6mm\u5206ss\u79d2SSS"), "MdHmsS");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 HH\u70b9mm\u5206ss\u79d2SSS"), "MdHmsS");
        dateParsersMap.put(new DateParser("MM-dd HH:mm:ss"), "MdHms");
        dateParsersMap.put(new DateParser("dd/MM HH:mm:ss"), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5HH\u65f6mm\u5206ss\u79d2"), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5HH\u70b9mm\u5206ss\u79d2"), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 HH\u65f6mm\u5206ss\u79d2"), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 HH\u70b9mm\u5206ss\u79d2"), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5HH\u65f6mm\u5206ss"), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5HH\u70b9mm\u5206ss"), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 HH\u65f6mm\u5206ss"), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 HH\u70b9mm\u5206ss"), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7HH\u65f6mm\u5206ss\u79d2"), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7HH\u70b9mm\u5206ss\u79d2"), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 HH\u65f6mm\u5206ss\u79d2"), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 HH\u70b9mm\u5206ss\u79d2"), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7HH\u65f6mm\u5206ss"), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7HH\u70b9mm\u5206ss"), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 HH\u65f6mm\u5206ss"), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 HH\u70b9mm\u5206ss"), "MdHms");
        dateParsersMap.put(new DateParser("MM-dd AM hh:mm:ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM-dd PM hh:mm:ss", false), "MdHms");
        dateParsersMap.put(new DateParser("dd/MM AM hh:mm:ss", true), "MdHms");
        dateParsersMap.put(new DateParser("dd/MM PM hh:mm:ss", false), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u51cc\u6668hh\u65f6mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u51cc\u6668hh\u70b9mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u65e9\u4e0ahh\u65f6mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u65e9\u4e0ahh\u70b9mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u65e9\u6668hh\u65f6mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u65e9\u6668hh\u70b9mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u65e9hh\u65f6mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u65e9hh\u70b9mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u4e0a\u5348hh\u65f6mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u4e0a\u5348hh\u70b9mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u4e0b\u5348hh\u65f6mm\u5206ss\u79d2", false), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u4e0b\u5348hh\u70b9mm\u5206ss\u79d2", false), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u51cc\u6668hh\u65f6mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u51cc\u6668hh\u70b9mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u65e9\u4e0ahh\u65f6mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u65e9\u4e0ahh\u70b9mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u65e9\u6668hh\u65f6mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u65e9\u6668hh\u70b9mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u65e9hh\u65f6mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u65e9hh\u70b9mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u4e0a\u5348hh\u65f6mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u4e0a\u5348hh\u70b9mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u4e0b\u5348hh\u65f6mm\u5206ss", false), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u4e0b\u5348hh\u70b9mm\u5206ss", false), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u51cc\u6668hh\u65f6mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u51cc\u6668hh\u70b9mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u65e9\u4e0ahh\u65f6mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u65e9\u4e0ahh\u70b9mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u65e9\u6668hh\u65f6mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u65e9\u6668hh\u70b9mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u65e9hh\u65f6mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u65e9hh\u70b9mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u4e0a\u5348hh\u65f6mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u4e0a\u5348hh\u70b9mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u4e0b\u5348hh\u65f6mm\u5206ss\u79d2", false), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u4e0b\u5348hh\u70b9mm\u5206ss\u79d2", false), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u51cc\u6668hh\u65f6mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u51cc\u6668hh\u70b9mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u65e9\u4e0ahh\u65f6mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u65e9\u4e0ahh\u70b9mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u65e9\u6668hh\u65f6mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u65e9\u6668hh\u70b9mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u65e9hh\u65f6mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u65e9hh\u70b9mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u4e0a\u5348hh\u65f6mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u4e0a\u5348hh\u70b9mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u4e0b\u5348hh\u65f6mm\u5206ss", false), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u4e0b\u5348hh\u70b9mm\u5206ss", false), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u51cc\u6668hh\u65f6mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u51cc\u6668hh\u70b9mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u65e9\u4e0ahh\u65f6mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u65e9\u4e0ahh\u70b9mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u65e9\u6668hh\u65f6mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u65e9\u6668hh\u70b9mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u65e9hh\u65f6mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u65e9hh\u70b9mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u4e0a\u5348hh\u65f6mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u4e0a\u5348hh\u70b9mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u4e0b\u5348hh\u65f6mm\u5206ss\u79d2", false), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u4e0b\u5348hh\u70b9mm\u5206ss\u79d2", false), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u51cc\u6668hh\u65f6mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u51cc\u6668hh\u70b9mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u65e9\u4e0ahh\u65f6mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u65e9\u4e0ahh\u70b9mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u65e9\u6668hh\u65f6mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u65e9\u6668hh\u70b9mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u65e9hh\u65f6mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u65e9hh\u70b9mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u4e0a\u5348hh\u65f6mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u4e0a\u5348hh\u70b9mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u4e0b\u5348hh\u65f6mm\u5206ss", false), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u4e0b\u5348hh\u70b9mm\u5206ss", false), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u51cc\u6668hh\u65f6mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u51cc\u6668hh\u70b9mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u65e9\u4e0ahh\u65f6mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u65e9\u4e0ahh\u70b9mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u65e9\u6668hh\u65f6mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u65e9\u6668hh\u70b9mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u65e9hh\u65f6mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u65e9hh\u70b9mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u4e0a\u5348hh\u65f6mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u4e0a\u5348hh\u70b9mm\u5206ss\u79d2", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u4e0b\u5348hh\u65f6mm\u5206ss\u79d2", false), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u4e0b\u5348hh\u70b9mm\u5206ss\u79d2", false), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u51cc\u6668hh\u65f6mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u51cc\u6668hh\u70b9mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u65e9\u4e0ahh\u65f6mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u65e9\u4e0ahh\u70b9mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u65e9\u6668hh\u65f6mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u65e9\u6668hh\u70b9mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u65e9hh\u65f6mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u65e9hh\u70b9mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u4e0a\u5348hh\u65f6mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u4e0a\u5348hh\u70b9mm\u5206ss", true), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u4e0b\u5348hh\u65f6mm\u5206ss", false), "MdHms");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u4e0b\u5348hh\u70b9mm\u5206ss", false), "MdHms");
        dateParsersMap.put(new DateParser("MM-dd HH:mm"), "MdHm");
        dateParsersMap.put(new DateParser("dd/MM HH:mm"), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5HH\u65f6mm\u5206"), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5HH\u70b9mm\u5206"), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 HH\u65f6mm\u5206"), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 HH\u70b9mm\u5206"), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5HH\u65f6mm"), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5HH\u70b9mm"), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 HH\u65f6mm"), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 HH\u70b9mm"), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7HH\u65f6mm\u5206"), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7HH\u70b9mm\u5206"), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 HH\u65f6mm\u5206"), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 HH\u70b9mm\u5206"), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7HH\u65f6mm"), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7HH\u70b9mm"), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 HH\u65f6mm"), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 HH\u70b9mm"), "MdHm");
        dateParsersMap.put(new DateParser("MM-dd AM hh:mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM-dd PM hh:mm", false), "MdHm");
        dateParsersMap.put(new DateParser("dd/MM AM hh:mm", true), "MdHm");
        dateParsersMap.put(new DateParser("dd/MM PM hh:mm", false), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u51cc\u6668hh\u65f6mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u51cc\u6668hh\u70b9mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u65e9\u4e0ahh\u65f6mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u65e9\u4e0ahh\u70b9mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u65e9\u6668hh\u65f6mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u65e9\u6668hh\u70b9mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u65e9hh\u65f6mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u65e9hh\u70b9mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u4e0a\u5348hh\u65f6mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u4e0a\u5348hh\u70b9mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u4e0b\u5348hh\u65f6mm\u5206", false), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u4e0b\u5348hh\u70b9mm\u5206", false), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u51cc\u6668hh\u65f6mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u51cc\u6668hh\u70b9mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u65e9\u4e0ahh\u65f6mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u65e9\u4e0ahh\u70b9mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u65e9\u6668hh\u65f6mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u65e9\u6668hh\u70b9mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u65e9hh\u65f6mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u65e9hh\u70b9mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u4e0a\u5348hh\u65f6mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u4e0a\u5348hh\u70b9mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u4e0b\u5348hh\u65f6mm", false), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u4e0b\u5348hh\u70b9mm", false), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u51cc\u6668hh\u65f6mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u51cc\u6668hh\u70b9mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u65e9\u4e0ahh\u65f6mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u65e9\u4e0ahh\u70b9mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u65e9\u6668hh\u65f6mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u65e9\u6668hh\u70b9mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u65e9hh\u65f6mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u65e9hh\u70b9mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u4e0a\u5348hh\u65f6mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u4e0a\u5348hh\u70b9mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u4e0b\u5348hh\u65f6mm\u5206", false), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u4e0b\u5348hh\u70b9mm\u5206", false), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u51cc\u6668hh\u65f6mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u51cc\u6668hh\u70b9mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u65e9\u4e0ahh\u65f6mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u65e9\u4e0ahh\u70b9mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u65e9\u6668hh\u65f6mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u65e9\u6668hh\u70b9mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u65e9hh\u65f6mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u65e9hh\u70b9mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u4e0a\u5348hh\u65f6mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u4e0a\u5348hh\u70b9mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u4e0b\u5348hh\u65f6mm", false), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u4e0b\u5348hh\u70b9mm", false), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u51cc\u6668hh\u65f6mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u51cc\u6668hh\u70b9mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u65e9\u4e0ahh\u65f6mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u65e9\u4e0ahh\u70b9mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u65e9\u6668hh\u65f6mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u65e9\u6668hh\u70b9mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u65e9hh\u65f6mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u65e9hh\u70b9mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u4e0a\u5348hh\u65f6mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u4e0a\u5348hh\u70b9mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u4e0b\u5348hh\u65f6mm\u5206", false), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u4e0b\u5348hh\u70b9mm\u5206", false), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u51cc\u6668hh\u65f6mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u51cc\u6668hh\u70b9mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u65e9\u4e0ahh\u65f6mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u65e9\u4e0ahh\u70b9mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u65e9\u6668hh\u65f6mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u65e9\u6668hh\u70b9mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u65e9hh\u65f6mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u65e9hh\u70b9mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u4e0a\u5348hh\u65f6mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u4e0a\u5348hh\u70b9mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u4e0b\u5348hh\u65f6mm", false), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u4e0b\u5348hh\u70b9mm", false), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u51cc\u6668hh\u65f6mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u51cc\u6668hh\u70b9mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u65e9\u4e0ahh\u65f6mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u65e9\u4e0ahh\u70b9mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u65e9\u6668hh\u65f6mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u65e9\u6668hh\u70b9mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u65e9hh\u65f6mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u65e9hh\u70b9mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u4e0a\u5348hh\u65f6mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u4e0a\u5348hh\u70b9mm\u5206", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u4e0b\u5348hh\u65f6mm\u5206", false), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u4e0b\u5348hh\u70b9mm\u5206", false), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u51cc\u6668hh\u65f6mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u51cc\u6668hh\u70b9mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u65e9\u4e0ahh\u65f6mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u65e9\u4e0ahh\u70b9mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u65e9\u6668hh\u65f6mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u65e9\u6668hh\u70b9mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u65e9hh\u65f6mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u65e9hh\u70b9mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u4e0a\u5348hh\u65f6mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u4e0a\u5348hh\u70b9mm", true), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u4e0b\u5348hh\u65f6mm", false), "MdHm");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u4e0b\u5348hh\u70b9mm", false), "MdHm");
        dateParsersMap.put(new DateParser("MM-dd HH o'clock"), "MdH");
        dateParsersMap.put(new DateParser("dd/MM HH o'clock"), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5HH\u65f6"), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5HH\u70b9"), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 HH\u65f6"), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 HH\u70b9"), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5HH\u65f6"), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5HH\u70b9"), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 HH\u65f6"), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 HH\u70b9"), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7HH\u65f6"), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7HH\u70b9"), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 HH\u65f6"), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 HH\u70b9"), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7HH\u65f6"), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7HH\u70b9"), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 HH\u65f6"), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 HH\u70b9"), "MdH");
        dateParsersMap.put(new DateParser("MM-dd AM hh o'clock", true), "MdH");
        dateParsersMap.put(new DateParser("MM-dd PM hh o'clock", false), "MdH");
        dateParsersMap.put(new DateParser("dd/MM AM hh o'clock", true), "MdH");
        dateParsersMap.put(new DateParser("dd/MM PM hh o'clock", false), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u51cc\u6668hh\u65f6", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u51cc\u6668hh\u70b9", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u65e9\u4e0ahh\u65f6", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u65e9\u4e0ahh\u70b9", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u65e9\u6668hh\u65f6", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u65e9\u6668hh\u70b9", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u65e9hh\u65f6", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u65e9hh\u70b9", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u4e0a\u5348hh\u65f6", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u4e0a\u5348hh\u70b9", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u4e0b\u5348hh\u65f6", false), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5\u4e0b\u5348hh\u70b9", false), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u51cc\u6668hh\u65f6", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u51cc\u6668hh\u70b9", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u65e9\u4e0ahh\u65f6", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u65e9\u4e0ahh\u70b9", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u65e9\u6668hh\u65f6", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u65e9\u6668hh\u70b9", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u65e9hh\u65f6", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u65e9hh\u70b9", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u4e0a\u5348hh\u65f6", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u4e0a\u5348hh\u70b9", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u4e0b\u5348hh\u65f6", false), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5 \u4e0b\u5348hh\u70b9", false), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u51cc\u6668hh\u65f6", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u51cc\u6668hh\u70b9", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u65e9\u4e0ahh\u65f6", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u65e9\u4e0ahh\u70b9", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u65e9\u6668hh\u65f6", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u65e9\u6668hh\u70b9", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u65e9hh\u65f6", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u65e9hh\u70b9", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u4e0a\u5348hh\u65f6", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u4e0a\u5348hh\u70b9", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u4e0b\u5348hh\u65f6", false), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7\u4e0b\u5348hh\u70b9", false), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u51cc\u6668hh\u65f6", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u51cc\u6668hh\u70b9", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u65e9\u4e0ahh\u65f6", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u65e9\u4e0ahh\u70b9", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u65e9\u6668hh\u65f6", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u65e9\u6668hh\u70b9", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u65e9hh\u65f6", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u65e9hh\u70b9", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u4e0a\u5348hh\u65f6", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u4e0a\u5348hh\u70b9", true), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u4e0b\u5348hh\u65f6", false), "MdH");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7 \u4e0b\u5348hh\u70b9", false), "MdH");
        dateParsersMap.put(new DateParser("MM-dd"), "Md");
        dateParsersMap.put(new DateParser("MM.dd"), "Md");
        dateParsersMap.put(new DateParser("dd/MM"), "Md");
        dateParsersMap.put(new DateParser("MM\u6708dd\u65e5"), "Md");
        dateParsersMap.put(new DateParser("MM\u6708dd\u53f7"), "Md");
        dateParsersMap.put(new DateParser("MM\u6708dd"), "Md");
        dateParsersMap.put(new DateParser("MM\u00b7dd"), "Md");
        dateParsersMap.put(new DateParser("MM\u6708\u00b7dd\u65e5"), "Md");
        dateParsersMap.put(new DateParser("MM\u6708\u00b7dd\u53f7"), "Md");
        dateParsersMap.put(new DateParser(DEFAULT_YEAR_FORMAT), "y");
        dateParsersMap.put(new DateParser("yyyy\u5e74"), "y");
    }
}

