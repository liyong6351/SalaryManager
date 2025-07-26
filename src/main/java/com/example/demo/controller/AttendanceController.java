package com.example.demo.controller;

import com.alibaba.excel.EasyExcel;
import com.example.demo.service.AttendanceService;
import com.example.demo.service.impl.AttendanceServiceImpl;
import com.example.demo.tool.ReadSheetDataListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Description: 处理考勤的controller
 * @Author: liyong
 * @Date: 2025/7/26 17:39
 */
@Controller
public class AttendanceController {

    private final AttendanceService service;

    public AttendanceController(AttendanceService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String index() {
        return "upload";
    }

    @PostMapping("/uploadPlan")
    public String uploadExcel(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        if (file.isEmpty()) {
            model.addAttribute("message", "请选择要上传的文件");
            model.addAttribute("messageType", "error");
            return "upload";
        }

        List<Map<String, String>> lists = EasyExcel.read(file.getInputStream())
                .sheet(0)
                .registerReadListener(new ReadSheetDataListener())
                .headRowNumber(0)
                .doReadSync();
        service.uploadPlan(lists);

        //0 -> 排班信息：班次A（早班）: 07:30-17:00  班次B：14:00-22:00（中班）  休: 当天休息

        model.addAttribute("message", "排班表导入成功");
        return "upload";
    }

    @PostMapping("/uploadData")
    public String uploadDataExcel(@RequestParam("file") MultipartFile file, Model model) throws IOException {

        List<Map<String, String>> lists = EasyExcel.read(file.getInputStream())
                .sheet(0)
                .registerReadListener(new ReadSheetDataListener())
                .headRowNumber(0)
                .doReadSync();
        service.uploadData(lists);
        model.addAttribute("UploadDataMessage","数据表导入成功");
        return "upload";
    }
}