package com.example.demo.tool.transfer;

import com.example.demo.mapper.TSystemUserPlanDBModelMapper;
import com.example.demo.model.SignDataModel;
import com.example.demo.model.db.TSystemUserPlanDBModel;
import com.example.demo.utils.DateCustomUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.example.demo.utils.StringCustomUtils.splitString4HHmm;

@Slf4j
@Component(value = "sign")
public class SignDataTransfer extends AbstractExcelDataTransfer<SignDataModel> {

    private final TSystemUserPlanDBModelMapper tSystemUserPlanDBModelMapper;

    public SignDataTransfer(TSystemUserPlanDBModelMapper tSystemUserPlanDBModelMapper) {
        this.tSystemUserPlanDBModelMapper = tSystemUserPlanDBModelMapper;
    }

    @Override
    protected List<SignDataModel> doTransfer(List<Map<String, String>> dataList) {
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
                    model = generateStartEndTime(model, dataMap.get(key));
                    if (model != null) {
                        result.add(model);
                    }
                }
            }
        }
        return result;
    }

    private SignDataModel generateStartEndTime(SignDataModel model, String s) {
        //06:0306:0314:0917:1017:1017:1117:11

        TSystemUserPlanDBModel model1 = tSystemUserPlanDBModelMapper.selectByNameAndDate(model.getName(), model.getDate());
        if (model1 == null) {
            return null;
        }
        model.setType(model1.getType());
        model.setShouldStartTime(model1.getStartTime());
        model.setShouldEndTime(model1.getEndTime());

        if (StringUtils.isNotBlank(s)) {
            List<String> strings = splitString4HHmm(s);
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
                model.setCardTimes(dateList.size());
            }
            model.setOriginalData(s.replace("\n", ""));
        }
        return model;
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
        String[] split = s.split("~", 0);
        Date startDay = DateCustomUtils.transFormat4Day(split[0].trim());
        Date endDay = DateCustomUtils.transFormat4Day(split[1].trim());
        if (startDay == null || endDay == null) {
            log.info("failed reason s is not valid");
            return result;
        }
        Calendar startCalender = Calendar.getInstance();
        startCalender.setTime(startDay);
        Calendar endCalender = Calendar.getInstance();
        endCalender.setTime(endDay);

        int index = 0;
        while (startCalender.before(endCalender)) {
            result.put(index + "", startCalender.getTime());
            startCalender.add(Calendar.DATE, 1);
            index++;
        }
        result.put(index + "", startCalender.getTime());

        return result;
    }
}
