package com.example.demo.model;

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
    private String name;
    private String month;
    // 应该出席天数
    private int shouldPresentDays;
    // 实际出席天数
    private int actualPresentDays;
    // 缺席天数
    private int absenceDays;
    // 迟到天数
    private int lateDays;
    // 迟到详情
    private String lateDetail;
    // 早退天数
    private int earlyDays;
    // 早退详情
    private String earlyDetail;
    // 缺卡天数
    private int afterWorkNoCardDays;
    // 缺卡详情
    private String afterWorkNoCardDetail;
}
