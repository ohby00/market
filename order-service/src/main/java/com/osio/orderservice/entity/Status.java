package com.osio.orderservice.entity;

public enum Status {
    READY_TO_SHIPPING,  // 배송전
    SHIPPING,           // 배송중
    DELIVERED,          // 배송완료
    RETURNING,          // 반품 중
    REFUND,             // 반품 완료
    CANCELED,           // 취소
    PAY_REQUIRED,   // 결제 화면 진입
    PAY_SUCCESS,         // 결제 완료
    PENDING             // 결제 대기
}
