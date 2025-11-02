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
        Long pointUseAmount,
        @NotNull @Positive
        Long balancePoint,
        List<PointUsageLink> pointUsageLinkList

)
{
    public static PointUseResDto of(String orderKey, Long pointUseAmount, Long balancePoint, List<PointUsageLink> pointUsageLinkList){
        return new PointUseResDto(orderKey, pointUseAmount, balancePoint, pointUsageLinkList);
    }
}
