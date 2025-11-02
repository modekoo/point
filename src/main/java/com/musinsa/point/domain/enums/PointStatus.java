package com.musinsa.point.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PointStatus {
    ACTIVE("사용가능한 포인트 입니다.", true)
    , EXPIRED("만료된 포인트 입니다.", false)
    , USED("이미 사용중인 포인트 입니다.", false)
    , CANCELED("취소처리된 포인트 입니다.", false);

    private final String message;
    private final boolean canCancel;
}
