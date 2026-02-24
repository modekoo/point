package com.point.domain.service;

import com.point.config.PointMode;
import com.point.domain.enums.ModeType;
import com.point.exception.ApiException;
import com.point.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PointValidateModeService {

    final PointMode mode;

    //mode(soft, hard)에 따라 튕겨내는 수위 결정

    /**
     * HARD일 시 요청금액이 크면 ApiException
     * SOFT일 시 요청금액이 크면 한도까지 충전
     */
    long validOverMaxEarnLimit(long reqPointAmount, long policyEarnLimit){
        if(mode.getMode().equals(ModeType.HARD)) {
            if (reqPointAmount > policyEarnLimit) {
                log.info("요청 충전포인트 = {}, 1회 한도 제한 포인트 = {}", reqPointAmount, policyEarnLimit);
                throw new ApiException(ErrorCode.POLICY_OVER_EARN);
            }
            else{
                return reqPointAmount;
            }
        }
        return policyEarnLimit;
    }

    long validUsePointOverBalance(long reqPointAmount, long totalBalance){
        if(mode.getMode().equals(ModeType.HARD)) {
            if (reqPointAmount > totalBalance) {
                log.info("요청 사용포인트 = {}, 1회 한도 제한 포인트 = {}", reqPointAmount, totalBalance);
                throw new ApiException(ErrorCode.INVALID_REQUEST, "유저 포인트 잔액이 사용량 하려는 포인트보다 적습니다.");
            }
            else{
                return reqPointAmount;
            }
        }
        return totalBalance;
    }

    /**
     * HARD일 시 요청금액이 + 잔여금액이 최대보유량보다 크면 ApiException
     * SOFT일 시 요청금액 + 잔여금액이 최대보유량보다 크면 한도까지 충전
     */
    long validOverMaxLimit(long reqPointAmount, long totalBalance, long totalLimit){
        if(mode.getMode().equals(ModeType.HARD)) {
            if (reqPointAmount + totalBalance > totalLimit) {
                log.info("요청 충전포인트 = {}, 포인트 잔액 = {}, 포인트 한도 = {}", reqPointAmount, totalBalance, totalLimit);
                throw new ApiException(ErrorCode.POLICY_OVER_MAX);
            }
            else{
                return reqPointAmount;
            }
        }
        return totalLimit - totalBalance;
    }

    long validCancelPointOverTotalPoint(long cancelPoint, long totalBalance, long totalLimit){
        if(mode.getMode().equals(ModeType.HARD)) {
            if (cancelPoint + totalBalance > totalLimit) {
                log.info("요청 취소 = {}, 포인트 잔액 = {}, 포인트 한도 = {}", cancelPoint, totalBalance, totalLimit);
                throw new ApiException(ErrorCode.INVALID_REQUEST, "포인트 취소 시 유저 포인트 최대치를 초과합니다.");
            } else {
                return cancelPoint;
            }
        }
        return totalLimit - totalBalance;
    }



    /**
     * HARD일 시 취소요청금액이 사용중인포인트보다 크면 ApiException
     * SOFT일 시 사용중인포인트만큼만 차감
     */
    long validCancelPointOverUsedPoint(long cancelPoint, long usedPoint){
        if(mode.getMode().equals(ModeType.HARD)) {
           if(cancelPoint > usedPoint){
               log.info("요청 취소 = {}, 사용중인 포인트 = {}", cancelPoint, usedPoint);
               throw new ApiException(ErrorCode.INVALID_REQUEST, "주문에 사용한 포인트를 초과하여 취소할 수 없습니다.");
           }else{
               return cancelPoint;
           }
        }
        return usedPoint;
    }
}
