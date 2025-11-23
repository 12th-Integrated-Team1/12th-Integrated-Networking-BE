package com.example.cotato_networking.service.util;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WeatherDataCalculator {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    // 최저 기온 계산
    public int calculateMinTemp(List<JsonNode> dayData) {
        return dayData.stream()
                .mapToInt(node -> (int) Math.round(node.get("main").get("temp_min").asDouble()))
                .min()
                .orElse(0);
    }

    // 최고 기온 계산
    public int calculateMaxTemp(List<JsonNode> dayData) {
        return dayData.stream()
                .mapToInt(node -> (int) Math.round(node.get("main").get("temp_max").asDouble()))
                .max()
                .orElse(0);
    }

    // 시간대별 강수확률 계산
    public int calculateRainChance(List<JsonNode> dayData, int startHour, int endHour) {
        return dayData.stream()
                .filter(node -> isWithinTimeRange(node, startHour, endHour))
                .mapToInt(node -> (int) (node.get("pop").asDouble() * 100))
                .max()
                .orElse(0);
    }

    // 특정 시간대에 포함되는지 확인
    private boolean isWithinTimeRange(JsonNode node, int startHour, int endHour) {
        long timestamp = node.get("dt").asLong();
        LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(timestamp),
                SEOUL_ZONE
        );
        int hour = dateTime.getHour();
        return hour >= startHour && hour < endHour;
    }
}