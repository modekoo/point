package com.musinsa.point.domain.dto.pointPolicy;

import com.musinsa.point.domain.entity.PointPolicy;
import com.musinsa.point.exception.ApiException;
import com.musinsa.point.exception.ErrorCode;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PointPolicyReqDto(
    @NotBlank @Size(max = 32)
    String userId,
    @Nullable
    Long pointEarnLimit,
    @Nullable
    Long pointTotalLimit)

{
    public PointPolicyReqDto{
        if(pointEarnLimit != null && (pointEarnLimit < 1 || pointEarnLimit > 100_000L)) throw new ApiException(ErrorCode.INVALID_REQUEST, "1회 한도는 1~10만원 사이입니다.");
        if(pointTotalLimit != null && pointTotalLimit < 1) throw new ApiException(ErrorCode.INVALID_REQUEST, "최대 적립 금액은 1원보다 커야합니다.");
    }


    public static PointPolicyReqDto from(PointPolicy policy){
        return new PointPolicyReqDto(
                policy.getUserPointInfo().getUser().getUserId()
                , policy.getPointEarnLimit()
                , policy.getPointTotalLimit()
        );
    }

    public static PointPolicyReqDto of(String userId, Long pointEarnLimit, Long pointTotalLimit){
        return new PointPolicyReqDto(userId, pointEarnLimit, pointTotalLimit);
    }
}
