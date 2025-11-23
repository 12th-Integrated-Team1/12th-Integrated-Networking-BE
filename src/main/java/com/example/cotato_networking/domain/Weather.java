package com.example.cotato_networking.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "weather")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Weather {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weather_id")
    private Long id;

    @Column(nullable = false)
    private Double temperature;

    @Column(name = "feels_like", nullable = false)
    private Double feelsLike;

    @Column(nullable = false)
    private Integer humidity;

    @Column(name = "wind_speed", nullable = false)
    private Double windSpeed;

    @Column(name = "weather_main", length = 50, nullable = false)
    private String weatherMain;

    @Column(name = "weather_description", length = 100, nullable = false)
    private String weatherDescription;

    @Column(name = "weather_icon", length = 10)
    private String weatherIcon;

    @Column(name = "recorded_at", length = 20, nullable = false)
    private String recordedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Builder
    public Weather(Location location, Double temperature, Double feelsLike,
                   Integer humidity, Double windSpeed, String weatherMain,
                   String weatherDescription, String weatherIcon, String recordedAt) {
        this.location = location;
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.weatherMain = weatherMain;
        this.weatherDescription = weatherDescription;
        this.weatherIcon = weatherIcon;
        this.recordedAt = recordedAt;
    }
}