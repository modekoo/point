package com.musinsa.point.domain.dto.Point;

public record PointEarnResDto (
    Long earnPoint,
    Long balancePoint,
    Long maxLimitPoint
)
{
    public static PointEarnResDto of(Long earnPoint, Long balancePoint, Long maxLimitPoint) {
        return new PointEarnResDto(earnPoint, balancePoint, maxLimitPoint);
    }
}

