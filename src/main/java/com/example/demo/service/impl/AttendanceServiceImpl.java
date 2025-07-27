package com.example.demo.service.impl;

import com.example.demo.enums.ExcelType;
import com.example.demo.model.PlanDataModel;
import com.example.demo.model.SignDataModel;
import com.example.demo.model.SignOutPutDataModel;
import com.example.demo.model.SignOutPutStatisticsDataModel;
import com.example.demo.service.AttendanceService;
import com.example.demo.tool.transfer.AbstractExcelDataTransfer;
import com.example.demo.utils.DateCustomUtils;
import com.example.demo.utils.RedisUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final StringRedisTemplate stringRedisTemplate;

    private final Map<String, AbstractExcelDataTransfer> transferMap;


    public AttendanceServiceImpl(StringRedisTemplate stringRedisTemplate, Map<String, AbstractExcelDataTransfer> transferMap) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.transferMap = transferMap;
    }

    @Override
    public String uploadPlan(List<Map<String, String>> list) {
        List<PlanDataModel> dataList = transferMap.get(ExcelType.PLAN.getName()).transferData(list);
        int i = RedisUtils.storePlanData4User(stringRedisTemplate, dataList);
        return "导入" + i + "个人的考勤规则信息";
    }

    @Override
    public String uploadData(List<Map<String, String>> list) {
        AbstractExcelDataTransfer transfer = transferMap.get(ExcelType.SIGN.getName());
        List<SignDataModel> signDataModelList = transfer.transferData(list);
        signDataModelList.sort(Comparator.comparing(SignDataModel::getName));
        if (CollectionUtils.isNotEmpty(signDataModelList)) {
            List<SignOutPutStatisticsDataModel> statisticsDataModelList = invokeStatisticsData(signDataModelList);
            List<SignOutPutDataModel> signOutPutDataModelList = invokeData(signDataModelList);
        }
        return "";
    }

    private List<SignOutPutDataModel> invokeData(List<SignDataModel> list) {
        List<SignOutPutDataModel> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        result = list.stream()
                .filter(k -> k.getStartTime() == null || k.getEndTime() == null)
                .map(k -> {
                    SignOutPutDataModel dataModel = new SignOutPutDataModel();
                    if (k.getStartTime() != null && k.getEndTime() != null) {
                        dataModel.setOriginData(k.getOriginalData());
                        dataModel.setName(k.getName());
                        dataModel.setDate(DateCustomUtils.getDay(k.getDate()));
                        dataModel.setEndTime(DateCustomUtils.getDay(k.getEndTime()));
                        dataModel.setStartTime(DateCustomUtils.getDay(k.getStartTime()));
                        dataModel.setArrivedLate(k.isArrivedLate() ? "是" : "");
                        dataModel.setLeaveEarly(k.isLeaveEarly() ? "是" : "");
                        dataModel.setLackCard(k.isLackCard() ? "是" : "");
                    }
                    return dataModel;
                }).collect(Collectors.toList());
        return result;
    }

    private List<SignOutPutStatisticsDataModel> invokeStatisticsData(List<SignDataModel> list) {
        List<SignOutPutStatisticsDataModel> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        Map<String, List<SignDataModel>> name2RecordListMap = new HashMap<>();
        for (SignDataModel model : list) {
            if (!name2RecordListMap.containsKey(model.getName())) {
                name2RecordListMap.put(model.getName(), new ArrayList<>());
            }
            name2RecordListMap.get(model.getName()).add(model);
        }

        return result;
    }
}
