package com.example.cotato_networking.service.util;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class WeatherDateFormatter {

    // 한국 표준시로 변환을 위한 상수
    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    // "11월 18일 월요일" 형식
    public String formatDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M월 d일 EEEE", Locale.KOREAN);
        return dateTime.format(formatter);
    }

    // "05:44" 형식
    public String formatTime(long timestamp) {
        LocalDateTime dateTime = timestampToDateTime(timestamp);
        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    // "09시" 형식
    public String formatHour(long timestamp) {
        LocalDateTime dateTime = timestampToDateTime(timestamp);
        return dateTime.format(DateTimeFormatter.ofPattern("HH시"));
    }

    // "2025-11-18" 형식 (날짜 키)
    public String formatDateKey(long timestamp) {
        LocalDateTime dateTime = timestampToDateTime(timestamp);
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    // "11.18" 형식
    public String formatDailyDate(String dateKey) {
        String[] parts = dateKey.split("-");
        return parts[1] + "." + parts[2];
    }

    // "오늘", "내일", "월", "화" 등
    public String formatDayOfWeek(String dateKey, int dayIndex) {
        if (dayIndex == 0) return "오늘";

        LocalDateTime dateTime = LocalDateTime.parse(dateKey + "T00:00:00");
        return dateTime.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN));
    }

    // Timestamp를 LocalDateTime으로 변환
    private LocalDateTime timestampToDateTime(long timestamp) {
        return LocalDateTime.ofInstant(
                Instant.ofEpochSecond(timestamp),
                SEOUL_ZONE
        );
    }
}