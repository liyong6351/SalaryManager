package com.example.demo.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class DetailVo {
    @ExcelProperty("姓名")
    private String name;
    @ExcelProperty("日期")
    private String date;
    @ExcelProperty("班次")
    private String type;
    @ExcelProperty("上班卡")
    private String startTime;
    @ExcelProperty("下班卡")
    private String endTime;
    @ExcelProperty("排班上班卡")
    private String shouldStartTime;
    @ExcelProperty("排班下班卡")
    private String shouldEndTime;
    @ExcelProperty("迟到")
    private String late;
    @ExcelProperty("早退")
    private String early;
    @ExcelProperty("缺卡")
    private String lackCard;
    @ExcelProperty("缺席")
    private String absense;
    @ExcelProperty("原始数据")
    private String originData;
}
