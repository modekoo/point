package com.point.domain.dto.Point;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PointUseReqDto(
        @NotBlank
        String orderKey,
        @NotNull @Positive
        Long pointUseAmount,
        @NotBlank @Size(max = 32)
        String userId
)
{
    public static PointUseReqDto of(String orderKey, Long pointUseAmount, String userId){
        return new PointUseReqDto(orderKey, pointUseAmount, userId);
    }
}
