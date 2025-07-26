package com.example.demo.tool.transfer;

import lombok.Setter;

import java.util.List;
import java.util.Map;

@Setter
public abstract class AbstractExcelDataTransfer<T> {

    public List<T> transferData(List<Map<String, String>> dataList) {
        doBefore();
        List<T> result = doTransfer(dataList);
        doAfter();
        return result;
    }

    protected abstract List<T> doTransfer(List<Map<String, String>> dataList);

    private void doAfter() {
    }

    private void doBefore() {
    }

}
