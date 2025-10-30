package com.musinsa.point.repository;

import com.musinsa.point.entity.PointItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointItemRepository extends JpaRepository<PointItem, Long> {
}
