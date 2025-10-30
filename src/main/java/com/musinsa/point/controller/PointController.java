package com.musinsa.point.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PointController {

    @GetMapping("/health")
    public <T> ResponseEntity<T> healthCheck(){
        return ResponseEntity.ok().build();
    }

}
