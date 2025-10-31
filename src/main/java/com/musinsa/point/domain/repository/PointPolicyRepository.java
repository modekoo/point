package com.musinsa.point.domain.repository;

import com.musinsa.point.domain.entity.PointPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PointPolicyRepository extends JpaRepository<PointPolicy, Long> {

    @Query("""
      select pp 
        from PointPolicy pp
        join fetch pp.userPointInfo upi
        join fetch upi.user u
       where u.userId = :userId
    """)
    Optional<PointPolicy> findByUserId(@Param("userId") String userId);

}
