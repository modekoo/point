package com.musinsa.point.domain.service;

import com.musinsa.point.config.PointEarnMode;
import com.musinsa.point.domain.dto.Point.PointEarnReqDto;
import com.musinsa.point.domain.dto.Point.PointEarnResDto;
import com.musinsa.point.domain.dto.pointPolicy.PointPolicyResDto;
import com.musinsa.point.domain.entity.PointEvent;
import com.musinsa.point.domain.entity.PointItem;
import com.musinsa.point.domain.entity.PointPolicy;
import com.musinsa.point.domain.entity.UserPointInfo;
import com.musinsa.point.domain.enums.EventType;
import com.musinsa.point.exception.ApiException;
import com.musinsa.point.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointService {

    private final PointPolicyService pointPolicyService;
    private final UserPointInfoService userPointInfoService;
    private final PointEarnMode pointEarnMode;
    private final PointEventService pointEventService;
    private final PointItemService pointItemService;

    /**
     * 1. 1회 한도 초과 시
     * 2. 최대 적립금액 초과시
     * 부분 적립(1회 한도 금액, 적립 금액 최대까지), 튕겨내야하나
     */
    @Transactional
    public PointEarnResDto earnPoint(PointEarnReqDto pointEarnReqDTO){
        PointPolicy pointPolicy = pointPolicyService.getUserPolicy(pointEarnReqDTO.userId());
        UserPointInfo userPointInfo = userPointInfoService.getUserPointInfo(pointEarnReqDTO.userId());

        log.debug("pointEarnMode = {}", pointEarnMode.getMode());

        //1회 한도 초과시 튕겨냄
        if(pointEarnReqDTO.pointAmount() > pointPolicy.getPointEarnLimit()) {
            log.info("요청 충전포인트 = {}, 1회 한도 제한 포인트 = {}", pointEarnReqDTO.pointAmount(), pointPolicy.getPointEarnLimit());
            throw new ApiException(ErrorCode.POLICY_OVER_EARN);
        }

        //적립 금액 초과시 튕겨냄
        if(userPointInfo.getPointTotalBalance() + pointEarnReqDTO.pointAmount() > pointPolicy.getPointEarnLimit()) {
            log.info("요청 충전포인트 = {}, 포인트 잔액 = {}, 포인트 한도 = {}", pointEarnReqDTO.pointAmount(), userPointInfo.getPointTotalBalance(), pointPolicy.getPointEarnLimit());
            throw new ApiException(ErrorCode.POLICY_OVER_MAX);
        }

        //잔액 추가
        userPointInfo = userPointInfoService.setUserPointBalance(userPointInfo, pointEarnReqDTO.pointAmount());
        PointEvent pointEvent = pointEventService.createPointEvent(EventType.EARN, userPointInfo);
        PointItem pointItem = pointItemService.createPointItem(userPointInfo, pointEvent, pointEarnReqDTO.pointAmount(), pointEarnReqDTO.menualFlag(), pointEarnReqDTO.pointExpirationDt());

        return PointEarnResDto.of(pointItem.getPointAmount(), userPointInfo.getPointTotalBalance(), pointPolicy.getPointTotalLimit());
    }

}
