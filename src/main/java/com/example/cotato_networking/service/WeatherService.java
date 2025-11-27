package com.example.cotato_networking.service;

import com.example.cotato_networking.domain.Location;
import com.example.cotato_networking.dto.response.CurrentWeatherResponse;
import com.example.cotato_networking.dto.response.DailyForecastResponse;
import com.example.cotato_networking.dto.response.HourlyForecastResponse;
import com.example.cotato_networking.global.exception.AppException;
import com.example.cotato_networking.global.exception.location.LocationErrorCode;
import com.example.cotato_networking.repository.LocationRepository;
import com.example.cotato_networking.service.util.WeatherDataCalculator;
import com.example.cotato_networking.service.util.WeatherDateFormatter;
import com.example.cotato_networking.service.util.WeatherTranslator;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WeatherService {

    private final LocationRepository locationRepository;
    private final OpenWeatherMapService openWeatherMapService;
    private final WeatherDateFormatter dateFormatter;
    private final WeatherDataCalculator dataCalculator;
    private final WeatherTranslator weatherTranslator;

    // 현재 날씨 조회
    public CurrentWeatherResponse getCurrentWeather(Long locationId) {
        Location location = findLocationById(locationId);

        // 외부 API 호출
        JsonNode currentWeather = openWeatherMapService.getCurrentWeather(
                location.getLatitude(),
                location.getLongitude()
        );
        JsonNode airPollution = openWeatherMapService.getAirPollution(
                location.getLatitude(),
                location.getLongitude()
        );
        JsonNode uvIndex = openWeatherMapService.getUvIndex(
                location.getLatitude(),
                location.getLongitude()
        );

        return buildCurrentWeatherResponse(location, currentWeather, airPollution, uvIndex);
    }

    // 시간별 예보 조회
    public List<HourlyForecastResponse> getHourlyForecast(Long locationId) {
        Location location = findLocationById(locationId);
        JsonNode forecast = openWeatherMapService.getForecast(
                location.getLatitude(),
                location.getLongitude()
        );

        return buildHourlyForecastList(forecast);
    }

    // 주간 예보 조회
    public List<DailyForecastResponse> getDailyForecast(Long locationId) {
        Location location = findLocationById(locationId);
        JsonNode forecast = openWeatherMapService.getForecast(
                location.getLatitude(),
                location.getLongitude()
        );

        Map<String, List<JsonNode>> dailyMap = groupByDate(forecast);
        return buildDailyForecastList(dailyMap);
    }

   // 헬퍼 메서드

    private Location findLocationById(Long locationId) {
        return locationRepository.findById(locationId)
                .orElseThrow(() -> new AppException(LocationErrorCode.NOT_FOUND));
    }

    private CurrentWeatherResponse buildCurrentWeatherResponse(
            Location location,
            JsonNode currentWeather,
            JsonNode airPollution,
            JsonNode uvIndex
    ) {
        String rawWeather = currentWeather.get("weather").get(0).get("description").asText();

        return new CurrentWeatherResponse(
                location.getLocationName(),
                dateFormatter.formatDate(LocalDateTime.now()),
                currentWeather.get("main").get("temp").asDouble(),
                weatherTranslator.translateToSimple(rawWeather),
                currentWeather.get("main").get("feels_like").asDouble(),
                currentWeather.get("main").get("humidity").asInt(),
                currentWeather.get("wind").get("speed").asDouble(),
                airPollution.get("list").get(0).get("components").get("pm10").asDouble(),
                airPollution.get("list").get(0).get("components").get("pm2_5").asDouble(),
                uvIndex.get("value").asDouble(),
                dateFormatter.formatTime(currentWeather.get("sys").get("sunrise").asLong())
        );
    }

    private List<HourlyForecastResponse> buildHourlyForecastList(JsonNode forecast) {
        List<HourlyForecastResponse> hourlyList = new ArrayList<>();
        JsonNode list = forecast.get("list");

        int count = Math.min(12, list.size());
        for (int i = 0; i < count; i++) {
            JsonNode item = list.get(i);
            String rawWeather = item.get("weather").get(0).get("description").asText();

            hourlyList.add(new HourlyForecastResponse(
                    dateFormatter.formatHour(item.get("dt").asLong()),
                    (int) Math.round(item.get("main").get("temp").asDouble()),
                    weatherTranslator.translateToSimple(rawWeather)
            ));
        }

        return hourlyList;
    }

    private Map<String, List<JsonNode>> groupByDate(JsonNode forecast) {
        Map<String, List<JsonNode>> dailyMap = new LinkedHashMap<>();
        JsonNode list = forecast.get("list");

        for (JsonNode item : list) {
            String dateKey = dateFormatter.formatDateKey(item.get("dt").asLong());
            dailyMap.computeIfAbsent(dateKey, k -> new ArrayList<>()).add(item);
        }

        return dailyMap;
    }

    private List<DailyForecastResponse> buildDailyForecastList(Map<String, List<JsonNode>> dailyMap) {
        List<DailyForecastResponse> dailyList = new ArrayList<>();
        int dayCount = 0;

        for (Map.Entry<String, List<JsonNode>> entry : dailyMap.entrySet()) {
            if (dayCount >= 5) break;

            dailyList.add(buildDailyForecastResponse(
                    entry.getKey(),
                    entry.getValue(),
                    dayCount
            ));

            dayCount++;
        }

        return dailyList;
    }

    private DailyForecastResponse buildDailyForecastResponse(
            String dateKey,
            List<JsonNode> dayData,
            int dayIndex
    ) {
        return new DailyForecastResponse(
                dateFormatter.formatDailyDate(dateKey),
                dateFormatter.formatDayOfWeek(dateKey, dayIndex),
                dataCalculator.calculateMinTemp(dayData),
                dataCalculator.calculateMaxTemp(dayData),
                getMorningWeather(dayData),
                getAfternoonWeather(dayData),
                dataCalculator.calculateRainChance(dayData, 0, 12),
                dataCalculator.calculateRainChance(dayData, 12, 24)
        );
    }

    private String getMorningWeather(List<JsonNode> dayData) {
        return dayData.stream()
                .filter(node -> {
                    long timestamp = node.get("dt").asLong();
                    LocalDateTime dt = LocalDateTime.ofInstant(
                            Instant.ofEpochSecond(timestamp),
                            ZoneId.of("Asia/Seoul")
                    );
                    int hour = dt.getHour();
                    return hour >= 6 && hour < 12;
                })
                .findFirst()
                .map(node -> {
                    String rawWeather = node.get("weather").get(0).get("description").asText();
                    return weatherTranslator.translateToSimple(rawWeather);
                })
                .orElse("맑음");
    }

    private String getAfternoonWeather(List<JsonNode> dayData) {
        return dayData.stream()
                .filter(node -> {
                    long timestamp = node.get("dt").asLong();
                    LocalDateTime dt = LocalDateTime.ofInstant(
                            Instant.ofEpochSecond(timestamp),
                            ZoneId.of("Asia/Seoul")
                    );
                    int hour = dt.getHour();
                    return hour >= 12 && hour < 18;
                })
                .findFirst()
                .map(node -> {
                    String rawWeather = node.get("weather").get(0).get("description").asText();
                    return weatherTranslator.translateToSimple(rawWeather);
                })
                .orElse("맑음");
    }
}