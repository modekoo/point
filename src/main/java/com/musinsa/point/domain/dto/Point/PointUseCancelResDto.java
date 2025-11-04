package com.musinsa.point.domain.dto.Point;

import com.musinsa.point.domain.entity.PointUsageLink;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record PointUseCancelResDto (
        @NotBlank
        String orderKey,
        @NotNull @Positive
        Long reqPointUseCancelAmount,
        @NotNull @Positive
        Long pointUseCancelAmount,
        @NotNull @Positive
        Long balancePoint
){
}
