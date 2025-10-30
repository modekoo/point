package com.musinsa.point.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PointContoller<T> {

    @GetMapping("/health")
    public ResponseEntity<T> healthCheck(){
        return ResponseEntity.ok().build();
    }

}
