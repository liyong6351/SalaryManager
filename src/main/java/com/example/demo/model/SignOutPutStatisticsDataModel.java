package com.example.demo.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @Description: 员工的统计数据(按月)
 * @Author: liyong
 * @Date: 2025/7/26 17:22
 */
@Data
@ToString
public class SignOutPutStatisticsDataModel {
    @ExcelProperty("姓名")
    private String name;
    @ExcelProperty("月份")
    private String month;
    // 应该出席天数
    @ExcelProperty("应出席天数")
    private int shouldPresentDays;
    // 实际出席天数
    @ExcelProperty("实出席天数")
    private int actualPresentDays;
    // 缺席天数
    @ExcelProperty("缺席天数")
    private int absenceDays;
    // 迟到天数
    @ExcelProperty("迟到天数")
    private int lateDays;
    // 早退天数
    @ExcelProperty("早退天数")
    private int earlyDays;
    // 迟到详情
    @ExcelProperty("迟到详情")
    private String lateDetail;
    // 早退详情
    @ExcelProperty("早退详情")
    private String earlyDetail;
    // 缺卡详情
    @ExcelProperty("缺卡详情")
    private String lackCardDetail;
}
