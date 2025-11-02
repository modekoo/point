package com.musinsa.point.domain.repository;

import com.musinsa.point.domain.entity.PointItem;
import com.musinsa.point.domain.entity.PointPolicy;
import com.musinsa.point.domain.enums.PointStatus;
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
    List<PointItem> findByUserId(@Param("userId") String userId);

    @Query("""
      select pi
        from PointItem pi
       where pi.userPointInfo.user.userId = :userId
         and pi.pointStatus = :pointStatus
       order by pi.manualFlag asc, pointExpirationDt asc
    """)
    List<PointItem> findByUserIdAndPointStatus(@Param("userId") String userId, @Param("pointStatus")PointStatus pointStatus);

}
