package com.example.demo.enums;

public enum ExcelType {
    PLAN("plan"),
    SIGN("sign");

    private final String name;

    ExcelType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
