package org.sbproject03.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateCartRequest {
    private String productId;  // 변경할 상품 ID
    private int quantity;      // 변경할 수량
}
