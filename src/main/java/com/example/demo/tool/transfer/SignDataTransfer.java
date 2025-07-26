package com.example.demo.tool.transfer;

import com.example.demo.model.SignDataModel;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component(value = "sign")
public class SignDataTransfer extends AbstractExcelDataTransfer<SignDataModel> {
    @Override
    protected List<SignDataModel> doTransfer(List<Map<String, String>> dataList) {
        return Collections.emptyList();
    }
}
