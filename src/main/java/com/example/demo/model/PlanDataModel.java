package com.example.demo.model;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @Description: 排版数据
 * @Author: liyong
 * @Date: 2025/7/26 17:22
 */
@Data
@ToString
public class PlanDataModel {
    private String name;
    private Date date;
    private Date startTime;
    private Date endTime;
    private String description;
}
