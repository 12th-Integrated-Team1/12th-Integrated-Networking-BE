package com.example.cotato_networking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DailyForecastResponse {
    private String date;
    private String dayOfWeek;
    private Integer minTemp;
    private Integer maxTemp;
    private String morningWeatherMain;
    private String afternoonWeatherMain;
    private Integer morningRainChance;
    private Integer afternoonRainChance;
}