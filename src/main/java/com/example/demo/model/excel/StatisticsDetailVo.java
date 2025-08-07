package com.example.demo.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class StatisticsDetailVo {
    @ExcelProperty("月份")
    private String month;
    @ExcelProperty("姓名")
    private String name;
    @ExcelProperty("应工作日")
    private Integer shouldWorkDays;
    @ExcelProperty("实工作日")
    private Integer workDays;
    @ExcelProperty("迟到天数")
    private Integer lateDays;
    @ExcelProperty("早退天数")
    private Integer earlyDays;
    @ExcelProperty("缺席天数")
    private Integer absenseDays;
    @ExcelProperty("迟到详情")
    private String lateDetail;
    @ExcelProperty("早退详情")
    private String earlyDetail;
    @ExcelProperty("缺席详情")
    private String absenseDetail;
}
