package com.example.demo.model;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @Description: 每个员工实际数据(按天)
 * @Author: liyong
 * @Date: 2025/7/26 17:22
 */
@Data
@ToString
public class SignOutPutDataModel {
    private String name;
    private String date;
    private String originData;
    private String startTime;
    private String endTime;
    // 迟到
    private String arrivedLate;
    //早退
    private String leaveEarly;
    //下班缺卡
    private String lackCard;
}
