package com.example.demo.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;

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

    private final static String FORMAT_MONTH = "YYYY-MM-dd MM";

    private final static String FORMAT_SHORT = "yyyy/M/d";

    /**
     * 输入参数 07:30 转换为 2025-07-26 07:30:00 对应的日期
     *
     * @param arg 输入参数 hh:mm 格式
     * @return yyyy-mm-dd hh:mm:ss 格式
     */
    public static Date trans4Plan(String arg) {
        String[] split = arg.split("-");
        String[] split1 = split[0].split(":");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(split1[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(split1[1]));
        return calendar.getTime();
    }

    public static String getDay(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_DAY);
        return simpleDateFormat.format(date);
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
     * @throws ParseException 转换失败
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
     * @throws ParseException 转换失败
     */
    public static Date transFormat4Day(String str) {
        // 步骤 1：解析为 LocalDate
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(str, formatter);
        // 步骤 2：转换为 Date（如需旧版 Date 对象）
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Date transFormat4Time(String str) {
        // 步骤 1：解析为 LocalDate
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parse = null;
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

    public static void main(String[] args) throws ParseException {
        String dateString = "2023/1/5"; // 月份和日不补零

        // 步骤 1：解析为 LocalDate
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/M/d");
        LocalDate localDate = LocalDate.parse(dateString, formatter);

        // 步骤 2：转换为 Date（如需旧版 Date 对象）
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        System.out.println("Parsed Date: " + date);
    }
}
