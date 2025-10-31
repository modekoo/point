package com.musinsa.point.domain.dto.pointPolicy;

import com.musinsa.point.domain.entity.PointPolicy;
import com.musinsa.point.exception.ApiException;
import com.musinsa.point.exception.ErrorCode;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PointPolicyResDto(
    @NotBlank @Size(max = 32)
    String userId,
    @Nullable
    Long pointEarnLimit,
    @Nullable
    Long pointTotalLimit
)
{
    public PointPolicyResDto{
        if(pointEarnLimit == null || pointTotalLimit == null) throw new ApiException(ErrorCode.INTERNAL_ERROR, "user policy정책 없음");
    }

    public static PointPolicyResDto from(PointPolicy policy){
        return new PointPolicyResDto(
                policy.getUserPointInfo().getUser().getUserId()
                , policy.getPointEarnLimit()
                , policy.getPointTotalLimit()
        );
    }
}
