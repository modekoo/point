package com.musinsa.point.dto;

import lombok.Getter;

@Getter
public class CommonResponseDto<T> {
    private T result;
    private String message = "ok";

    private CommonResponseDto(T data){
        result = data;
    }

    private CommonResponseDto(){
    }

    public static <T> CommonResponseDto<T> success(T data){
        return new CommonResponseDto<>(data);
    }

    public static <T> CommonResponseDto<T> success(){
        return new CommonResponseDto<>();
    }
}
