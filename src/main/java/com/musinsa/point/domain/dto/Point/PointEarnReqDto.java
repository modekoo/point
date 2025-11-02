package com.musinsa.point.domain.dto.Point;

import com.musinsa.point.consts.PointConst;
import com.musinsa.point.exception.ApiException;
import com.musinsa.point.exception.ErrorCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PointEarnReqDto (
    @NotBlank @Size(max = 32)
    String userId,
    @NotNull @Positive
    Long pointAmount,
    //어드민 여부 기본값 false
    boolean manualFlag,
    //포인트 만료 기간 기본값 365일
    Long pointExpirationDt
)
{
    public PointEarnReqDto {
        if(pointExpirationDt != null && pointExpirationDt > PointConst.POINT_EXPIRE_DAYS * 5) throw new ApiException(ErrorCode.INVALID_REQUEST, "포인트 만료 일자는 5년이하여야 합니다.");
        if(pointExpirationDt == null) pointExpirationDt = PointConst.POINT_EXPIRE_DAYS;
        if(pointAmount != null && pointAmount < 1 || pointAmount > 100_000L) throw new ApiException(ErrorCode.INVALID_REQUEST, "포인트 적립금은 0원보다 커야합니다.(10만원 이하)");
    }

    public PointEarnReqDto(String userId, Long pointAmount) {
        this(userId, pointAmount, false, PointConst.POINT_EXPIRE_DAYS);
    }

    public static PointEarnReqDto of(String userId, Long pointAmount, boolean manualFlag, Long pointExpirationDt){
        return new PointEarnReqDto(userId, pointAmount, manualFlag, pointExpirationDt);
    }

    public static PointEarnReqDto of(String userId, Long pointAmount) {
        return new PointEarnReqDto(userId, pointAmount);
    }
}
