package com.example.demo.utils;

import lombok.extern.slf4j.Slf4j;

import java.text.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

@Slf4j
public class DateCustomUtils {
    private final static String FORMAT_SECOND = "YYYY-MM-dd HH:mm:ss";

    private final static String FORMAT_DAY = "YYYY-MM-dd";

    private final static String FORMAT_MONTH = "YYYY-MM";

    private final static String FORMAT_SHORT = "yyyy/M/d";

    /**
     * 输入参数 07:30 转换为 2025-07-26 07:30:00 对应的日期
     *
     * @param arg 输入参数 hh:mm 格式
     * @return yyyy-mm-dd hh:mm:ss 格式
     */
    public static Date trans4Plan(int month, String arg, int addDate) {
        String[] split = arg.split("-", 0);
        String[] split1 = split[0].split(":", 0);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(split1[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(split1[1]));
        calendar.add(Calendar.DATE, addDate);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public static String getMonth(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_MONTH);
        return simpleDateFormat.format(date);
    }

    /**
     * 将 @FORMAT_SHORT (YYYY/M/d) 格式的日期转化为日期
     *
     * @param str 2025/6/7
     * @return Date
     */
    public static Date transFormat4Short(String str) {
        // 步骤 1：解析为 LocalDate
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMAT_SHORT);
        LocalDate localDate = LocalDate.parse(str, formatter);
        // 步骤 2：转换为 Date（如需旧版 Date 对象）
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 将 @FORMAT_SHORT (YYYY/M/d) 格式的日期转化为日期
     *
     * @param str 2025/6/7
     * @return Date
     */
    public static Date transFormat4Day(String str) {
        str = str.replaceAll("\t", "").replaceAll("\\(", "").replaceAll("\\)", "").trim();
        Date parse = transFormat4DayYYYYMMDD(str);
        if (parse == null) {
            parse = transFormat4DayYYYY_MM_DD(str);
        }
        if (parse == null) {
            parse = transFormat4DayMMDD(str);
        }
        if (parse == null) {
            parse = transFormat4DayMMDD1(str);
        }
        return parse;
    }

    private static Date transFormat4DayYYYYMMDD(String str) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date parse;
        try {
            parse = simpleDateFormat.parse(str);
        } catch (ParseException e) {
            return null;
        }
        return parse;
    }

    private static Date transFormat4DayYYYY_MM_DD(String str) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date parse;
        try {
            parse = simpleDateFormat.parse(str);
        } catch (ParseException e) {
            return null;
        }
        return parse;
    }

    private static Date transFormat4DayMMDD(String str) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date parse;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        str = year + "/" + str;
        try {
            parse = simpleDateFormat.parse(str);
        } catch (ParseException ex) {
            return null;
        }
        return parse;
    }

    private static Date transFormat4DayMMDD1(String str) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date parse;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        str = year + "-" + str;
        try {
            parse = simpleDateFormat.parse(str);
        } catch (ParseException ex) {
            return null;
        }
        return parse;
    }

    public static Date transFormat4Time(String str) {
        // 步骤 1：解析为 LocalDate
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parse;
        try {
            parse = simpleDateFormat.parse(str);
        } catch (ParseException e) {
            return new Date();
        }
        return parse;
    }

    public static String transFormat4Day(Date date) {
        SimpleDateFormat srcDateFormat = new SimpleDateFormat(FORMAT_DAY);
        return srcDateFormat.format(date);
    }

    public static String transFormat4DayShort(Date date) {
        SimpleDateFormat srcDateFormat = new SimpleDateFormat(FORMAT_SHORT);
        return srcDateFormat.format(date);
    }

    public static String transFormat4YYYYMMDDHHMISS(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat srcDateFormat = new SimpleDateFormat(FORMAT_SECOND);
        return srcDateFormat.format(date);
    }

    public static int getDays4Month(int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DATE, 1); // 设置年份和月份，日期设为1号
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }
}
