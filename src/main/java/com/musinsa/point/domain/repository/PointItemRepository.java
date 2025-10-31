package com.musinsa.point.domain.repository;

import com.musinsa.point.domain.entity.PointItem;
import com.musinsa.point.domain.entity.PointPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PointItemRepository extends JpaRepository<PointItem, Long> {

    @Query("""
      select pi
        from PointItem pi
        join fetch pi.userPointInfo upi
        join fetch upi.user u
       where u.userId = :userId
    """)
    Optional<List<PointItem>> findByUserId(@Param("userId") String userId);

}
