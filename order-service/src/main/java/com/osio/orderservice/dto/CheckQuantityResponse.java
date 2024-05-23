package com.osio.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckQuantityResponse {
    private String body;
    private String statusCode; // 정수로 정의된 필드에서 문자열로 변경
    private int statusCodeValue; // 필요한 경우 여전히 정수 타입의 코드를 저장할 수 있음

    // 생성자, getter, setter 등 추가
}