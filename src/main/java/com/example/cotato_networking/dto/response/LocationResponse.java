package com.example.cotato_networking.dto.response;

import com.example.cotato_networking.domain.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LocationResponse {
    private Long id;
    private String locationName;
    private Double latitude;
    private Double longitude;
    private Boolean isPinned;

    public static LocationResponse from(Location location) {
        return new LocationResponse(
                location.getId(),
                location.getLocationName(),
                location.getLatitude(),
                location.getLongitude(),
                location.getIsPinned()
        );
    }
}
