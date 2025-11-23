package com.example.cotato_networking.service;

import com.example.cotato_networking.global.exception.AppException;
import com.example.cotato_networking.global.exception.CommonErrorCode;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OpenWeatherMapService {

    @Value("${openweather.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    // 현재 날씨 조회
    public JsonNode getCurrentWeather(double lat, double lon) {
        String url = String.format(
                "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s&units=metric&lang=kr",
                lat, lon, apiKey
        );

        try {
            return restTemplate.getForObject(url, JsonNode.class);
        } catch (Exception e) {
            throw new AppException(CommonErrorCode.WEATHER_SERVICE_UNAVAILABLE);
        }
    }

    // 대기 오염 정보 조회(미세먼지와 초미세먼저 정보)
    public JsonNode getAirPollution(double lat, double lon) {
        String url = String.format(
                "https://api.openweathermap.org/data/2.5/air_pollution?lat=%s&lon=%s&appid=%s",
                lat, lon, apiKey
        );

        try {
            return restTemplate.getForObject(url, JsonNode.class);
        } catch (Exception e) {
            throw new AppException(CommonErrorCode.WEATHER_SERVICE_UNAVAILABLE);
        }
    }

    // 자외선 지수 조회
    public JsonNode getUvIndex(double lat, double lon) {
        String url = String.format(
                "https://api.openweathermap.org/data/2.5/uvi?lat=%s&lon=%s&appid=%s",
                lat, lon, apiKey
        );

        try {
            return restTemplate.getForObject(url, JsonNode.class);
        } catch (Exception e) {
            throw new AppException(CommonErrorCode.WEATHER_SERVICE_UNAVAILABLE);
        }
    }

    // 5일 예보 조회
    public JsonNode getForecast(double lat, double lon) {
        String url = String.format(
                "https://api.openweathermap.org/data/2.5/forecast?lat=%s&lon=%s&appid=%s&units=metric&lang=kr",
                lat, lon, apiKey
        );

        try {
            return restTemplate.getForObject(url, JsonNode.class);
        } catch (Exception e) {
            throw new AppException(CommonErrorCode.WEATHER_SERVICE_UNAVAILABLE);
        }
    }
}