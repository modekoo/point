package com.musinsa.point.domain.repository;

import com.musinsa.point.domain.entity.PointUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointUsageRepository extends JpaRepository<PointUsage, Long> {
}
