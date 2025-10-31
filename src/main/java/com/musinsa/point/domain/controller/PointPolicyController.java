package com.musinsa.point.domain.controller;

import com.musinsa.point.domain.dto.pointPolicy.PointPolicyReqDto;
import com.musinsa.point.domain.dto.pointPolicy.PointPolicyResDto;
import com.musinsa.point.domain.entity.PointPolicy;
import com.musinsa.point.domain.service.PointPolicyService;
import com.musinsa.point.dto.CommonResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/point/policy")
public class PointPolicyController {

    final private PointPolicyService pointPolicyService;

    @PutMapping
    public ResponseEntity<CommonResponseDto<PointPolicyResDto>> setUserPointPolicy(@RequestBody @Valid PointPolicyReqDto pointPolicyReqDto){
        PointPolicy result = pointPolicyService.setUserPolicy(pointPolicyReqDto.userId(), pointPolicyReqDto);
        return ResponseEntity.ok(CommonResponseDto.success(PointPolicyResDto.from(result)));
    }

}
