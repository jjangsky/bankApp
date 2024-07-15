package com.study.bankapp.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomDateUtil {
    public static String toStringFormat(LocalDateTime localDateTime){
        // 날짜 포맷 지정
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
