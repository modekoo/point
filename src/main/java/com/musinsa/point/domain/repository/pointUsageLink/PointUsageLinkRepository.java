package com.musinsa.point.domain.repository.pointUsageLink;

import com.musinsa.point.domain.entity.PointUsageLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointUsageLinkRepository extends JpaRepository<PointUsageLink, Long> {
    List<PointUsageLink> findByPointItem_PointItemKey(Long pointItemKey);

    List<PointUsageLink> findByPointUsage_Order_OrderKey(String orderKey);
}
