package com.point.domain.service;

import com.point.domain.entity.Order;
import com.point.domain.entity.PointEvent;
import com.point.domain.entity.PointUsage;
import com.point.domain.enums.PointUsageType;
import com.point.domain.repository.PointUsageRepository;
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
