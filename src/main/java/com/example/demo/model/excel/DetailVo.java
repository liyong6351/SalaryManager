package com.example.demo.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.enums.BooleanEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import com.alibaba.excel.enums.poi.VerticalAlignmentEnum;
import lombok.Data;

@Data
public class DetailVo {
    @ExcelProperty("姓名")
    @ColumnWidth(10)
    private String name;
    @ExcelProperty("日期")
    @ColumnWidth(10)
    private String date;
    @ExcelProperty("班次")
    @ColumnWidth(8)
    private String type;
    @ExcelProperty("跨天")
    @ColumnWidth(8)
    private String isTwoDay;
    @ExcelProperty("上班卡")
    @ColumnWidth(12)
    private String startTime;

    @ExcelProperty("下班卡")
    @ColumnWidth(12)
    private String endTime;

    @ExcelProperty("排班上班卡")
    @ColumnWidth(15)
    private String shouldStartTime;


    @ExcelProperty("排班下班卡")
    @ColumnWidth(15)
    private String shouldEndTime;

    @ExcelProperty("迟到")
    @ColumnWidth(8)
    private String late;

    @ExcelProperty("早退")
    @ColumnWidth(8)
    private String early;

    @ExcelProperty("缺卡")
    @ColumnWidth(8)
    private String lackCard;

    @ExcelProperty("缺席")
    @ColumnWidth(8)
    private String absense;

    @ExcelProperty("原始数据")
    @ColumnWidth(30)
    @ContentStyle(
            wrapped= BooleanEnum.TRUE,
            horizontalAlignment = HorizontalAlignmentEnum.LEFT, // 内容居中
            verticalAlignment = VerticalAlignmentEnum.CENTER    // 垂直居中（可选）
    )
    private String originData;
}
