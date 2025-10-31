package com.musinsa.point.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReqCreateUserDTO {
    @NotBlank @Size(max = 32)
    private String userId;
}
