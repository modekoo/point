package com.musinsa.point.config;

import com.musinsa.point.domain.enums.ModeType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "point.earn")
@Component
public class PointMode {
    private ModeType mode = ModeType.HARD;
}
