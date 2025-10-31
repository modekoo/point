package com.musinsa.point.domain.repository;

import com.musinsa.point.domain.entity.PointEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointEventRepository extends JpaRepository<PointEvent, Long> {
}
