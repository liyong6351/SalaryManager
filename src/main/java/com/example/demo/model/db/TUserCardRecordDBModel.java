package com.example.demo.model.db;

import lombok.Data;

import java.util.Date;

@Data
public class TUserCardRecordDBModel {
    private Integer id;

    private String userName;

    private Date date;
    private String type;

    private boolean isTwoDay;
    private Date shouldStartTime;

    private Date shouldEndTime;
    private Date startTime;

    private Date endTime;

    private String late;

    private String early;

    private String absense;

    private String lackCard;

    private Integer cardTimes;

    private String originData;
}