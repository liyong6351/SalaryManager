package com.example.demo.service;

import java.util.List;
import java.util.Map;

public interface AttendanceService {
    String uploadPlan(List<Map<String, String>> list);

    String uploadData(List<Map<String, String>> list);
}
