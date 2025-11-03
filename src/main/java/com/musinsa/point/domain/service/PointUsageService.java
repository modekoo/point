package com.musinsa.point.domain.service;

import com.musinsa.point.domain.entity.Order;
import com.musinsa.point.domain.entity.PointEvent;
import com.musinsa.point.domain.entity.PointUsage;
import com.musinsa.point.domain.enums.PointUsageType;
import com.musinsa.point.domain.repository.PointUsageRepository;
import com.musinsa.point.exception.ApiException;
import com.musinsa.point.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PointUsageService {

    private final PointUsageRepository pointUsageRepository;

    public PointUsage createUsageByOrder(PointEvent pointEvent, Order order, Long pointUsageAmount){
        PointUsage pointUsage = PointUsage.of(pointEvent, order, pointUsageAmount, PointUsageType.USED);
        return pointUsageRepository.save(pointUsage);
    }

    public List<PointUsage> getPointUsageByOrderKey(String orderKey){
        return pointUsageRepository.findByOrder_OrderKey(orderKey);
    }
}
