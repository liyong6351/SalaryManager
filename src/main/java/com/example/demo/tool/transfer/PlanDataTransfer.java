package com.example.demo.tool.transfer;

import com.example.demo.mapper.TSystemPlanDBModelMapper;
import com.example.demo.model.PlanDataModel;
import com.example.demo.model.db.TSystemPlanDBModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.demo.utils.DateCustomUtils.transFormat4DayShort;
import static com.example.demo.utils.DateCustomUtils.transFormat4Short;
import static com.example.demo.utils.StringCustomUtils.etl4Key;

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
                model.setTwoDay(typeDate2PlanMap.get(key).isTwoDay());
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
}
