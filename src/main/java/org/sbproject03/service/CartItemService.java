package org.sbproject03.service;

import org.sbproject03.domain.CartItems;
import org.sbproject03.domain.Product;
import org.sbproject03.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductService productService;

    // 특정 카트에 담긴 아이템들을 가져오는 메서드
    public List<CartItems> getCartItems(Long cartId) {
        return cartItemRepository.findAllByCart_CartId(cartId);  // cartId로 필터링된 아이템들 반환
    }

    // 장바구니에 상품을 저장하는 메서드
    public void save(CartItems cartItems) {
        cartItemRepository.save(cartItems); // 장바구니에 상품 저장
    }

    // 장바구니에서 개별 상품을 삭제하는 메서드
    public void deleteCartItem(String productId) {
        // 상품 ID로 장바구니 아이템을 찾고 삭제
        CartItems item = cartItemRepository.findByProduct_ProductId(productId);
        if (item != null) {
            cartItemRepository.delete(item); // 장바구니에서 해당 상품 삭제
        }
    }

    // 장바구니에서 특정 상품을 찾는 메서드
    public CartItems findCartItemByProductId(String productId) {
        // 상품 ID로 카트에 담긴 상품을 찾기
        return cartItemRepository.findByProduct_ProductId(productId);
    }

    // 개별 상품 주문 후 장바구니에서 해당 아이템을 삭제하는 메서드
    public void deleteByProductId(String productId) {
        CartItems cartItem = cartItemRepository.findByProduct_ProductId(productId);
        if (cartItem != null) {
            cartItemRepository.delete(cartItem); // 해당 상품 삭제
        }
    }

    // 전체 주문 후 카트 삭제하는 메서드
    @Transactional
    public void deleteAllByCartId(Long cartId) {
        cartItemRepository.deleteByCart_CartId(cartId);
    }
}
