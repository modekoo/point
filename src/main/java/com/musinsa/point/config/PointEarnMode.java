package com.musinsa.point.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "point.earn")
@Component
public class PointEarnMode {
    private String mode;
}
