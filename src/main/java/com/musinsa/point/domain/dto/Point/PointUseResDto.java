package com.musinsa.point.domain.dto.Point;

import com.musinsa.point.domain.entity.PointUsageLink;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PointUseResDto(
        @NotBlank
        String orderKey,
        @NotNull @Positive
        Long reqPointUseAmount,
        @NotNull @Positive
        Long pointUseAmount,
        @NotNull @Positive
        Long balancePoint
)
{
    public static PointUseResDto of(String orderKey, Long pointUseAmount, Long useAmount, Long balancePoint){
        return new PointUseResDto(orderKey, pointUseAmount, useAmount, balancePoint);
    }
}
