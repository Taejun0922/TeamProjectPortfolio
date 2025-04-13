package org.sbproject03.dto;

public class UpdateCartRequest {

    private String productId;  // 변경할 상품 ID
    private int quantity;      // 변경할 수량

    public UpdateCartRequest() {
    }

    public UpdateCartRequest(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
