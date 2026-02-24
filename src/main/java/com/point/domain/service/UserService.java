package com.point.domain.service;

import com.point.domain.dto.user.CreateUserReqDto;
import com.point.domain.entity.PointPolicy;
import com.point.domain.entity.User;
import com.point.domain.entity.UserPointInfo;
import com.point.domain.repository.PointPolicyRepository;
import com.point.domain.repository.UserPointRepository;
import com.point.domain.repository.UserRepository;
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
