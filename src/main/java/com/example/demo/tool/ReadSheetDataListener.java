package com.example.demo.tool;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.CellExtra;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReadSheetDataListener extends AnalysisEventListener<Map<String, String>> {

    @Override
    public void invoke(Map<String, String> data, AnalysisContext context) {
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
}