package com.musinsa.point.domain.repository;

import com.musinsa.point.domain.entity.PointUsageLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointUsageLinkRepository extends JpaRepository<PointUsageLink, Long> {
}
