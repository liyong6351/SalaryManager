package com.example.demo.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StartTimeEndTimeModel {
    Date startTime;
    Date endTime;
    boolean isTwoDay;
}
