package com.example.demo.tool.transfer;

import com.example.demo.model.PlanDataModel;
import com.example.demo.model.SignDataModel;
import com.example.demo.utils.DateCustomUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.example.demo.utils.RedisUtils.getPlanData4User;
import static com.example.demo.utils.StringCustomUtils.splitStringByFixedLength;

@Component(value = "sign")
public class SignDataTransfer extends AbstractExcelDataTransfer<SignDataModel> {

    private final StringRedisTemplate stringRedisTemplate;

    public SignDataTransfer(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    protected List<SignDataModel> doTransfer(List<Map<String, String>> dataList) {
        System.out.println("data");
        List<SignDataModel> result = new ArrayList<>();
        Map<String, Date> index2DateKey = transPeriodData(dataList.get(2).get("2"));

        for (int i = 4; i < dataList.size(); ) {
            Map<String, String> nameMap = dataList.get(i);
            Map<String, String> dataMap = dataList.get(i + 1);
            result.addAll(generateSignList(index2DateKey, nameMap, dataMap));
            i += 2;
        }
        return result;
    }

    private List<SignDataModel> generateSignList(Map<String, Date> index2DateKey, Map<String, String> nameMap, Map<String, String> dataMap) {
        List<SignDataModel> result = new ArrayList<>();
        if (MapUtils.isNotEmpty(nameMap) && MapUtils.isNotEmpty(dataMap)) {
            String name = nameMap.get("10");
            dataMap.remove("lineNumber");
            for (String key : dataMap.keySet()) {
                if (index2DateKey.containsKey(key)) {
                    SignDataModel model = new SignDataModel();
                    model.setDate(index2DateKey.get(key));
                    model.setName(name);
                    model.setDate(index2DateKey.get(key));
                    model.setOriginalData(dataMap.get(key));
                    generateStartEndTime(model, dataMap.get(key));
                    result.add(model);
                }
            }
        }
        return result;
    }

    private void generateStartEndTime(SignDataModel model, String s) {
        //06:0306:0314:0917:1017:1017:1117:11

        if (StringUtils.isNotBlank(s)) {
            List<String> strings = splitStringByFixedLength(s, 5);
            List<Date> dateList = generateDateList(model.getDate(), strings);
            dateList.sort((o1, o2) -> {
                if (o1.before(o2)) {
                    return -1;
                } else if (o1.after(o2)) {
                    return 1;
                } else {
                    return 0;
                }
            });
            if (CollectionUtils.isNotEmpty(dateList) && dateList.size() > 1) {
                model.setStartTime(dateList.get(0));
                model.setEndTime(dateList.get(dateList.size() - 1));
            }
            PlanDataModel planData4User = getPlanData4User(stringRedisTemplate, model.getName(), model.getDate());
            judgeData(planData4User, model);
            System.out.println("hello");
        }
    }

    private void judgeData(PlanDataModel planData4User, SignDataModel model) {
        if (planData4User != null) {
            if (planData4User.getStartTime() != null && planData4User.getEndTime() != null) {
                if (model.getStartTime() == null || model.getEndTime() == null) {
                    model.setLackCard(true);
                    model.setLackArrived(true);
                }
                if (model.getStartTime().after(planData4User.getStartTime())) {
                    model.setArrivedLate(true);
                    model.setLackArrived(true);
                } else if (model.getEndTime().before(planData4User.getEndTime())) {
                    model.setLeaveEarly(true);
                    model.setLackArrived(true);
                }
            }
        }
    }

    private List<Date> generateDateList(Date date, List<String> strings) {
        List<Date> result = new ArrayList<>();
        String dateStr = DateCustomUtils.transFormat4Day(date);
        strings.forEach(k -> {
            String tempDateStr = dateStr + " " + k + ":00";
            Date tempDate = DateCustomUtils.transFormat4Time(tempDateStr);
            result.add(tempDate);
        });
        return result;
    }

    private Map<String, Date> transPeriodData(String s) {
        Map<String, Date> result = new HashMap<>();
        //2 -> 2025-06-01 ~ 2025-06-30
        String[] split = s.split("~");
        Date startDay = DateCustomUtils.transFormat4Day(split[0].trim());
        Date endDay = DateCustomUtils.transFormat4Day(split[1].trim());
        Calendar startCalender = Calendar.getInstance();
        startCalender.setTime(startDay);
        Calendar endCalender = Calendar.getInstance();
        endCalender.setTime(endDay);

        int index = 1;
        while (startCalender.before(endCalender)) {
            result.put(index + "", startCalender.getTime());
            startCalender.add(Calendar.DATE, 1);
            index++;
        }
        result.put(index + "", startCalender.getTime());

        return result;
    }
}
