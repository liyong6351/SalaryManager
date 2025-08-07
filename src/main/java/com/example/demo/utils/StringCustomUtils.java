package com.example.demo.utils;

import com.example.demo.model.common.StartTimeEndTimeModel;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class StringCustomUtils {

    private final static Map<String, String> characterMap = new HashMap<>();

    static {
        characterMap.put("早", "");
        characterMap.put("中", "");
        characterMap.put("晚", "");
        characterMap.put("班", "");
        characterMap.put("次", "");
        characterMap.put("月", "");
        characterMap.put("排", "");
        characterMap.put("信", "");
        characterMap.put("息", "");
        characterMap.put("周", "");
        characterMap.put("一", "");
        characterMap.put("二", "");
        characterMap.put("三", "");
        characterMap.put("四", "");
        characterMap.put("五", "");
        characterMap.put("六", "");
        characterMap.put("日", "");
        characterMap.put("（", "");
        characterMap.put("(", "");
        characterMap.put(")", "");
        characterMap.put("）", "");
    }

    public static List<String> splitString4HHmm(String input) {
        List<String> parts = new ArrayList<>();
        if (StringUtils.isEmpty(input)) {
            return parts;
        }
        if (!input.contains("\n")) {
            return Arrays.asList(splitEvery5Chars(input));
        }

        String[] split = input.split("\n", 0);
        return Arrays.asList(split);
    }

    public static String[] splitEvery5Chars(String str) {
        // 计算需要分割的数组长度（向上取整）
        int arrayLength = (str.length() + 3) / 5;
        String[] result = new String[arrayLength];

        // 遍历字符串并分割
        for (int i = 0; i < arrayLength; i++) {
            int start = i * 5;
            int end = Math.min(start + 5, str.length());  // 防止索引越界
            result[i] = str.substring(start, end);
        }

        return result;
    }

    public static String clearGroupConcat(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        StringBuilder result = new StringBuilder();
        String[] split = str.split(";", 0);
        for (String s : split) {
            if (StringUtils.isNotBlank(s)) {
                result.append(s).append("|");
            }
        }
        return result.toString();
    }


    public static String etl4Key(String src) {
        for (String key : characterMap.keySet()) {
            src = src.replace(key, characterMap.get(key));
        }
        return src;
    }


    public static int getMonthDate(String s) {
        String month = etl4Key(s);
        return Integer.parseInt(month);
    }

    public static StartTimeEndTimeModel dealTime(String str, int month) {
        Date startDate = null;
        Date endDate = null;
        if (str.contains("-")) {
            String[] s1 = str.split("-", 0);
            startDate = DateCustomUtils.trans4Plan(month, s1[0]);
            endDate = DateCustomUtils.trans4Plan(month, s1[1]);
            if (!startDate.before(endDate)) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(endDate);
                calendar.add(Calendar.DATE, 1);
                endDate = calendar.getTime();
            }
        }
        return new StartTimeEndTimeModel(startDate, endDate);
    }
}
