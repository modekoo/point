package com.point.domain.repository;

import com.point.domain.entity.UserPointInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPointRepository extends JpaRepository<UserPointInfo, Long> {
    Optional<UserPointInfo> findByUser_UserId(String userId);
}
