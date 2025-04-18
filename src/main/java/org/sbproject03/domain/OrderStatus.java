package org.sbproject03.domain;

public enum OrderStatus {
    ORDERED,      // 주문 완료
    SHIPPED,      // 배송 중
    DELIVERED,    // 배송 완료
    CANCELED;     // 주문 취소
}
