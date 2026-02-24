package com.point.domain.dto.Point;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

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
