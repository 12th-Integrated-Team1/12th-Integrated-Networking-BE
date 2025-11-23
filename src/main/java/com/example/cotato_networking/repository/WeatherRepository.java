package com.example.cotato_networking.repository;

import com.example.cotato_networking.domain.Location;
import com.example.cotato_networking.domain.Weather;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeatherRepository extends JpaRepository<Weather, Long> {
}