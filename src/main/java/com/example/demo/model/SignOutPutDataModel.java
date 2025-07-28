package com.example.demo.model;

import com.alibaba.excel.annotation.ExcelProperty;
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
    @ExcelProperty("姓名")
    private String name;
    @ExcelProperty("日期")
    private String date;
    @ExcelProperty("原始数据")
    private String originData;
    @ExcelProperty("上班时间")
    private String startTime;
    @ExcelProperty("下班时间")
    private String endTime;
    // 迟到
    @ExcelProperty("迟到")
    private String arrivedLate;
    //早退
    @ExcelProperty("早退")
    private String leaveEarly;
    //下班缺卡
    @ExcelProperty("缺卡")
    private String lackCard;
}
