package com.musinsa.point.dto;

import com.musinsa.point.domain.dto.Point.PointCancelResDto;
import com.musinsa.point.domain.entity.PointItem;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CommonResponseDto<T> {
    private T result;
    private String message = "ok";
    private String code;

    private CommonResponseDto(T data){
        result = data;
    }

    private CommonResponseDto(){}

    public CommonResponseDto(T data, String message, String code) {
        result = data;
        this.message = message;
        this.code = code;
    }

    public static <T> CommonResponseDto<T> success(T data){
        return new CommonResponseDto<>(data);
    }

    public static <T> CommonResponseDto<T> success(){
        return new CommonResponseDto<>();
    }

    public static <T> CommonResponseDto<T> fail(T data, String message, String code) {
        return new CommonResponseDto<>(data, message, code);
    }
}
