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
    private String startTime;
    private String endTime;
}
