package com.musinsa.point.domain.service;

import com.musinsa.point.domain.entity.PointEvent;
import com.musinsa.point.domain.entity.PointItem;
import com.musinsa.point.domain.entity.UserPointInfo;
import com.musinsa.point.domain.enums.PointStatus;
import com.musinsa.point.domain.enums.PointType;
import com.musinsa.point.domain.repository.PointItemRepository;
import com.musinsa.point.exception.ApiException;
import com.musinsa.point.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PointItemService {

    private final PointItemRepository pointItemRepository;

    public PointItem createPointItem(UserPointInfo userPointInfo, PointEvent pointEvent, Long pointAmount, boolean manualFlag, Long pointExpirationDt){
        PointItem pointItem = PointItem.of(userPointInfo, pointEvent, PointType.EARN, pointAmount, manualFlag, pointExpirationDt, PointStatus.ACTIVE);
        return pointItemRepository.save(pointItem);
    }

    public PointItem getPointItem(Long pointItemKey){
        return pointItemRepository.findById(pointItemKey).orElseThrow(() -> new ApiException(ErrorCode.POINT_NOT_FOUND));
    }

    public PointItem setPointCancel(Long pointItemKey){
        PointItem pointItem = getPointItem(pointItemKey);
        pointItem.pointCancel();
        return pointItem;
    }

    public List<PointItem> getPointItemList(String userId){
        List<PointItem> pointItemList = pointItemRepository.findByUserId(userId);
        return pointItemList != null ? pointItemList : new ArrayList<>();
    }

    public List<PointItem> getPointItemListByUserIdActive(String userId){
        List<PointItem> pointItemList = pointItemRepository.findByUserIdAndPointStatus(userId, PointStatus.ACTIVE);
        return pointItemList != null ? pointItemList : new ArrayList<>();
    }

}
