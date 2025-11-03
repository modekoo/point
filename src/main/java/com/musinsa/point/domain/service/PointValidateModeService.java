package com.musinsa.point.domain.service;

import com.musinsa.point.config.PointMode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PointValidateModeService {

    final PointMode mode;

    //mode(soft, hard)에 따라 튕겨내는 수위 결정

}
