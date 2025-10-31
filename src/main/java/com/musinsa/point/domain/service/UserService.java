package com.musinsa.point.domain.service;

import com.musinsa.point.domain.dto.user.CreateUserReqDto;
import com.musinsa.point.domain.entity.PointPolicy;
import com.musinsa.point.domain.entity.User;
import com.musinsa.point.domain.entity.UserPointInfo;
import com.musinsa.point.domain.repository.PointPolicyRepository;
import com.musinsa.point.domain.repository.UserPointRepository;
import com.musinsa.point.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserPointRepository userPointRepository;
    private final PointPolicyRepository pointPolicyRepository;

    @Transactional
    public void createUser(CreateUserReqDto createUserReqDTO){
        User user = User.of(createUserReqDTO.userId());
        userRepository.save(user);
        UserPointInfo userPointInfo = UserPointInfo.of(user);
        userPointRepository.save(userPointInfo);
        PointPolicy pointPolicy = PointPolicy.of(userPointInfo);
        pointPolicyRepository.save(pointPolicy);
    }

}
