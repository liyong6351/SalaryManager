package com.example.demo.enums;

import lombok.Getter;

@Getter
public enum ExcelType {
    PLAN("plan"),
    SIGN("sign");

    private final String name;

    ExcelType(String name) {
        this.name = name;
    }

}
