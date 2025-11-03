package com.musinsa.point.domain.repository.pointUsageLink;

import com.musinsa.point.domain.dto.pointUsageLink.CancelablePointDto;
import com.musinsa.point.domain.entity.PointUsageLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointUsageLinkRepository extends JpaRepository<PointUsageLink, Long> {
    List<PointUsageLink> findByPointItem_PointItemKey(Long pointItemKey);

    List<PointUsageLink> findByPointUsage_Order_OrderKey(String orderKey);

    List<PointUsageLink> findByPointUsage_PointUsageKey(Long pointUsageKey);

    @Query("""
      select new com.musinsa.point.domain.dto.pointUsageLink.CancelablePointDto(
             pul.pointItem.pointItemKey
           , pul.pointItem.pointStatus
           , pul.pointItem.pointExpirationDt
           , pul.pointItem.manualFlag
           , sum(pul.pointUsageAmount)
           )
        from PointUsageLink pul
        join pul.pointUsage pu
        join pu.order o
       where o.orderKey = :orderKey
       group by pul.pointItem.pointItemKey
              , pul.pointItem.pointStatus
              , pul.pointItem.pointExpirationDt
              , pul.pointItem.manualFlag
      having sum(pul.pointUsageAmount) > 0
       order by pul.pointItem.pointItemKey asc
    """)
    List<CancelablePointDto> findCancelablePoint(@Param("orderKey") String orderKey);
}
