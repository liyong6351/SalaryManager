package com.example.demo.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class StartTimeEndTimeModel {
    String date;
    Date startTime;
    Date endTime;
}
