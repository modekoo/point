package com.point.domain.dto.pointUsageLink;

import com.point.domain.enums.PointStatus;

import java.time.LocalDateTime;

public record CancelablePointDto(
        Long pointItemKey,
        PointStatus pointStatus,
        LocalDateTime pointExpirationDt,
        boolean manualFlag,
        Long canCancelPointAmount
)
{
}
