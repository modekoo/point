package com.musinsa.point.domain.service;

import com.musinsa.point.domain.entity.PointItem;
import com.musinsa.point.domain.entity.PointUsage;
import com.musinsa.point.domain.entity.PointUsageLink;
import com.musinsa.point.domain.repository.pointUsageLink.PointUsageLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PointUsageLinkService {

    final private PointUsageLinkRepository pointUsageLinkRepository;

    public PointUsageLink createPointUsageLink(PointUsage pointUsage, PointItem pointItem, Long pointUsageAmount){
        return pointUsageLinkRepository.save(PointUsageLink.of(pointUsage, pointItem, pointUsageAmount));
    }

    public List<PointUsageLink> getPointUsageLinkListByPointItemKey(Long pointKey){
        return pointUsageLinkRepository.findByPointItem_PointItemKey(pointKey);
    }

    public List<PointUsageLink> getPointUsageLinkListByOrderKey(String orderKey){
        return pointUsageLinkRepository.findByPointUsage_Order_OrderKey(orderKey);
    }

}
