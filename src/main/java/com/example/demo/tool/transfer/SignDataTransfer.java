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
        model.setTwoDay(model1.isTwoDay());
        model.setShouldStartTime(model1.getStartTime());
        model.setShouldEndTime(model1.getEndTime());

        if (StringUtils.isNotBlank(s)) {
            List<String> strings = splitString4HHmm(s);
            List<Date> dateList = generateDateList(model.isTwoDay(), model.getDate(), strings);
            dateList.sort(Comparator.comparing(Date::getTime));
            if (CollectionUtils.isNotEmpty(dateList)) {
                if (dateList.size() > 1) {
                    model.setStartTime(dateList.get(0));
                    model.setEndTime(dateList.get(dateList.size() - 1));
                } else {
                    if (model.getShouldStartTime() != null && model.getShouldEndTime() != null) {
                        long abStart = Math.abs((dateList.get(0).getTime() - model.getShouldStartTime().getTime()));
                        long abEnd = Math.abs((dateList.get(0).getTime() - model.getShouldEndTime().getTime()));
                        if (abStart < abEnd) {
                            model.setStartTime(dateList.get(0));
                        } else {
                            model.setEndTime(dateList.get(0));
                        }
                    } else if (model.getShouldStartTime() != null) {
                        model.setStartTime(dateList.get(0));
                    } else if (model.getShouldEndTime() != null) {
                        model.setEndTime(dateList.get(0));
                    }
                }
                model.setCardTimes(dateList.size());
            } else {
                model.setCardTimes(0);
            }
            model.setOriginalData(s);
        }
        return model;
    }

    private List<Date> generateDateList(boolean isTwoDay, Date date, List<String> strings) {
        List<Date> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(strings)) {
            return result;
        }
        String dateStr = DateCustomUtils.transFormat4Day(date);
        strings.forEach(k -> {
            String tempDateStr = dateStr + " " + k + ":00";
            Date tempDate = DateCustomUtils.transFormat4Time(tempDateStr);
            result.add(tempDate);
        });
        if (isTwoDay) {
            result.sort(Comparator.comparing(Date::getTime).reversed());
            for (int i = 1; i < result.size(); i++) {
                if (result.get(i).before(result.get(0))) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(result.get(i));
                    calendar.set(Calendar.MILLISECOND, 0);
                    calendar.add(Calendar.DATE, 1);
                    result.set(i, calendar.getTime());
                }
            }
        } else {
            result.sort(Comparator.comparing(Date::getTime));
        }
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
