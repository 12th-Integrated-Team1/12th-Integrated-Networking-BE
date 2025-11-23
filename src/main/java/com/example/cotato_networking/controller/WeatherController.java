package com.example.cotato_networking.controller;

import com.example.cotato_networking.domain.User;
import com.example.cotato_networking.dto.response.CurrentWeatherResponse;
import com.example.cotato_networking.dto.response.DailyForecastResponse;
import com.example.cotato_networking.dto.response.HourlyForecastResponse;
import com.example.cotato_networking.global.dto.DataResponse;
import com.example.cotato_networking.global.dto.ErrorResponse;
import com.example.cotato_networking.global.security.annotation.CurrentUser;
import com.example.cotato_networking.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "WeatherController", description = "날씨 API")
@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @Operation(summary = "현재 날씨 조회", description = "특정 위치의 현재 날씨 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "날씨 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요 (COMMON-004)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "위치를 찾을 수 없음 (LOCATION-002)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "503", description = "날씨 정보를 가져올 수 없음 (WEATHER-001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{locationId}/current")
    public ResponseEntity<DataResponse<CurrentWeatherResponse>> getCurrentWeather(
            @PathVariable Long locationId,
            @CurrentUser User user
    ) {
        CurrentWeatherResponse response = weatherService.getCurrentWeather(locationId);
        return ResponseEntity.ok(DataResponse.from(response));
    }



    @Operation(summary = "시간별 예보 조회", description = "특정 위치의 시간별 날씨 예보(3시간 간격, 약 12개)를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예보 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요 (COMMON-004)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "위치를 찾을 수 없음 (LOCATION-002)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "503", description = "날씨 정보를 가져올 수 없음 (WEATHER-001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{locationId}/hourly")
    public ResponseEntity<DataResponse<List<HourlyForecastResponse>>> getHourlyForecast(
            @PathVariable Long locationId,
            @CurrentUser User user
    ) {
        List<HourlyForecastResponse> response = weatherService.getHourlyForecast(locationId);
        return ResponseEntity.ok(DataResponse.from(response));
    }



    @Operation(summary = "주간 예보 조회", description = "특정 위치의 주간 날씨 예보(5일치)를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예보 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요 (COMMON-004)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "위치를 찾을 수 없음 (LOCATION-002)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "503", description = "날씨 정보를 가져올 수 없음 (WEATHER-001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{locationId}/daily")
    public ResponseEntity<DataResponse<List<DailyForecastResponse>>> getDailyForecast(
            @PathVariable Long locationId,
            @CurrentUser User user
    ) {
        List<DailyForecastResponse> response = weatherService.getDailyForecast(locationId);
        return ResponseEntity.ok(DataResponse.from(response));
    }
}