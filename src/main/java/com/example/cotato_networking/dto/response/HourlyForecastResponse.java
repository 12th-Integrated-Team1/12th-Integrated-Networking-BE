package com.example.cotato_networking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HourlyForecastResponse {
    private String time;
    private Integer temperature;
    private String weatherMain;
}