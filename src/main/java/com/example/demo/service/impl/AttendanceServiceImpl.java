package com.example.demo.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
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
import org.apache.commons.collections4.MapUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.example.demo.utils.RedisUtils.getPlan4User;
import static com.example.demo.utils.RedisUtils.getShouldWorkMap;

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
    public void uploadData(List<Map<String, String>> list, HttpServletResponse response) {
        AbstractExcelDataTransfer transfer = transferMap.get(ExcelType.SIGN.getName());
        List<SignDataModel> signDataModelList = transfer.transferData(list);
        signDataModelList.sort(Comparator.comparing(SignDataModel::getName));
        if (CollectionUtils.isNotEmpty(signDataModelList)) {
            List<SignOutPutDataModel> signOutPutDataModelList = invokeData(signDataModelList);
            List<SignOutPutStatisticsDataModel> statisticsDataModelList = invokeStatisticsData(signDataModelList);
            writeSheet(signOutPutDataModelList, statisticsDataModelList, response);
        }
    }

    public void writeSheet(List<SignOutPutDataModel> list1, List<SignOutPutStatisticsDataModel> list2, HttpServletResponse response) {
        try (OutputStream out = response.getOutputStream(); ExcelWriter excelWriter = EasyExcel.write(out).build()) {

            String fileName = "出勤报表" + DateCustomUtils.getMonth(new Date()) + ".xlsx"; // 包含空格的文件名

            // RFC 5987 编码标准 (最佳实践)
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name())
                    .replaceAll("\\+", "%20");

            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setHeader("Content-Disposition",
                    "attachment; filename*=UTF-8''" + encodedFileName);

            // 写入 Sheet1
            WriteSheet sheet1 = EasyExcel.writerSheet(0, "Sheet1")
                    .head(SignOutPutDataModel.class).build();
            excelWriter.write(list1, sheet1);

            // 写入 Sheet2
            WriteSheet sheet2 = EasyExcel.writerSheet(1, "Sheet2")
                    .head(SignOutPutStatisticsDataModel.class).build();
            excelWriter.write(list2, sheet2);
        } catch (Exception e) {
            throw new RuntimeException("导出失败", e);
        }
    }

    private List<SignOutPutDataModel> invokeData(List<SignDataModel> list) {
        List<SignOutPutDataModel> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        result = list.stream()
                .filter(k -> k.getStartTime() != null && k.getEndTime() != null)
                .map(k -> {
                    SignOutPutDataModel dataModel = new SignOutPutDataModel();
                    dataModel.setName(k.getName());
                    dataModel.setDate(DateCustomUtils.transFormat4Day(k.getDate()));
                    dataModel.setOriginData(k.getOriginalData());
                    if (k.getStartTime() != null && k.getEndTime() != null) {
                        dataModel.setName(k.getName());
                        dataModel.setDate(DateCustomUtils.transFormat4Day(k.getDate()));
                        dataModel.setEndTime(DateCustomUtils.transFormat4Day(k.getEndTime()));
                        dataModel.setStartTime(DateCustomUtils.transFormat4Day(k.getStartTime()));
                        dataModel.setArrivedLate(k.isArrivedLate() ? "是" : "");
                        dataModel.setLeaveEarly(k.isLeaveEarly() ? "是" : "");
                        dataModel.setLackCard(k.isLackCard() ? "是" : "");
                    } else {
                        dataModel.setStartTime("休息");
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
        Date date = list.get(0).getDate();
        Map<String, List<SignDataModel>> name2RecordListMap = new HashMap<>();
        for (SignDataModel model : list) {
            if (!name2RecordListMap.containsKey(model.getName())) {
                name2RecordListMap.put(model.getName(), new ArrayList<>());
            }
            name2RecordListMap.get(model.getName()).add(model);
        }

        if (MapUtils.isNotEmpty(name2RecordListMap)) {
            //名字->工作时间
            Map<String, Integer> shouldWorkMap = getShouldWorkMap(stringRedisTemplate, date);
            for (Map.Entry<String, List<SignDataModel>> entry : name2RecordListMap.entrySet()) {
                SignOutPutStatisticsDataModel model = new SignOutPutStatisticsDataModel();
                model.setName(entry.getKey());
                model.setShouldPresentDays(shouldWorkMap.getOrDefault(entry.getKey(), 0));
                analyseList(model, entry.getValue());
                result.add(model);
            }
        }

        return result;
    }

    private void analyseList(SignOutPutStatisticsDataModel model, List<SignDataModel> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        model.setMonth(DateCustomUtils.getMonth(list.get(0).getDate()));
        Map<String, PlanDataModel> plan4User = getPlan4User(stringRedisTemplate, list.get(0).getName());
        Set<String> lateDetail = new TreeSet<>();
        Set<String> earlyDetail = new TreeSet<>();
        Set<String> lackDetail = new TreeSet<>();
        int actualDays = 0;
        for (SignDataModel signDataModel : list) {
            String key = DateCustomUtils.transFormat4Day(signDataModel.getDate());
            if (plan4User.containsKey(key)) {
                PlanDataModel planDataModel = plan4User.get(key);
                actualDays += calculateDays(signDataModel, planDataModel, lackDetail, key, lateDetail, earlyDetail);
            } else {
                model.setLateDetail("没有排班");
            }
        }
        model.setActualPresentDays(actualDays);
        model.setAbsenceDays(lackDetail.size());
        model.setLateDays(lateDetail.size());
        model.setEarlyDays(earlyDetail.size());
        model.setLateDetail(JSON.toJSONString(lateDetail));
        model.setEarlyDetail(JSON.toJSONString(earlyDetail));
        model.setLackCardDetail(JSON.toJSONString(lackDetail));
    }

    private static int calculateDays(SignDataModel signDataModel, PlanDataModel planDataModel, Set<String> lackDetail, String key, Set<String> lateDetail, Set<String> earlyDetail) {
        int result = 0;
        Date signStartTime = signDataModel.getStartTime();
        Date signEndTime = signDataModel.getEndTime();
        Date planStartTime = planDataModel.getStartTime();
        Date planEndTime = planDataModel.getEndTime();

        if (planStartTime != null && planEndTime != null) {
            if (signStartTime == null) {
                result = 1;
                lackDetail.add(key);
                lateDetail.add(key);
                if (signEndTime == null) {
                    lackDetail.add(key);
                    earlyDetail.add(key);
                }
            } else {
                if (signEndTime == null) {
                    result = 1;
                    lackDetail.add(key);
                    earlyDetail.add(key);
                } else {
                    if (signStartTime.after(planStartTime)) {
                        result = 1;
                        lackDetail.add(key);
                        lateDetail.add(key);
                    }
                    if (signEndTime.before(planEndTime)) {
                        result = 1;
                        lackDetail.add(key);
                        earlyDetail.add(key);
                    }
                }
            }
        }
        return result;
    }
}
