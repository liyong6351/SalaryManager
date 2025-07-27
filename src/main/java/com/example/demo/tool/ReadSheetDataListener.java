package com.example.demo.tool;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.CellExtra;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReadSheetDataListener extends AnalysisEventListener<Map<String, String>> {

    @Override
    public void invoke(Map<String, String> data, AnalysisContext context) {
        // 判断当前行是否为空
        if (isRowEmpty(data)) {
            System.out.println("检测到空行，行号: " + context.readRowHolder().getRowIndex());
            // 添加空行占位逻辑（如插入空对象）
        } else {
            // 正常处理数据
        }
        data.put("lineNumber", String.valueOf(context.readRowHolder().getRowIndex()));
        List<Object> keyList = new ArrayList<>(data.keySet());
        for (Object key : keyList) {
            if (key instanceof Integer) {
                if (data.get(key) == null) {
                    data.put(key.toString(), "");
                } else {
                    data.put(key.toString(), data.get(key));
                }
                data.remove(key);
            }
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }

    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        super.onException(exception, context);
    }

    @Override
    public void extra(CellExtra extra, AnalysisContext context) {
        super.extra(extra, context);
    }

    @Override
    public boolean hasNext(AnalysisContext context) {
        return super.hasNext(context);
    }

    // 判断行数据是否为空
    private boolean isRowEmpty(Map<String, String> data) {
        return data == null || MapUtils.isEmpty(data);
    }
}