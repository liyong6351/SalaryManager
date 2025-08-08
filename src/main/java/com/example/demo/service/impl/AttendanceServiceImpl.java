package com.example.demo.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.example.demo.enums.ExcelType;
import com.example.demo.mapper.TSystemPlanDBModelMapper;
import com.example.demo.mapper.TSystemUserPlanDBModelMapper;
import com.example.demo.mapper.TUserCardRecordDBModelMapper;
import com.example.demo.model.PlanDataModel;
import com.example.demo.model.SignDataModel;
import com.example.demo.model.common.StartTimeEndTimeModel;
import com.example.demo.model.db.TSystemPlanDBModel;
import com.example.demo.model.db.TSystemUserPlanDBModel;
import com.example.demo.model.db.TUserCardRecordDBModel;
import com.example.demo.model.excel.DetailVo;
import com.example.demo.model.excel.StatisticsDetailVo;
import com.example.demo.service.AttendanceService;
import com.example.demo.tool.transfer.AbstractExcelDataTransfer;
import com.example.demo.utils.DateCustomUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.demo.utils.DateCustomUtils.*;
import static com.example.demo.utils.StringCustomUtils.*;

@Slf4j
@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final TSystemPlanDBModelMapper tSystemPlanDBModelMapper;

    private final TSystemUserPlanDBModelMapper tSystemUserPlanDBModelMapper;

    private final TUserCardRecordDBModelMapper tUserCardRecordDBModelMapper;

    private final Map<String, AbstractExcelDataTransfer<?>> transferMap;


    public AttendanceServiceImpl(Map<String, AbstractExcelDataTransfer<?>> transferMap, TSystemPlanDBModelMapper tSystemPlanDBModelMapper, TSystemUserPlanDBModelMapper tSystemUserPlanDBModelMapper, TUserCardRecordDBModelMapper tUserCardRecordDBModelMapper) {
        this.transferMap = transferMap;
        this.tSystemPlanDBModelMapper = tSystemPlanDBModelMapper;
        this.tSystemUserPlanDBModelMapper = tSystemUserPlanDBModelMapper;
        this.tUserCardRecordDBModelMapper = tUserCardRecordDBModelMapper;
    }

    @Override
    public String uploadPlan(List<Map<String, String>> list) {
        store4Plan(list.get(2));
        List<PlanDataModel> dataList = (List<PlanDataModel>) transferMap.get(ExcelType.PLAN.getName()).transferData(list);
        store4UserPlan(dataList);
        long count = dataList.stream().map(PlanDataModel::getName).distinct().count();
        return "导入" + count + "个人的考勤规则信息";
    }

    private void store4UserPlan(List<PlanDataModel> list) {
        List<TSystemUserPlanDBModel> dataList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(k -> {
                TSystemUserPlanDBModel model = new TSystemUserPlanDBModel();
                model.setUserName(k.getName());
                model.setDate(k.getDate());
                model.setType(k.getType());
                model.setStartTime(k.getStartTime());
                model.setEndTime(k.getEndTime());
                dataList.add(model);
            });
        }
        if (CollectionUtils.isNotEmpty(dataList)) {
            List<TSystemUserPlanDBModel> insertList = new ArrayList<>();
            AtomicInteger updateCount = new AtomicInteger();
            dataList.forEach(k -> {
                TSystemUserPlanDBModel model = tSystemUserPlanDBModelMapper.selectByCondition(k.getUserName(), k.getType(), k.getDate());
                if (model == null) {
                    insertList.add(k);
                } else {
                    if (!isDateSame(model.getStartTime(), k.getStartTime()) || !isDateSame(model.getEndTime(), k.getEndTime())) {

                        model.setStartTime(k.getStartTime());
                        model.setEndTime(k.getEndTime());
                        updateCount.getAndIncrement();
                        tSystemUserPlanDBModelMapper.updateByPrimaryKey(model);
                    }
                }
            });
            if (CollectionUtils.isNotEmpty(insertList)) {
                try {
                    insertList.forEach(tSystemUserPlanDBModelMapper::insert);
                } catch (Exception e) {
                    log.error("error ", e);
                }
            }
        }
    }

    private void store4Plan(Map<String, String> stringStringMap) {
        // 7月排班信息 班次A（早班） 07:30-17:00 班次B： 14:00-22:00（中班） 休: 当天休息
        String text = stringStringMap.get("0");

        String[] split = text.split(" ", 0);
        // 清洗月份
        int month = getMonthDate(split[0]);
        // 获取每个月的天数
        int days = getDays4Month(month);

        // 封装 排班 -> {开始时间，结束时间}
        Map<String, StartTimeEndTimeModel> map = new HashMap<>();
        for (int i = 1; i < split.length; ) {
            String key = etl4Key(split[i]);
            map.put(key, dealTime(split[i + 1], month));
            i = i + 2;
        }


        List<TSystemPlanDBModel> list = new ArrayList<>();
        for (int i = 1; i <= days; i++) {
            int finalI = i;
            map.forEach((plan, startTimeEndTimeModel) -> list.add(generatePlanData(finalI, month, plan, startTimeEndTimeModel, text)));
        }
        if (CollectionUtils.isNotEmpty(list)) {
            List<Integer> existIdList = new ArrayList<>();
            list.forEach(model -> {
                Integer id = tSystemPlanDBModelMapper.selectByTypeAndDate(model.getType(), model.getDate());
                if (id != null) {
                    existIdList.add(id);
                }
            });
            if (CollectionUtils.isNotEmpty(existIdList)) {
                tSystemPlanDBModelMapper.deleteByIdList(existIdList);
            }
            if (CollectionUtils.isNotEmpty(list)) {
                try {
                    list.forEach(tSystemPlanDBModelMapper::insert);
                } catch (Exception e) {
                    log.error("error ", e);
                }
            }
        }
    }

    private static TSystemPlanDBModel generatePlanData(int day, int month, String type, StartTimeEndTimeModel model, String text) {
        TSystemPlanDBModel result = new TSystemPlanDBModel();
        result.setType(type);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, day);
        calendar.set(Calendar.MONTH, month - 1);
        result.setDate(calendar.getTime());
        if (model.getStartTime() != null) {
            calendar.setTime(model.getStartTime());
            calendar.set(Calendar.DATE, day);
            result.setStartTime(calendar.getTime());
        }

        if (model.getEndTime() != null) {
            calendar.setTime(model.getEndTime());
            calendar.set(Calendar.DATE, day);
            if (model.isTwoDay()) {
                calendar.add(Calendar.DATE, 1);
            }
            result.setEndTime(calendar.getTime());
        }

        result.setOriginData(text);
        return result;
    }

    @Override
    public void uploadData(List<Map<String, String>> list, HttpServletResponse response) {
        AbstractExcelDataTransfer<?> transfer = transferMap.get(ExcelType.SIGN.getName());
        List<SignDataModel> signDataModelList = (List<SignDataModel>) transfer.transferData(list);
        signDataModelList.sort(Comparator.comparing(SignDataModel::getName));
        if (CollectionUtils.isNotEmpty(signDataModelList)) {
            List<TUserCardRecordDBModel> dataList = new ArrayList<>();
            signDataModelList.forEach(k -> {
                TSystemUserPlanDBModel model = tSystemUserPlanDBModelMapper.selectByNameAndDate(k.getName(), k.getDate());
                TUserCardRecordDBModel date = new TUserCardRecordDBModel();
                date.setUserName(model.getUserName());
                date.setDate(k.getDate());
                date.setType(model.getType());
                date.setStartTime(k.getStartTime());
                date.setEndTime(k.getEndTime());
                date.setShouldStartTime(k.getShouldStartTime());
                date.setShouldEndTime(k.getShouldEndTime());
                date.setOriginData(k.getOriginalData());
                date.setCardTimes(k.getCardTimes());
                fillFullData(date);
                dataList.add(date);
            });

            if (CollectionUtils.isNotEmpty(dataList)) {
                List<TUserCardRecordDBModel> insertList = new ArrayList<>();
                dataList.forEach(k -> {
                    TUserCardRecordDBModel oldModel = tUserCardRecordDBModelMapper.selectByCondition(k.getUserName(), k.getDate());
                    if (oldModel == null) {
                        insertList.add(k);
                    } else {
                        k.setId(oldModel.getId());
                        tUserCardRecordDBModelMapper.updateByPrimaryKey(k);
                    }
                });
                if (CollectionUtils.isNotEmpty(insertList)) {
                    try {
                        insertList.forEach(tUserCardRecordDBModelMapper::insert);
                    } catch (Exception e) {
                        log.error("error ", e);
                    }
                }
            }
            List<DetailVo> detailVos = tUserCardRecordDBModelMapper.select4Detail();
            List<StatisticsDetailVo> statisticsDetailVos = tUserCardRecordDBModelMapper.select4Statistics();
            if (CollectionUtils.isNotEmpty(statisticsDetailVos)) {
                statisticsDetailVos.forEach(k -> {
                    k.setLateDetail(clearGroupConcat(k.getLateDetail()));
                    k.setEarlyDetail(clearGroupConcat(k.getEarlyDetail()));
                    k.setAbsenseDetail(clearGroupConcat(k.getAbsenseDetail()));
                });
            }
            writeSheet(detailVos, statisticsDetailVos, response);
        }
    }

    @Override
    public void cleanData() {
        tSystemUserPlanDBModelMapper.truncateTable();
        tSystemPlanDBModelMapper.truncateTable();
        tUserCardRecordDBModelMapper.truncateTable();
    }

    private void fillFullData(TUserCardRecordDBModel date) {
        if (date == null) {
            return;
        }
        if (date.getShouldStartTime() == null) {
            date.setLate("N");
        } else {
            if (date.getStartTime() == null) {
                date.setLate("Y");
            } else if (date.getStartTime().after(date.getShouldStartTime())) {
                date.setLate("Y");
            } else {
                date.setLate("N");
            }
        }

        if (date.getShouldEndTime() == null) {
            date.setEarly("N");
        } else {
            if (date.getEndTime() == null) {
                date.setEarly("Y");
            } else if (date.getEndTime().before(date.getShouldEndTime())) {
                date.setEarly("Y");
            } else {
                date.setEarly("N");
            }
        }

        if (date.getEarly().equals("Y") || date.getLate().equals("Y")) {
            date.setAbsense("Y");
        } else {
            if (date.getShouldEndTime() == null || date.getShouldStartTime() == null) {
                date.setAbsense("Y");
            } else {
                date.setAbsense("N");
            }
        }
        if (date.getCardTimes() >= 2) {
            date.setLackCard("N");
        } else {
            if (date.getShouldStartTime() == null || date.getShouldEndTime() == null) {
                date.setLackCard("N");
            } else {
                date.setLackCard("Y");
            }
        }

    }

    public void writeSheet(List<DetailVo> list1, List<StatisticsDetailVo> list2, HttpServletResponse response) {
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
                    .head(DetailVo.class).build();
            excelWriter.write(list1, sheet1);

            // 写入 Sheet2
            WriteSheet sheet2 = EasyExcel.writerSheet(1, "Sheet2")
                    .head(StatisticsDetailVo.class).build();
            excelWriter.write(list2, sheet2);
        } catch (Exception e) {
            throw new RuntimeException("导出失败", e);
        }
    }
}
