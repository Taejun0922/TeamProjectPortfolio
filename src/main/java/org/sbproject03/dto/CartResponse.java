package org.sbproject03.dto;

public class CartResponse {

    private boolean success;  // 요청 처리 성공 여부
    private int itemTotalPrice;  // 개별 상품 가격
    private int cartTotalPrice;  // 총 결제 금액

    // 기본 생성자
    public CartResponse() {}

    // 생성자
    public CartResponse(boolean success, int itemTotalPrice, int cartTotalPrice) {
        this.success = success;
        this.itemTotalPrice = itemTotalPrice;
        this.cartTotalPrice = cartTotalPrice;
    }

    // Getter 및 Setter
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getItemTotalPrice() {
        return itemTotalPrice;
    }

    public void setItemTotalPrice(int itemTotalPrice) {
        this.itemTotalPrice = itemTotalPrice;
    }

    public int getCartTotalPrice() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(int cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }
}
