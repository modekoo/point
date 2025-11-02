package com.musinsa.point.domain.controller;

import com.musinsa.point.domain.dto.Point.PointCancelReqDto;
import com.musinsa.point.domain.dto.Point.PointCancelResDto;
import com.musinsa.point.domain.dto.Point.PointEarnReqDto;
import com.musinsa.point.domain.dto.Point.PointEarnResDto;
import com.musinsa.point.domain.service.PointService;
import com.musinsa.point.dto.CommonResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/point")
public class PointController {

    private final PointService pointService;

    @PostMapping("/earn")
    public ResponseEntity<CommonResponseDto<PointEarnResDto>> earnPoint(@RequestBody @Valid PointEarnReqDto pointEarnReqDTO){
        CommonResponseDto<PointEarnResDto> res = pointService.earnPoint(pointEarnReqDTO);
        return ResponseEntity.ok(res);
    }

    @PutMapping("/cancel")
    public ResponseEntity<CommonResponseDto<PointCancelResDto>> cancelPoint(@RequestBody @Valid PointCancelReqDto pointCancelReqDto){
        CommonResponseDto<PointCancelResDto> res = pointService.cancelPoint(pointCancelReqDto);
        return ResponseEntity.ok(res);
    }

}
