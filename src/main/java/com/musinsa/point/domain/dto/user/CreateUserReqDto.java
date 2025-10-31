package com.musinsa.point.domain.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserReqDto (
    @NotBlank @Size(max = 32)
    String userId
){}
