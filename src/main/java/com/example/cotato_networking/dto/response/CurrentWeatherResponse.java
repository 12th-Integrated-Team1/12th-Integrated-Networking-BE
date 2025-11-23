package com.example.cotato_networking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CurrentWeatherResponse {
    private String locationName;
    private String date;
    private Double temperature;
    private String weatherMain;
    private Double feelsLike;
    private Integer humidity;
    private Double windSpeed;
    private Double pm10;
    private Double pm25;
    private Double uvIndex;
    private String sunrise;
}