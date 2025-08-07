package com.example.demo.tool.transfer;

import com.example.demo.model.PlanDataModel;
import com.example.demo.model.common.StartTimeEndTimeModel;
import com.example.demo.utils.DateCustomUtils;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.example.demo.utils.DateCustomUtils.transFormat4Short;
import static com.example.demo.utils.StringCustomUtils.*;

@Component(value = "plan")
public class PlanDataTransfer extends AbstractExcelDataTransfer<PlanDataModel> {


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
                model.setDescription("type is " + model.getType());
            }
            model.setDescription("排班是" + model.getType());
            result.add(model);
        });

        return result;
    }

    private Map<String, String> transferRule4KeyMap(Map<String, String> map) {
        Map<String, String> result = new HashMap<>();
        map.forEach((k, v) -> result.put(k, etl4Key(v).replace("\n", "")));
        return result;
    }

    private Map<String, Map<String, StartTimeEndTimeModel>> transferRule4Month(Map<String, String> stringStringMap) {
        Map<String, StartTimeEndTimeModel> result = new HashMap<>();
        // 7月排班信息 班次A（早班） 07:30-17:00 班次B： 14:00-22:00（中班） 休: 当天休息
        String text = stringStringMap.get("0");

        String[] split = text.split(" ", 0);
        int month = getMonthDate(split[0]);

        for (int i = 1; i < split.length; ) {
            result.put(etl4Key(split[i]), dealTime(split[i + 1], month));
            i = i + 2;
        }

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
                Map<String, StartTimeEndTimeModel> temp = new HashMap<>();
                data.forEach((k, v) -> {
                    StartTimeEndTimeModel t = new StartTimeEndTimeModel();
                    int startDay = 0;
                    if (v.getStartTime() == null) {
                        t.setStartTime(null);
                    } else {
                        Calendar calendar1 = Calendar.getInstance();
                        calendar1.setTime(v.getStartTime());
                        startDay = calendar1.get(Calendar.DATE);
                        calendar1.set(Calendar.DATE, calendar.get(Calendar.DATE));
                        t.setStartTime(calendar1.getTime());
                    }
                    if (v.getEndTime() == null) {
                        t.setEndTime(null);
                    } else {
                        Calendar calendar1 = Calendar.getInstance();
                        calendar1.setTime(v.getEndTime());
                        if (startDay != calendar1.get(Calendar.DATE)) {
                            calendar1.add(Calendar.DATE, 1);
                        } else {
                            calendar1.set(Calendar.DATE, calendar.get(Calendar.DATE));
                        }
                        t.setEndTime(calendar1.getTime());
                    }
                    temp.put(k, t);
                });
                tempData.put(DateCustomUtils.transFormat4Day(calendar.getTime()), temp);
                calendar.add(Calendar.DATE, -1);
            } while (realMonth == calendar.get(Calendar.MONTH));
        }
        return tempData;
    }
}
