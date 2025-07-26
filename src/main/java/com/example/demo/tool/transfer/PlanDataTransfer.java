package com.example.demo.tool.transfer;

import com.example.demo.model.PlanDataModel;
import com.example.demo.model.common.StartTimeEndTimeModel;
import com.example.demo.utils.DateCustomUtils;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.example.demo.utils.DateCustomUtils.trans4Plan;

@Component(value = "plan")
public class PlanDataTransfer extends AbstractExcelDataTransfer<PlanDataModel> {

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
        characterMap.put("（", "");
        characterMap.put("(", "");
        characterMap.put(")", "");
        characterMap.put("）", "");
    }

    @Override
    protected List<PlanDataModel> doTransfer(List<Map<String, String>> dataList) {
        //Map<String, Date> planAll = transferAllData(dataList.get(1));

        for (int i = 3; i < dataList.size(); i++) {

        }
        return Collections.emptyList();
    }

    private Map<String, StartTimeEndTimeModel> transferAllData(Map<String, String> stringStringMap) {
        Map<String, StartTimeEndTimeModel> result = new HashMap<>();
        // 7月排班信息 班次A（早班） 07:30-17:00 班次B： 14:00-22:00（中班） 休: 当天休息
        String text = stringStringMap.get("0");

        String[] split = text.split(" ");
        Date date = getMonthDate(split[0]);
        String firstKey = etl4Key(split[1]);
        String[] s1 = split[2].split("-");
        Date firstKeyStartDate = DateCustomUtils.trans4Plan(s1[0]);
        Date firstKeyEndDate = DateCustomUtils.trans4Plan(s1[1]);
        String secondKey = etl4Key(split[3]);
        String[] s2 = split[1].split("-");
        Date secondKeyStartDate = DateCustomUtils.trans4Plan(s2[0]);
        Date secondKeyEndDate = DateCustomUtils.trans4Plan(s2[1]);
        String thirdKey = etl4Key(split[4]);

        StartTimeEndTimeModel firstKeyData = new StartTimeEndTimeModel(null, firstKeyStartDate, firstKeyEndDate);
        StartTimeEndTimeModel secondKeyData = new StartTimeEndTimeModel(null, secondKeyStartDate, secondKeyEndDate);
        StartTimeEndTimeModel thirdKeyData = new StartTimeEndTimeModel(null, null, null);

        return result;
    }

    private Date getMonthDate(String s) {
        String month = etl4Key(s);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, Integer.parseInt(month));
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    private static String etl4Key(String src) {
        for (String key : characterMap.keySet()) {
            src = src.replace(key, characterMap.get(key));
        }
        return src;
    }
}
