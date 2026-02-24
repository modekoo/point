package com.point.domain.service;

import com.point.consts.PointConst;
import com.point.domain.dto.Point.*;
import com.point.domain.dto.pointUsageLink.CancelablePointDto;
import com.point.domain.entity.*;
import com.point.domain.enums.EventType;
import com.point.domain.enums.PointStatus;
import com.point.domain.enums.PointType;
import com.point.domain.enums.PointUsageType;
import com.point.dto.CommonResponseDto;
import com.point.exception.ApiException;
import com.point.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointService {

    private final PointPolicyService pointPolicyService;
    private final UserPointInfoService userPointInfoService;
    private final PointEventService pointEventService;
    private final PointItemService pointItemService;
    private final OrderService orderService;
    private final PointUsageService pointUsageService;
    private final PointUsageLinkService pointUsageLinkService;

    private final PointValidateModeService pointValidateModeService;

    /**
     * 1. 1회 한도 초과 시
     * 2. 최대 적립금액 초과시
     */
    @Transactional
    public CommonResponseDto<PointEarnResDto> earnPoint(PointEarnReqDto pointEarnReqDTO){
        PointPolicy pointPolicy = pointPolicyService.getUserPolicy(pointEarnReqDTO.userId());
        UserPointInfo userPointInfo = userPointInfoService.getUserPointInfo(pointEarnReqDTO.userId());

        //1회 한도 초과시 튕겨냄
        long earnAmount = pointValidateModeService.validOverMaxEarnLimit(pointEarnReqDTO.pointAmount(), pointPolicy.getPointEarnLimit());

        //적립 금액 초과시 튕겨냄
        earnAmount = pointValidateModeService.validOverMaxLimit(earnAmount, userPointInfo.getPointTotalBalance(), pointPolicy.getPointTotalLimit());

        //잔액 추가
        userPointInfo = userPointInfoService.setUserPointBalance(userPointInfo, earnAmount);
        PointEvent pointEvent = pointEventService.createPointEvent(EventType.EARN, userPointInfo);
        PointItem pointItem = pointItemService.createPointItem(userPointInfo, pointEvent, PointType.EARN, earnAmount, pointEarnReqDTO.manualFlag(), pointEarnReqDTO.pointExpirationDt());

        return CommonResponseDto.success(PointEarnResDto.of(pointItem.getPointItemKey(), pointEarnReqDTO.pointAmount(), earnAmount
                , userPointInfo.getPointTotalBalance(), pointPolicy.getPointTotalLimit()));
    }

    @Transactional
    public CommonResponseDto<PointCancelResDto> cancelPoint(PointCancelReqDto pointCancelReqDto){
        PointItem pointItem = pointItemService.getPointItem(pointCancelReqDto.pointItemKey());
        PointStatus pointStatus = pointItem.getPointStatus();

        if(pointStatus.isCanCancel()) {
            pointItem.setPointCancel();
            return CommonResponseDto.success(PointCancelResDto.from(pointItem));
        }
        else
            return CommonResponseDto.fail(PointCancelResDto.from(pointItem), pointStatus.name(), pointStatus.getMessage());
    }

    /**
     * 1. 총 포인트 잔여액이 차감 액수보다 작을 시
     * 2. 부분 사용(총 포인트 잔여액만큼 사용?), 튕겨내야하나
     */
    @Transactional
    public CommonResponseDto<PointUseResDto> usePoint(PointUseReqDto pointUseReqDto){

        //포인트 체크
        UserPointInfo userPointInfo = userPointInfoService.getUserPointInfo(pointUseReqDto.userId());

        long useAmount = pointValidateModeService.validUsePointOverBalance(pointUseReqDto.pointUseAmount(), userPointInfo.getPointTotalBalance());

        //주문 쌓기
        Order order = orderService.createOrder(pointUseReqDto.orderKey());

        //주문 이벤트
        PointEvent pointEvent = pointEventService.createPointEvent(EventType.USE, userPointInfo);

        //주문에 관한 포인트 사용량
        PointUsage pointUsage = pointUsageService.createUsageByOrder(pointEvent, order, useAmount);

        //주문에 관한 포인트 연결
        List<PointItem> pointItemList = pointItemService.getPointItemListByUserIdActive(userPointInfo.getUser().getUserId());

        //포인트 사용
        //화면에 뿌려줄 return값이 필요하다면
        List pointUsageLinkList = pointUse(pointItemList, pointUsage, useAmount);

        //유저 포인트 총량 차감
        userPointInfo.setTotalBalance(- useAmount);
        
        return CommonResponseDto.success(PointUseResDto.of(order.getOrderKey(), pointUseReqDto.pointUseAmount(), useAmount, userPointInfo.getPointTotalBalance()));
    }

    private <T>List<T> pointUse(List<PointItem> pointItemList, PointUsage pointUsage, Long pointUseAmount){
        List<T> pointUsageLinkList = new ArrayList<>();
        long pointToUse = pointUseAmount; //사용할 포인트
        for(PointItem pointItem : pointItemList){
            if(pointToUse <= 0) break;
            else{
                List<PointUsageLink> usedPointUsageLinkList = pointItem.getPointUsageLinkList();
                long usedSum = usedPointUsageLinkList != null ? usedPointUsageLinkList.stream().mapToLong(PointUsageLink::getPointUsageAmount).sum() : 0;
                long useAblePoint = Math.min(pointItem.getPointAmount() - usedSum, pointToUse); //사용할 포인트가 해당 포인트보다 작을 때 고려

                //PointItem이 차감 후에도 여분의 포인트를 가지고 있을 때 status는 일단 'active' 그대로 사용
                if(pointToUse > pointItem.getPointAmount() - usedSum) pointItem.setPointUsed();

                pointToUse -= useAblePoint;
                PointUsageLink pointUsageLink = pointUsageLinkService.createPointUsageLink(pointUsage, pointItem, useAblePoint);
            }
        }
        return pointUsageLinkList;
    }

    /**
     * 1. 부분 취소, 다시 사용, 부분 취소
     */
    public CommonResponseDto<PointUseCancelResDto> useCancelPoint(PointUseCancelReqDto pointUseCancelReqDto){

        PointPolicy pointPolicy = pointPolicyService.getUserPolicy(pointUseCancelReqDto.userId());
        UserPointInfo userPointInfo = pointPolicy.getUserPointInfo();

        //취소액 + 잔액이 > 적립한계치보다 클 경우
        long cancelPoint = pointValidateModeService.validCancelPointOverTotalPoint(pointUseCancelReqDto.pointCancelAmount(), userPointInfo.getPointTotalBalance(), pointPolicy.getPointTotalLimit());

        //주문번호로 header, 부분취소 row화 하기로 하여 List로
        List<PointUsage> pointUsageList = pointUsageService.getPointUsageByOrderKey(pointUseCancelReqDto.orderKey());
        long pointUsedSum = pointUsageList.stream().mapToLong(PointUsage::getPointUsageAmount).sum();

        //취소액이 주문에 사용한 포인트보다 클 경우
        cancelPoint = pointValidateModeService.validCancelPointOverUsedPoint(cancelPoint, pointUsedSum);

        //최초 주문관련 포인트 사용 원장
        PointUsage pointUsage = pointUsageList.stream()
                .filter(pointUsageItem -> pointUsageItem.getPointUsageType().equals(PointUsageType.USED))
                .findFirst()
                .orElseThrow(() -> new ApiException(ErrorCode.POINT_USAGE_NOT_FOUND));

        //주문번호로 취소가능한 포인트 조회
        List<CancelablePointDto> cancelablePointList = pointUsageLinkService.getCancelablePointList(pointUseCancelReqDto.orderKey());

        //이벤트 등록
        PointEvent useCancelPointEvent = pointEventService.createPointEvent(EventType.USE_CANCELED, userPointInfo);
        //취소원장 등록(-)
        PointUsage useCancelPointUsage = pointUsageService.createUsageByOrder(useCancelPointEvent, pointUsage.getOrder(), -cancelPoint);

        //포인트 부분취소(완전취소처럼 값을 맞춰서 쓸수는 있으나 order cancel등의 API 추가가 유효)
        //화면에 뿌려줄 return이 필요하다면
        List resultList = pointUseCancel(cancelPoint, cancelablePointList, userPointInfo, useCancelPointUsage);

        //사용자 포인트 추가(userPointInfo)
        userPointInfo.setTotalBalance(cancelPoint);
        return CommonResponseDto.success(new PointUseCancelResDto(useCancelPointUsage.getOrder().getOrderKey(), pointUseCancelReqDto.pointCancelAmount(), cancelPoint, userPointInfo.getPointTotalBalance()));
    }

    private <T>List<T> pointUseCancel(Long cancelTotalReqAmount, List<CancelablePointDto> cancelablePointList, UserPointInfo userPointInfo, PointUsage useCancelPointUsage){
        long cancelTotalAmount = cancelTotalReqAmount;
        LocalDateTime now = LocalDateTime.now();
        List<T> resultList = new ArrayList<>();
        for(CancelablePointDto cancelablePointDto : cancelablePointList){
            //취소금액
            if(cancelTotalAmount <= 0) break;
            long cancelAmount = Math.min(cancelTotalAmount, cancelablePointDto.canCancelPointAmount());

            //만료일이 지난 것
            //RE_EARN event등록, PointItem 새 등록
            if(cancelablePointDto.pointStatus().equals(PointStatus.EXPIRED) || now.isAfter(cancelablePointDto.pointExpirationDt())){
                PointEvent pointEvent = pointEventService.createPointEvent(EventType.RE_EARN, userPointInfo);
                PointItem pointItem = pointItemService.createPointItem(userPointInfo, pointEvent, PointType.RE_EARN, cancelAmount
                        , cancelablePointDto.manualFlag(), PointConst.POINT_EXPIRE_DAYS);
            }
            //만료일이 살아있는 경우 상태 변경
            else{
                PointItem usedPointItem = pointItemService.getPointItem(cancelablePointDto.pointItemKey());
                usedPointItem.setPointActive();
            }

            //조회없이 기존포인트 연결 위한 pointRef
            PointItem pointItemRef  = pointItemService.getPointItemRef(cancelablePointDto.pointItemKey());
            //포인트 사용취소 음수 등록
            //사용취소 부분은 만료처리된 pointItem에 붙여야하는지
            PointUsageLink useCancelPointUsageLink = pointUsageLinkService.createPointUsageLink(useCancelPointUsage, pointItemRef, -cancelAmount);
            cancelTotalAmount -= cancelAmount;
        }
        //화면에 리턴이 필요할경우 dto변환후 resultList.add 등
        return resultList;
    }

}
