package com.musinsa.point.domain.service;

import com.musinsa.point.domain.entity.Order;
import com.musinsa.point.domain.entity.PointEvent;
import com.musinsa.point.domain.entity.PointUsage;
import com.musinsa.point.domain.enums.PointUsageStatus;
import com.musinsa.point.domain.repository.PointUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PointUsageService {

    private final PointUsageRepository pointUsageRepository;

    public PointUsage createUsageByOrder(PointEvent pointEvent, Order order, Long usageAmount, Long usageBalance){
        PointUsage pointUsage = PointUsage.of(pointEvent, order, usageAmount, usageBalance, PointUsageStatus.USED);
        return pointUsageRepository.save(pointUsage);
    }
}
