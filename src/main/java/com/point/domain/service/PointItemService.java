package com.point.domain.service;

import com.point.domain.entity.PointEvent;
import com.point.domain.entity.PointItem;
import com.point.domain.entity.UserPointInfo;
import com.point.domain.enums.PointStatus;
import com.point.domain.enums.PointType;
import com.point.domain.repository.PointItemRepository;
import com.point.exception.ApiException;
import com.point.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PointItemService {

    private final PointItemRepository pointItemRepository;

    public PointItem createPointItem(UserPointInfo userPointInfo, PointEvent pointEvent, PointType pointType, Long pointAmount, boolean manualFlag, Long pointExpirationDt){
        PointItem pointItem = PointItem.of(userPointInfo, pointEvent, pointType, pointAmount, manualFlag, pointExpirationDt, PointStatus.ACTIVE);
        return pointItemRepository.save(pointItem);
    }

    public PointItem getPointItem(Long pointItemKey){
        return pointItemRepository.findById(pointItemKey).orElseThrow(() -> new ApiException(ErrorCode.POINT_NOT_FOUND));
    }

    public List<PointItem> getPointItemList(String userId){
        List<PointItem> pointItemList = pointItemRepository.findByUserId(userId);
        return pointItemList != null ? pointItemList : new ArrayList<>();
    }

    public List<PointItem> getPointItemListByUserIdActive(String userId){
        List<PointItem> pointItemList = pointItemRepository.findByUserIdAndPointStatus(userId, PointStatus.ACTIVE);
        return pointItemList != null ? pointItemList : new ArrayList<>();
    }

    public PointItem getPointItemRef(Long pointItemKey){
        return pointItemRepository.getReferenceById(pointItemKey);
    }

}
