package com.example.demo.service.impl;

import com.example.demo.enums.ExcelType;
import com.example.demo.model.PlanDataModel;
import com.example.demo.service.AttendanceService;
import com.example.demo.tool.transfer.AbstractExcelDataTransfer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
        AbstractExcelDataTransfer transfer = transferMap.get(ExcelType.PLAN.getName());
        if (transfer == null) {
            return "null";
        }
        List<PlanDataModel> dataList = transfer.transferData(list);
        return "";
    }

    @Override
    public String uploadData(List<Map<String, String>> list) {
        return "";
    }
}
