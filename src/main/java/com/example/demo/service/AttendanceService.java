package com.example.demo.service;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface AttendanceService {
    String uploadPlan(List<Map<String, String>> list);

    void uploadData(List<Map<String, String>> list, HttpServletResponse response);

    void cleanData();
}
