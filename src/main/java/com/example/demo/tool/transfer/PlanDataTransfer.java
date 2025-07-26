package com.example.demo.tool.transfer;

import com.example.demo.model.PlanDataModel;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(value = "plan")
public class PlanDataTransfer extends AbstractExcelDataTransfer<PlanDataModel> {

    @Override
    protected List<PlanDataModel> doTransfer(List<Map<String, String>> dataList) {
        Map<String,String> planAll = transferAllData(dataList.get(2));
        
        for (int i = 3; i < dataList.size(); i++) {

        }
        return Collections.emptyList();
    }

    private Map<String, String> transferAllData(Map<String, String> stringStringMap) {
        Map<String, String> result = new HashMap<>();
        result.put(stringStringMap.get(1), stringStringMap.get(1));

        return result;
    }
}
