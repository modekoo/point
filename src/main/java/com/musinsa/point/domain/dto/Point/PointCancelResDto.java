package com.musinsa.point.domain.dto.Point;

import com.musinsa.point.domain.entity.PointItem;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PointCancelResDto(
        @NotNull @Positive
        Long pointItemKey,
        String pointStatus
)
{

    public static PointCancelResDto from(PointItem pointItem) {
        return new PointCancelResDto(pointItem.getPointItemKey(), pointItem.getPointStatus().name());
    }
}

