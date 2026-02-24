package com.point.domain.repository;

import com.point.domain.entity.PointItem;
import com.point.domain.enums.PointStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    //and pi.pointExpirationDt >= now 고려필요
    List<PointItem> findByUserIdAndPointStatus(@Param("userId") String userId, @Param("pointStatus")PointStatus pointStatus);

}
