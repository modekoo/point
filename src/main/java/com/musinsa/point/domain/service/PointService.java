package com.musinsa.point.domain.service;

import com.musinsa.point.config.PointEarnMode;
import com.musinsa.point.domain.dto.Point.*;
import com.musinsa.point.domain.entity.*;
import com.musinsa.point.domain.enums.EventType;
import com.musinsa.point.domain.enums.PointStatus;
import com.musinsa.point.dto.CommonResponseDto;
import com.musinsa.point.exception.ApiException;
import com.musinsa.point.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointService {

    private final PointPolicyService pointPolicyService;
    private final UserPointInfoService userPointInfoService;
    private final PointEarnMode pointEarnMode;
    private final PointEventService pointEventService;
    private final PointItemService pointItemService;
    private final OrderService orderService;
    private final PointUsageService pointUsageService;
    private final PointUsageLinkService pointUsageLinkService;

    /**
     * 1. 1회 한도 초과 시
     * 2. 최대 적립금액 초과시
     * 부분 적립(1회 한도 금액, 적립 금액 최대까지), 튕겨내야하나
     */
    @Transactional
    public CommonResponseDto<PointEarnResDto> earnPoint(PointEarnReqDto pointEarnReqDTO){
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
        PointItem pointItem = pointItemService.createPointItem(userPointInfo, pointEvent, pointEarnReqDTO.pointAmount(), pointEarnReqDTO.manualFlag(), pointEarnReqDTO.pointExpirationDt());

        return CommonResponseDto.success(PointEarnResDto.of(pointItem.getPointItemKey(), pointItem.getPointAmount()
                , userPointInfo.getPointTotalBalance(), pointPolicy.getPointTotalLimit()));
    }

    @Transactional
    public CommonResponseDto<PointCancelResDto> cancelPoint(PointCancelReqDto pointCancelReqDto){
        PointItem pointItem = pointItemService.getPointItem(pointCancelReqDto.pointItemKey());
        PointStatus pointStatus = pointItem.getPointStatus();

        //적립취소시 event를 쌓아야하나
        if(pointStatus.isCanCancel()) {
            pointItem = pointItemService.setPointCancel(pointItem.getPointItemKey());
            return CommonResponseDto.success(PointCancelResDto.from(pointItem));
        }
        else
            return CommonResponseDto.fail(PointCancelResDto.from(pointItem), pointStatus.getMessage(), pointStatus.name());
    }

    /**
     * 1. 총 포인트 잔여액이 차감 액수보다 작을 시
     * 2. 부분 사용(총 포인트 잔여액만큼 사용?), 튕겨내야하나
     */
    @Transactional
    public CommonResponseDto<PointUseResDto> usePoint(PointUseReqDto pointUseReqDto){
        //주문 쌓기
        Order order = orderService.createOrder(pointUseReqDto.orderKey());

        //포인트 체크
        //튕겨내야하나?
        UserPointInfo userPointInfo = userPointInfoService.getUserPointInfo(pointUseReqDto.userId());
        if(userPointInfo.getPointTotalBalance() < pointUseReqDto.pointUseAmount()) throw new ApiException(ErrorCode.INVALID_REQUEST, "유저 포인트 잔액이 사용량 하려는 포인트보다 적습니다.");

        //주문 이벤트
        PointEvent pointEvent = pointEventService.createPointEvent(EventType.USE, userPointInfo);

        //주문에 관한 포인트 사용량
        PointUsage pointUsage = pointUsageService.createUsageByOrder(pointEvent, order, pointUseReqDto.pointUseAmount(), 0L);

        //주문에 관한 포인트 연결
        List<PointItem> pointItemList = pointItemService.getPointItemListByUserIdActive(userPointInfo.getUser().getUserId());
        List<PointUsageLink> pointUsageLinkList = setPointUse(pointItemList, pointUsage, pointUseReqDto.pointUseAmount());

        //유저 포인트 총량 차감
        userPointInfo.setTotalBalance(userPointInfo.getPointTotalBalance() - pointUseReqDto.pointUseAmount());
        
        return CommonResponseDto.success(PointUseResDto.of(order.getOrderKey(), pointUseReqDto.pointUseAmount(), userPointInfo.getPointTotalBalance(), pointUsageLinkList));
    }

    public List<PointUsageLink> setPointUse(List<PointItem> pointItemList, PointUsage pointUsage, Long pointUseAmount){
        List<PointUsageLink> pointUsageLinkList = new ArrayList<>();
        long pointToUse = pointUseAmount; //사용할 포인트
        for(PointItem pointItem : pointItemList){
            if(pointToUse <= 0) break;
            else{
                List<PointUsageLink> usedPointUsageLinkList = pointUsageLinkService.getPointUsageLinkListByPointItemKey(pointItem.getPointItemKey());
                long usedSum = 0;
                //이미 사용중이라면(pointUsageLink에 있다면)
                if(usedPointUsageLinkList != null && !usedPointUsageLinkList.isEmpty()){
                    //이미 사용중인 포인트량 체크
                    usedSum = usedPointUsageLinkList.stream().mapToLong(PointUsageLink::getPointUsageAmount).sum();
                }
                Long useAblePoint = Math.min(pointItem.getPointAmount() - usedSum, pointToUse); //사용할 포인트가 해당 포인트보다 작을 때 고려
                //PointItem이 차감 후에도 여분의 포인트를 가지고 있을 때 status는 일단 'active' 그대로 사용
                if(pointToUse > pointItem.getPointAmount() - usedSum) pointItem.pointUsed();

                pointToUse -= useAblePoint;
                pointUsageLinkList.add(pointUsageLinkService.createPointUsageLink(pointUsage, pointItem, useAblePoint));
            }
        }
        return pointUsageLinkList;
    }

}
