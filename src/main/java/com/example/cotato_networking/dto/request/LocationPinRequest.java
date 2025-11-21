package com.example.cotato_networking.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LocationPinRequest {

    @NotNull(message = "핀 여부는 필수입니다.")
    private Boolean isPinned;
}
