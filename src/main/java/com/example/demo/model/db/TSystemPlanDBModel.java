package com.example.demo.model.db;

import lombok.Data;

import java.util.Date;

import static com.example.demo.utils.DateCustomUtils.transFormat4YYYYMMDDHHMISS;

@Data
public class TSystemPlanDBModel {
    private Integer id;

    private String type;

    private Date date;

    private boolean isTwoDay;

    private Date startTime;

    private Date endTime;

    private String originData;

    @Override
    public String toString() {
        return "TSystemPlanDBModel{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", date='" + date + '\'' +
                ", startTime=" + transFormat4YYYYMMDDHHMISS(startTime) +
                ", endTime=" + transFormat4YYYYMMDDHHMISS(endTime) +
                ", originData='" + originData + '\'' +
                '}';
    }
}