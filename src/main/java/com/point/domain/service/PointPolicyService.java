package com.point.domain.service;

import com.point.domain.dto.pointPolicy.PointPolicyReqDto;
import com.point.domain.entity.PointPolicy;
import com.point.domain.repository.PointPolicyRepository;
import com.point.exception.ApiException;
import com.point.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@CacheConfig(cacheNames = "pointPolicyByUserId")
@RequiredArgsConstructor
@Service
public class PointPolicyService {
    private final PointPolicyRepository pointPolicyRepository;

    @Cacheable(key = "#userId")
    public PointPolicy getUserPolicy(String userId){
        return pointPolicyRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
    }

    @CachePut(key = "#userId")
    @Transactional
    public PointPolicy setUserPolicy(String userId, PointPolicyReqDto pointPolicyReqDto){
        PointPolicy userPointPolicy = pointPolicyRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.POLICY_NOT_FOUND));

        userPointPolicy.setPolicyFrom(pointPolicyReqDto);
        return userPointPolicy;
    }

}
