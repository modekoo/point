package com.point.domain.repository;

import com.point.domain.entity.PointUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointUsageRepository extends JpaRepository<PointUsage, Long> {
    List<PointUsage> findByOrder_OrderKey(String orderKey);
}
