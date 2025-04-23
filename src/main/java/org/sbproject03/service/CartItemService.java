package org.sbproject03.service;

import org.sbproject03.domain.Cart;
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

    @Autowired
    private CartService cartService;  // CartService 추가

    // 장바구니에 담긴 모든 아이템을 가져오는 메서드
    public List<CartItems> getCartItems(Long cartId) {
        return cartItemRepository.findAllByCart_CartId(cartId);  // cartId를 Long으로 수정
    }

    // 장바구니에 상품을 저장하는 메서드
    public void save(CartItems cartItems) {
        cartItemRepository.save(cartItems);
    }

    // 장바구니에서 개별 상품을 삭제하는 메서드
    public void deleteCartItem(String productId, Long cartId) {
        Product product = productService.getProductById(productId); // 상품 정보 가져오기
        CartItems item = cartItemRepository.findByCart_CartIdAndProduct_ProductId(cartId, productId);  // cartId와 productId로 찾기
        if (item != null) {
            cartItemRepository.delete(item); // 장바구니에서 해당 아이템 삭제
        }
    }

    // 장바구니에서 특정 상품을 찾는 메서드
    public CartItems findByCartIdAndProductId(Long cartId, String productId) {
        Product product = productService.getProductById(productId);
        return cartItemRepository.findByCart_CartIdAndProduct_ProductId(cartId, productId);  // cartId와 productId로 찾기
    }

    // 개별 상품 주문 후 장바구니에서 해당 아이템을 삭제하는 메서드
    public void deleteByCartIdAndProductId(Long cartId, String productId) {
        Product product = productService.getProductById(productId);
        CartItems cartItem = cartItemRepository.findByCart_CartIdAndProduct_ProductId(cartId, productId);
        if (cartItem != null) {
            cartItemRepository.delete(cartItem); // 장바구니에서 해당 상품 삭제
        }
    }
}
