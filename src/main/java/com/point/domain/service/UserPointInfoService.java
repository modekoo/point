package com.point.domain.service;

import com.point.domain.entity.UserPointInfo;
import com.point.domain.repository.UserPointRepository;
import com.point.exception.ApiException;
import com.point.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPointInfoService {

    private final UserPointRepository userPointRepository;

    public UserPointInfo getUserPointInfo(String userId){
        return userPointRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
    }

    public UserPointInfo setUserPointBalance(UserPointInfo userPointInfo, Long balance){
        userPointInfo.setTotalBalance(balance);
        return userPointInfo;
    }

}
