package com.musinsa.point.domain.service;

import com.musinsa.point.domain.dto.user.CreateUserReqDto;
import com.musinsa.point.domain.entity.User;
import com.musinsa.point.domain.entity.UserPointInfo;
import com.musinsa.point.domain.repository.UserPointRepository;
import com.musinsa.point.domain.repository.UserRepository;
import com.musinsa.point.exception.ApiException;
import com.musinsa.point.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserPointInfoService {

    private final UserPointRepository userPointRepository;

    @Transactional
    public UserPointInfo getUserPointInfo(String userId){
        return userPointRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public UserPointInfo setUserPointBalance(UserPointInfo userPointInfo, Long balance){
        userPointInfo.setTotalBalance(balance);
        return userPointInfo;
    }

}
