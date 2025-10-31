package com.musinsa.point.domain.repository;

import com.musinsa.point.domain.entity.UserPointInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPointRepository extends JpaRepository<UserPointInfo, Long> {
    Optional<UserPointInfo> findByUser_UserId(String userId);
}
