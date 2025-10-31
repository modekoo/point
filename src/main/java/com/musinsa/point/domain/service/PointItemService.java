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

    public PointItem createPointItem(UserPointInfo userPointInfo, PointEvent pointEvent, Long pointAmount, boolean menualFlag, Long pointExpirationDt){
        PointItem pointItem = PointItem.of(userPointInfo, pointEvent, PointType.EARN, pointAmount, menualFlag, pointExpirationDt, PointStatus.ACTIVE);
        return pointItemRepository.save(pointItem);
    }

    public List<PointItem> getPointItemList(String userId){
        return pointItemRepository.findByUserId(userId).orElse(new ArrayList<>());
    }

}
