package com.example.demo.model;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @Description: 打卡数据
 * @Author: liyong
 * @Date: 2025/7/26 17:22
 */
@Data
@ToString
public class SignDataModel {
    private String name;
    private Date date;
    private Date startTime;
    private Date endTime;
    private String originalData;
    // 迟到
    private boolean arrivedLate;
    //早退
    private boolean leaveEarly;
    //下班缺卡
    private boolean lackCard;
    // 是否缺勤
    private boolean lackArrived;
}
