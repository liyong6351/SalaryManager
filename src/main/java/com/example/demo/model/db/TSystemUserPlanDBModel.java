package com.example.demo.model.db;

import lombok.Data;

import java.util.Date;

@Data
public class TSystemUserPlanDBModel {
    private Integer id;

    private String userName;

    private String type;

    private Date date;

    private boolean isTwoDay;

    private Date startTime;

    private Date endTime;
}