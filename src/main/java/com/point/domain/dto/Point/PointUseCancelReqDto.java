package com.point.domain.dto.Point;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PointUseCancelReqDto(
        @NotBlank
        String orderKey,
        @NotNull @Positive
        Long pointCancelAmount,
        @NotBlank @Size(max = 32)
        String userId
)
{
    public static PointUseCancelReqDto of(String orderKey, Long pointCancelAmount, String userId){
        return new PointUseCancelReqDto(orderKey, pointCancelAmount, userId);
    }
}
