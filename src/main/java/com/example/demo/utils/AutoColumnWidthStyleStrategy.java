package com.example.demo.utils;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.Sheet;

public class AutoColumnWidthStyleStrategy implements SheetWriteHandler {

    @Override
    public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        // 不需要操作
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        // 获取当前 Sheet
        Sheet sheet = writeSheetHolder.getSheet();

        // 假设你知道你的数据模型类（例如 DemoData.class）
        // 这里需要你根据实际情况获取总列数。例如，通过表头获取。
        int columnCount = 10; // 获取你的数据有多少列（重要！）

        // 遍历每一列
        for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
            int maxWidth = 0; // 记录该列最大宽度（字符数）

            // 1. 处理表头 (第一行)
            org.apache.poi.ss.usermodel.Row headerRow = sheet.getRow(0);
            if (headerRow != null) {
                org.apache.poi.ss.usermodel.Cell headerCell = headerRow.getCell(columnIndex);
                if (headerCell != null) {
                    int length = getCellStringValue(headerCell).length();
                    if (length > maxWidth) maxWidth = length;
                }
            }

            // 2. 处理数据行 (从第二行开始)
            // 注意：EasyExcel 在 afterSheetCreate 时数据可能还未完全写入。
            // 方法A (推荐): 如果你在写之前就知道所有数据（如 List<DemoData>），在此处遍历你的数据源计算最大宽度。
            // 方法B (简单但可能不准确): 只处理已写入的行（适用于数据量不大或分批写入时估算）
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                org.apache.poi.ss.usermodel.Row dataRow = sheet.getRow(rowIndex);
                if (dataRow != null) {
                    org.apache.poi.ss.usermodel.Cell dataCell = dataRow.getCell(columnIndex);
                    if (dataCell != null) {
                        int length = getCellStringValue(dataCell).length();
                        // 针对中文字符调整（粗略估算，中文通常占2个英文字符宽度）
                        // 更精确的方法需要判断每个字符是否是中文（或宽字符）
                        if (containsChinese(getCellStringValue(dataCell))) {
                            length *= 2; // 中文按两倍宽度算
                        }
                        if (length > maxWidth) maxWidth = length;
                    }
                }
            }

            // 3. 设置列宽 (核心!)
            // Excel列宽单位 = (字符宽度 * 256)。这里加100是留点余量，系数0.9可调整防止过宽。
            // 最大宽度限制避免异常宽（可选）
            int columnWidth = (int) ((maxWidth * 256 * 0.9) + 100);
            if (columnWidth > 255 * 256) { // Excel单列最大宽度限制
                columnWidth = 255 * 256;
            }
            sheet.setColumnWidth(columnIndex, columnWidth);
        }
    }

    // 辅助方法：获取单元格的字符串值
    private String getCellStringValue(org.apache.poi.ss.usermodel.Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula(); // 或尝试计算值
            default:
                return "";
        }
    }

    // 辅助方法：粗略判断字符串是否包含中文（用于宽度估算）
    private boolean containsChinese(String str) {
        return str.matches(".*[\\u4e00-\\u9fa5].*"); // 匹配中文字符正则
    }
}