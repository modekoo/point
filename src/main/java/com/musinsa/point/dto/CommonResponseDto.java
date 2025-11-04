package com.musinsa.point.dto;

import com.musinsa.point.domain.dto.Point.PointCancelResDto;
import com.musinsa.point.domain.entity.PointItem;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CommonResponseDto<T> {
    private T result;
    private String code;
    private String message = "ok";

    private CommonResponseDto(T data){
        result = data;
    }

    private CommonResponseDto(){}

    private CommonResponseDto(T data, String code, String message) {
        result = data;
        this.message = message;
        this.code = code;
    }

    private CommonResponseDto(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static <T> CommonResponseDto<T> success(T data){
        return new CommonResponseDto<>(data);
    }

    public static <T> CommonResponseDto<T> success(){
        return new CommonResponseDto<>();
    }

    public static <T> CommonResponseDto<T> fail(T data, String code, String message) {
        return new CommonResponseDto<>(data, code, message);
    }

    public static <T> CommonResponseDto<T> fail(String code, String message) {
        return new CommonResponseDto<>(code, message);
    }
}
