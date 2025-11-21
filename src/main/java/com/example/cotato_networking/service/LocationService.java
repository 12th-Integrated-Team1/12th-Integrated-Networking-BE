package com.example.cotato_networking.service;

import com.example.cotato_networking.domain.Location;
import com.example.cotato_networking.domain.User;
import com.example.cotato_networking.dto.request.LocationCreateRequest;
import com.example.cotato_networking.dto.request.LocationPinRequest;
import com.example.cotato_networking.dto.response.LocationResponse;
import com.example.cotato_networking.global.exception.AppException;
import com.example.cotato_networking.global.exception.location.LocationErrorCode;
import com.example.cotato_networking.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LocationService {

    private final LocationRepository locationRepository;

    // 위치 등록
    public LocationResponse createLocation(User user, LocationCreateRequest request) {

        if(locationRepository.existsByUserAndLocationName(user, request.getLocationName())) {
            throw new AppException(LocationErrorCode.CONFLICT);
        }

        Location location = Location.builder()
                .locationName(request.getLocationName())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .user(user)
                .build();

        Location savedLocation = locationRepository.save(location);
        return LocationResponse.from(location);
    }

    // 위치 목록 조회
    @Transactional(readOnly = true)
    public List<LocationResponse> getLocations(User user) {
        List<Location> locations = locationRepository.findAllByUser(user);
        return locations.stream()
                .map(LocationResponse::from)
                .toList();
    }

    // 위치 핀 수정
    public LocationResponse updatePinStatus(Long locationId, LocationPinRequest request, User user) {
        Location location = findByIdAndUser(locationId, user);
        location.updatePinStatus(request.getIsPinned());
        return LocationResponse.from(location);
    }

    // 위치 삭제
    public void deleteLocation(Long locationId, User user) {
        Location location = findByIdAndUser(locationId, user);
        locationRepository.delete(location);
    }

    // id와 user 정보로 위치 조회
    private Location findByIdAndUser(Long locationId, User user) {
        return locationRepository.findByIdAndUser(locationId, user)
                .orElseThrow(() -> new AppException(LocationErrorCode.NOT_FOUND));
    }
}
