package com.example.demo.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.BooleanEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import com.alibaba.excel.enums.poi.VerticalAlignmentEnum;
import lombok.Data;

@Data
public class StatisticsDetailVo {
    @ExcelProperty("月份")
    @ColumnWidth(10)
    @ContentStyle(
            horizontalAlignment = HorizontalAlignmentEnum.CENTER, // 内容居中
            verticalAlignment = VerticalAlignmentEnum.CENTER    // 垂直居中（可选）
    )
    private String month;

    @ExcelProperty("姓名")
    @ColumnWidth(13)
    @HeadStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER,
            verticalAlignment = VerticalAlignmentEnum.CENTER)
    @ContentStyle(
            horizontalAlignment = HorizontalAlignmentEnum.CENTER, // 内容居中
            verticalAlignment = VerticalAlignmentEnum.CENTER    // 垂直居中（可选）
    )
    private String name;

    @ExcelProperty("应工作日")
    @ColumnWidth(13)
    @HeadStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER,
            verticalAlignment = VerticalAlignmentEnum.CENTER)
    @ContentStyle(
            horizontalAlignment = HorizontalAlignmentEnum.CENTER, // 内容居中
            verticalAlignment = VerticalAlignmentEnum.CENTER    // 垂直居中（可选）
    )
    private Integer shouldWorkDays;

    @ExcelProperty("实工作日")
    @ColumnWidth(13)
    @HeadStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER,
            verticalAlignment = VerticalAlignmentEnum.CENTER)
    @ContentStyle(
            horizontalAlignment = HorizontalAlignmentEnum.CENTER, // 内容居中
            verticalAlignment = VerticalAlignmentEnum.CENTER    // 垂直居中（可选）
    )
    private Integer workDays;

    @ExcelProperty("迟到天数")
    @ColumnWidth(13)
    @HeadStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER,
            verticalAlignment = VerticalAlignmentEnum.CENTER)
    @ContentStyle(
            horizontalAlignment = HorizontalAlignmentEnum.CENTER, // 内容居中
            verticalAlignment = VerticalAlignmentEnum.CENTER    // 垂直居中（可选）
    )
    private Integer lateDays;

    @ExcelProperty("早退天数")
    @ColumnWidth(13)
    @HeadStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER,
            verticalAlignment = VerticalAlignmentEnum.CENTER)
    @ContentStyle(
            horizontalAlignment = HorizontalAlignmentEnum.CENTER, // 内容居中
            verticalAlignment = VerticalAlignmentEnum.CENTER    // 垂直居中（可选）
    )
    private Integer earlyDays;

    @ExcelProperty("缺席天数")
    @ColumnWidth(13)
    @HeadStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER,
            verticalAlignment = VerticalAlignmentEnum.CENTER)
    @ContentStyle(
            horizontalAlignment = HorizontalAlignmentEnum.CENTER, // 内容居中
            verticalAlignment = VerticalAlignmentEnum.CENTER    // 垂直居中（可选）
    )
    private Integer absenseDays;

    @ExcelProperty("迟到详情")
    @ColumnWidth(15)
    @ContentStyle(
            wrapped=BooleanEnum.TRUE,
            horizontalAlignment = HorizontalAlignmentEnum.LEFT, // 内容居中
            verticalAlignment = VerticalAlignmentEnum.CENTER    // 垂直居中（可选）
    )
    private String lateDetail;

    @ExcelProperty("早退详情")
    @ColumnWidth(15)
    @ContentStyle(
            wrapped=BooleanEnum.TRUE,
            horizontalAlignment = HorizontalAlignmentEnum.LEFT, // 内容居中
            verticalAlignment = VerticalAlignmentEnum.CENTER    // 垂直居中（可选）
    )
    private String earlyDetail;

    @ExcelProperty("缺席详情")
    @ColumnWidth(15)
    @ContentStyle(
            wrapped=BooleanEnum.TRUE,
            horizontalAlignment = HorizontalAlignmentEnum.LEFT, // 内容居中
            verticalAlignment = VerticalAlignmentEnum.CENTER    // 垂直居中（可选）
    )
    private String absenseDetail;
}
