package com.example.demo.utils;

import java.util.ArrayList;
import java.util.List;

public class StringCustomUtils {

    public static List<String> splitStringByFixedLength(String input, int length) {
        List<String> parts = new ArrayList<>();
        if (input == null || input.isEmpty() || length <= 0) {
            return parts;
        }
        int totalLength = input.length();
        for (int i = 0; i < totalLength; i += length) {
            int end = Math.min(i + length, totalLength);
            parts.add(input.substring(i, end));
        }
        return parts;
    }
}
