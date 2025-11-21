package com.example.cotato_networking.controller;

import com.example.cotato_networking.domain.User;
import com.example.cotato_networking.dto.request.LocationCreateRequest;
import com.example.cotato_networking.dto.request.LocationPinRequest;
import com.example.cotato_networking.dto.response.LocationResponse;
import com.example.cotato_networking.global.dto.DataResponse;
import com.example.cotato_networking.global.dto.ErrorResponse;
import com.example.cotato_networking.global.security.annotation.CurrentUser;
import com.example.cotato_networking.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "LocationController", description = "위치 API")
@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @Operation(summary = "위치 등록", description = "새로운 위치를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "위치 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터 (COMMON-003)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 필요 (COMMON-004)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "위치 이름 중복 (LOCATION-001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping
    public ResponseEntity<DataResponse<LocationResponse>> createLocation(
            @Valid @RequestBody LocationCreateRequest request, @CurrentUser User user
    ) {
        LocationResponse response = locationService.createLocation(user, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(DataResponse.created(response));
    }

    @Operation(summary = "위치 목록 조회", description = "사용자가 저장한 모든 위치를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "위치 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요 (COMMON-004)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping
    public ResponseEntity<DataResponse<List<LocationResponse>>> getLocations(@CurrentUser User user) {
        List<LocationResponse> response = locationService.getLocations(user);
        return ResponseEntity.ok(DataResponse.from(response));
    }


    @Operation(summary = "위치 삭제", description = "해당 위치를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "위치 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요 (COMMON-004)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 위치 없음 (LOCATION-002)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<DataResponse<Void>> deleteLocation(@PathVariable("id") Long locationId, @CurrentUser User user) {
        locationService.deleteLocation(locationId, user);
        return ResponseEntity.ok(DataResponse.ok());
    }

    @Operation(summary = "위치 핀 수정", description = "해당 위치의 핀 여부를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "핀 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터 (COMMON-003)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 필요 (COMMON-004)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 위치 없음 (LOCATION-002)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PatchMapping("/{id}/pin")
    public ResponseEntity<DataResponse<LocationResponse>> updatePinStatus(
            @PathVariable("id") Long locationId, @Valid @RequestBody LocationPinRequest request, @CurrentUser User user
    ) {
        LocationResponse response = locationService.updatePinStatus(locationId, request, user);
        return ResponseEntity.ok(DataResponse.from(response));
    }
}
