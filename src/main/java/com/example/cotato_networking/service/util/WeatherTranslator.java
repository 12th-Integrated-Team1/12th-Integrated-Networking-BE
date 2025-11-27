package com.example.cotato_networking.service.util;

import org.springframework.stereotype.Component;

@Component
public class WeatherTranslator {

    public String translateToSimple(String description) {
        if (description == null) {
            return "맑음";
        }

        String lower = description.toLowerCase();

        // 맑음
        if (lower.contains("clear") || lower.equals("맑음")) {
            return "맑음";
        }

        // 구름
        if (lower.contains("cloud") || lower.contains("구름") || lower.contains("흐림")) {
            return "구름";
        }

        // 비
        if (lower.contains("rain") || lower.contains("drizzle") ||
                lower.contains("비") || lower.contains("소나기")) {
            return "비";
        }

        // 눈
        if (lower.contains("snow") || lower.contains("눈")) {
            return "눈";
        }

        // 번개
        if (lower.contains("thunder") || lower.contains("storm") ||
                lower.contains("번개") || lower.contains("천둥")) {
            return "번개";
        }

        // 바람
        if (lower.contains("wind") || lower.contains("바람")) {
            return "바람";
        }

        // 기타 (안개, 박무 등) -> 구름으로 처리
        if (lower.contains("mist") || lower.contains("fog") ||
                lower.contains("haze") || lower.contains("안개")) {
            return "구름";
        }

        return "맑음";
    }
}