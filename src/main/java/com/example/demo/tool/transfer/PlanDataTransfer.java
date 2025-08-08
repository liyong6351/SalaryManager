package com.example.demo.tool.transfer;

import com.example.demo.mapper.TSystemPlanDBModelMapper;
import com.example.demo.mapper.TSystemUserPlanDBModelMapper;
import com.example.demo.model.PlanDataModel;
import com.example.demo.model.common.StartTimeEndTimeModel;
import com.example.demo.model.db.TSystemPlanDBModel;
import com.example.demo.model.db.TSystemUserPlanDBModel;
import com.example.demo.utils.DateCustomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.demo.utils.DateCustomUtils.*;
import static com.example.demo.utils.StringCustomUtils.*;

@Component(value = "plan")
public class PlanDataTransfer extends AbstractExcelDataTransfer<PlanDataModel> {

    private final TSystemPlanDBModelMapper tSystemPlanDBModelMapper;

    public PlanDataTransfer(TSystemPlanDBModelMapper tSystemPlanDBModelMapper) {
        this.tSystemPlanDBModelMapper = tSystemPlanDBModelMapper;
    }

    @Override
    protected List<PlanDataModel> doTransfer(List<Map<String, String>> dataList) {

        List<TSystemPlanDBModel> planList = tSystemPlanDBModelMapper.list();
        // type||date -> model
        Map<String, TSystemPlanDBModel> type2Date2PlanMap = planList.stream().collect(Collectors.toMap(k -> k.getType() + "||" + transFormat4DayShort(k.getDate()), v -> v, (v1, v2) -> v2));

        Map<String, String> keyMap = transferRule4KeyMap(dataList.get(3));
        List<PlanDataModel> list = new ArrayList<>();
        for (int i = 4; i < dataList.size(); i++) {
            Map<String, String> map = dataList.get(i);
            list.addAll(transferSingData(type2Date2PlanMap, keyMap, map));
        }
        return list;
    }

    private List<PlanDataModel> transferSingData(Map<String, TSystemPlanDBModel> typeDate2PlanMap, Map<String, String> keyMap, Map<String, String> map) {
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
            String key = model.getType() + "||" + keyMap.get(k);
            if (typeDate2PlanMap.containsKey(key)) {
                model.setStartTime(typeDate2PlanMap.get(key).getStartTime());
                model.setEndTime(typeDate2PlanMap.get(key).getEndTime());
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
        // 7月排班信息 班次A（早班） 07:30-17:00 班次B（中班） 14:00-22:00 休 当天休息 班次C 08:00-20:00 班次D 20:00-08:00
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
                    Calendar calendar1 = Calendar.getInstance();
                    if (v.getStartTime() == null) {
                        t.setStartTime(null);
                    } else {
                        calendar1.setTime(v.getStartTime());
                        calendar1.set(Calendar.DATE, calendar.get(Calendar.DATE));
                        t.setStartTime(calendar1.getTime());
                    }
                    if (v.getEndTime() == null) {
                        t.setEndTime(null);
                    } else {
                        calendar1.setTime(v.getEndTime());
                        calendar1.set(Calendar.DATE, calendar.get(Calendar.DATE));
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
