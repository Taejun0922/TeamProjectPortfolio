package org.sbproject03.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartResponse {
    private boolean success;  // 요청 처리 성공 여부
    private int itemTotalPrice;  // 개별 상품 가격
    private int cartTotalPrice;  // 총 결제 금액
}
