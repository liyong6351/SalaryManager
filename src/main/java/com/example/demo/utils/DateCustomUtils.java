package com.example.demo.utils;

import java.text.Format;
import java.util.Calendar;
import java.util.Date;

public class DateCustomUtils {
    private final static String FORMAT_1 = "";

    private final static String FORMAT_MONTH = "";

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

    public static String getMonth(String month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, Integer.parseInt(month));
        calendar.set(Calendar.DAY_OF_MONTH, 1));


        return Format.Field;
    }
}
