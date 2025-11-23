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
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WeatherService {

    private final LocationRepository locationRepository;
    private final OpenWeatherMapService openWeatherMapService;
    private final WeatherDateFormatter dateFormatter;
    private final WeatherDataCalculator dataCalculator;

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


    // helpers
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
        return new CurrentWeatherResponse(
                location.getLocationName(),
                dateFormatter.formatDate(LocalDateTime.now()),
                currentWeather.get("main").get("temp").asDouble(),
                currentWeather.get("weather").get(0).get("description").asText(),
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
            hourlyList.add(new HourlyForecastResponse(
                    dateFormatter.formatHour(item.get("dt").asLong()),
                    (int) Math.round(item.get("main").get("temp").asDouble())
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
                dataCalculator.calculateRainChance(dayData, 0, 12),
                dataCalculator.calculateRainChance(dayData, 12, 24)
        );
    }
}