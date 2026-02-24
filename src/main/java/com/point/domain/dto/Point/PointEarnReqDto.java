package com.point.domain.dto.Point;

import com.point.consts.PointConst;
import jakarta.validation.constraints.*;

public record PointEarnReqDto (
    @NotBlank @Size(max = 32)
    String userId,
    @NotNull @Min(value = 1, message = "포인트 적립금은 1원 이상이어야 합니다.")
    @Max(value = 100_000, message = "포인트 적립금은 10만원 이하여야 합니다.")
    Long pointAmount,
    //어드민 여부 기본값 false
    boolean manualFlag,
    //포인트 만료 기간 기본값 365일
    Long pointExpirationDt
)
{
    public PointEarnReqDto {
        if(pointExpirationDt == null) pointExpirationDt = PointConst.POINT_EXPIRE_DAYS;
    }

    public PointEarnReqDto(String userId, Long pointAmount) {
        this(userId, pointAmount, false, PointConst.POINT_EXPIRE_DAYS);
    }

    @AssertTrue(message="포인트 만료 일자는 5년 이하여야 합니다.")
    public boolean isValidExpire() {
        return pointExpirationDt != null && pointExpirationDt <= PointConst.POINT_EXPIRE_DAYS * 5;
    }

    public static PointEarnReqDto of(String userId, Long pointAmount, boolean manualFlag, Long pointExpirationDt){
        return new PointEarnReqDto(userId, pointAmount, manualFlag, pointExpirationDt);
    }

    public static PointEarnReqDto of(String userId, Long pointAmount) {
        return new PointEarnReqDto(userId, pointAmount);
    }
}
