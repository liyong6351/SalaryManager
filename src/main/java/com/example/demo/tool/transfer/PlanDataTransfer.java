package com.example.demo.tool.transfer;

import com.alibaba.fastjson.JSON;
import com.example.demo.model.PlanDataModel;
import com.example.demo.model.common.StartTimeEndTimeModel;
import com.example.demo.utils.DateCustomUtils;
import com.example.demo.utils.RedisUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.example.demo.utils.DateCustomUtils.transFormat4Short;

@Component(value = "plan")
public class PlanDataTransfer extends AbstractExcelDataTransfer<PlanDataModel> {

    private final static Map<String, String> characterMap = new HashMap<>();

    private final StringRedisTemplate stringRedisTemplate;

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

    public PlanDataTransfer(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    protected List<PlanDataModel> doTransfer(List<Map<String, String>> dataList) {
        Map<String, Map<String, StartTimeEndTimeModel>> planAllData = transferRule4Month(dataList.get(2));

        Map<String, String> keyMap = transferRule4KeyMap(dataList.get(3));
        List<PlanDataModel> list = new ArrayList<>();
        for (int i = 4; i < dataList.size(); i++) {
            Map<String, String> map = dataList.get(i);
            list.addAll(transferSingData(planAllData, keyMap, map));
        }
        return list;
    }

    private List<PlanDataModel> transferSingData(Map<String, Map<String, StartTimeEndTimeModel>> allPlan, Map<String, String> keyMap, Map<String, String> map) {
        List<PlanDataModel> result = new ArrayList<>();
        map.remove("lineNumber");
        map.remove("0");
        map.remove("1");
        map.remove("2");
        String name = map.get("3");
        map.remove("3");
        System.out.println(JSON.toJSONString(allPlan));
        map.forEach((k, v) -> {
            PlanDataModel model = new PlanDataModel();
            model.setName(name);
            model.setType(v);
            model.setDate(transFormat4Short(keyMap.get(k)));
            String date = DateCustomUtils.transFormat4Day(model.getDate());
            if (allPlan.get(date) != null && allPlan.get(date).get(model.getType()) != null) {
                StartTimeEndTimeModel startTimeEndTimeModel = allPlan.get(date).get(model.getType());
                model.setStartTime(startTimeEndTimeModel.getStartTime());
                model.setEndTime(startTimeEndTimeModel.getEndTime());
            } else {
                System.out.println(date + " || " + "allPlan" + JSON.toJSONString(allPlan));
                System.out.println("hello");
                model.setDescription("type is " + model.getType());
            }
            model.setDescription("排班是" + model.getType());
            result.add(model);
        });

        return result;
    }

    private Map<String, String> transferRule4KeyMap(Map<String, String> map) {
        Map<String, String> result = new HashMap<>();
        map.forEach((k, v) -> {
            result.put(k, etl4Key(v).replace("\n", ""));
        });
        return result;
    }

    private Map<String, Map<String, StartTimeEndTimeModel>> transferRule4Month(Map<String, String> stringStringMap) {
        Map<String, StartTimeEndTimeModel> result = new HashMap<>();
        // 7月排班信息 班次A（早班） 07:30-17:00 班次B： 14:00-22:00（中班） 休: 当天休息
        String text = stringStringMap.get("0");

        String[] split = text.split(" ");
        int month = getMonthDate(split[0]);

        String firstKey = etl4Key(split[1]);
        String[] s1 = split[2].split("-");
        Date firstKeyStartDate = DateCustomUtils.trans4Plan(s1[0]);
        Date firstKeyEndDate = DateCustomUtils.trans4Plan(s1[1]);
        String secondKey = etl4Key(split[3]);
        String[] s2 = split[4].split("-");
        Date secondKeyStartDate = DateCustomUtils.trans4Plan(s2[0]);
        Date secondKeyEndDate = DateCustomUtils.trans4Plan(s2[1]);
        String thirdKey = etl4Key(split[5]);

        StartTimeEndTimeModel firstKeyData = new StartTimeEndTimeModel(firstKeyStartDate, firstKeyEndDate);
        StartTimeEndTimeModel secondKeyData = new StartTimeEndTimeModel(secondKeyStartDate, secondKeyEndDate);
        StartTimeEndTimeModel thirdKeyData = new StartTimeEndTimeModel(null, null);
        result.put(firstKey, firstKeyData);
        result.put(secondKey, secondKeyData);
        result.put(thirdKey, thirdKeyData);

        return restorePlan4Days(month, result);
    }

    private Map<String, Map<String, StartTimeEndTimeModel>> restorePlan4Days(int month, Map<String, StartTimeEndTimeModel> data) {

        Map<String, Map<String, StartTimeEndTimeModel>> tempData = new HashMap<>();
        for (String key : data.keySet()) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MONTH, month - 1);
            calendar.set(Calendar.DATE, 1);
            calendar.add(Calendar.MONTH, 1);
            calendar.add(Calendar.DATE, -1);
            int realMonth = calendar.get(Calendar.MONTH);
            do {
                tempData.put(DateCustomUtils.getDay(calendar.getTime()), data);
                calendar.add(Calendar.DATE, -1);
            } while (realMonth == calendar.get(Calendar.MONTH));
        }
        tempData.forEach((k, v) -> {
            RedisUtils.storePlanData(stringRedisTemplate, k, v);
        });
        return tempData;
    }

    private int getMonthDate(String s) {
        String month = etl4Key(s);
        return Integer.parseInt(month);
    }

    private static String etl4Key(String src) {
        for (String key : characterMap.keySet()) {
            src = src.replace(key, characterMap.get(key));
        }
        return src;
    }

    public static void main(String[] args) {
        String a = "7月排班信息 班次A（早班） 07:30-17:00 班次B： 14:00-22:00（中班） 休: 当天休息";
        String[] split = a.split(" ");
        System.out.println("hello");
    }
}
