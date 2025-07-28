package com.example.demo.utils;

import com.alibaba.fastjson.JSON;
import com.example.demo.model.PlanDataModel;
import com.example.demo.model.common.StartTimeEndTimeModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.*;

public class RedisUtils {
    public static void storePlanData(StringRedisTemplate template, String key, Map<String, StartTimeEndTimeModel> data) {
        data.forEach((k, v) -> {
            template.opsForHash().put("plan-" + key, k, JSON.toJSONString(v));
        });
    }

    public static int storePlanData4User(StringRedisTemplate template, List<PlanDataModel> list) {
        Set<String> result = new HashSet<>();
        final Date[] date = {null};
        if (CollectionUtils.isNotEmpty(list)) {
            Map<String, Integer> shouldWorkMap = new HashMap<>();
            list.forEach(model -> {
                result.add(model.getName());
                if (!shouldWorkMap.containsKey(model.getName())) {
                    shouldWorkMap.put(model.getName(), 0);
                }
                if (model.getStartTime() != null && model.getEndTime() != null) {
                    if (date[0] == null) {
                        date[0] = model.getStartTime();
                    }
                    shouldWorkMap.put(model.getName(), shouldWorkMap.get(model.getName()) + 1);
                }
                template.opsForHash().put("user-" + model.getName(), DateCustomUtils.transFormat4Day(model.getDate()), JSON.toJSONString(model));
            });

            if (MapUtils.isNotEmpty(shouldWorkMap) && date[0] != null) {
                String mainKey = "shouldWork-" + DateCustomUtils.getMonth(date[0]);
                Map<String, String> dataMap = new HashMap<>();
                shouldWorkMap.forEach((k, v) -> dataMap.put(k, String.valueOf(v)));
                template.opsForHash().putAll(mainKey, dataMap);
            }
        }
        return result.size();
    }

    /**
     * @Description: 从Redis获取
     * @Author: liyong
     * @Date: 2025/7/28 18:44
     */
    public static Map<String, Integer> getShouldWorkMap(StringRedisTemplate template, Date date) {
        Map<String, Integer> result = new HashMap<>();
        String mainKey = "shouldWork-" + DateCustomUtils.getMonth(date);
        Map<Object, Object> entries = template.opsForHash().entries(mainKey);
        if (MapUtils.isNotEmpty(entries)) {
            entries.forEach((k, v) -> result.put(k.toString(), (Integer) v));
        }

        return result;
    }

    public static PlanDataModel getPlanData4User(StringRedisTemplate template, String name, Date date) {
        PlanDataModel result = null;
        Object o = template.opsForHash().get("user-" + name, DateCustomUtils.transFormat4Day(date));
        if (o != null) {
            result = JSON.parseObject(o.toString(), PlanDataModel.class);
        }
        return result;
    }


    public static StartTimeEndTimeModel getPlan(StringRedisTemplate template, String day, String planType) {
        return (StartTimeEndTimeModel) template.opsForHash().get(day, planType);
    }
}
