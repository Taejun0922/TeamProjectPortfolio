package org.sbproject03.service;

import org.sbproject03.domain.CartItems;
import org.sbproject03.domain.Product;
import org.sbproject03.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductService productService;

    // 장바구니에 담긴 모든 아이템을 가져오는 메서드 (카트 정보를 사용하지 않음)
    public List<CartItems> getCartItems() {
        // 카트 정보 없이 모든 장바구니 아이템을 가져옵니다.
        return cartItemRepository.findAll();
    }

    // 장바구니에 상품을 저장하는 메서드
    public void save(CartItems cartItems) {
        cartItemRepository.save(cartItems); // 장바구니에 상품 저장
    }

    // 장바구니에서 개별 상품을 삭제하는 메서드
    public void deleteCartItem(String productId) {
        // 카트 정보 없이 상품 ID로 장바구니 아이템을 찾고 삭제
        CartItems item = cartItemRepository.findByProduct_ProductId(productId);
        if (item != null) {
            cartItemRepository.delete(item); // 장바구니에서 해당 상품 삭제
        }
    }

    // 장바구니에서 특정 상품을 찾는 메서드 (상품 주문시 카트 정보 없이 처리)
    public CartItems findCartItemByProductId(String productId) {
        // 상품 ID로 카트에 담긴 상품을 찾기
        return cartItemRepository.findByProduct_ProductId(productId);
    }

    // 개별 상품 주문 후 장바구니에서 해당 아이템을 삭제하는 메서드
    public void deleteByProductId(String productId) {
        // 카트 정보 없이 상품 ID로 해당 상품을 찾아 삭제
        CartItems cartItem = cartItemRepository.findByProduct_ProductId(productId);
        if (cartItem != null) {
            cartItemRepository.delete(cartItem); // 해당 상품 삭제
        }
    }

    // 장바구니에 담긴 모든 아이템 삭제하는 메서드
    public void deleteAllItems() {
        // 장바구니에 담긴 모든 상품을 삭제
        List<CartItems> items = cartItemRepository.findAll();
        for (CartItems item : items) {
            cartItemRepository.delete(item); // 모든 아이템 삭제
        }
    }
}
