package com.musinsa.point.domain.dto.Point;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PointCancelReqDto (
        @NotNull @Positive
        Long pointItemKey
)
{
    public static PointCancelReqDto of(Long pointItemKey){
        return new PointCancelReqDto(pointItemKey);
    }
}

