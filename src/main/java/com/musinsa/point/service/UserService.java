package com.musinsa.point.service;

import com.musinsa.point.dto.ReqCreateUserDTO;
import com.musinsa.point.entity.User;
import com.musinsa.point.entity.UserPointInfo;
import com.musinsa.point.repository.UserPointRepository;
import com.musinsa.point.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserPointRepository userPointRepository;

    @Transactional
    public void createUser(ReqCreateUserDTO reqCreateUserDTO){
        User user = User.of(reqCreateUserDTO.getUserId());
        userRepository.save(user);
        UserPointInfo userPointInfo = UserPointInfo.of(user);
        userPointRepository.save(userPointInfo);
    }

}
