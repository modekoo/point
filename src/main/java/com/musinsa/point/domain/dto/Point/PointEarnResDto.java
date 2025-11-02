package com.musinsa.point.domain.dto.Point;

public record PointEarnResDto (
    Long pointItemKey,
    Long earnPoint,
    Long balancePoint,
    Long maxLimitPoint
)
{
    public static PointEarnResDto of(Long pointItemKey, Long earnPoint, Long balancePoint, Long maxLimitPoint) {
        return new PointEarnResDto(pointItemKey, earnPoint, balancePoint, maxLimitPoint);
    }
}

