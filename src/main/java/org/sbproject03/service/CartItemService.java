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

    // 장바구니에 담긴 모든 아이템을 가져오는 메서드
    public List<CartItems> getCartItems(String cartRefId) {
        return cartItemRepository.findAllByCartRefId(cartRefId);
    }

    // 장바구니에 상품을 저장하는 메서드
    public void save(CartItems cartItems) {
        cartItemRepository.save(cartItems);
    }

    // 장바구니에서 개별 상품을 삭제하는 메서드
    public void deleteCartItem(String productId, String cartRefId) {
        Product product = productService.getProductById(productId); // 상품 정보 가져오기
        CartItems item = cartItemRepository.findByProductAndCartRefId(product, cartRefId);
        if (item != null) {
            cartItemRepository.delete(item); // 장바구니에서 해당 아이템 삭제
        }
    }

    // 장바구니에서 특정 상품을 찾는 메서드
    public CartItems findByCartIdAndProductId(String cartRefId, String productId) {
        Product product = productService.getProductById(productId);
        return cartItemRepository.findByCartRefIdAndProduct(cartRefId, product);
    }

    // 개별 상품 주문 후 장바구니에서 해당 아이템을 삭제하는 메서드
    public void deleteByCartIdAndProductId(String cartRefId, String productId) {
        Product product = productService.getProductById(productId);
        CartItems cartItem = cartItemRepository.findByCartRefIdAndProduct(cartRefId, product);
        if (cartItem != null) {
            cartItemRepository.delete(cartItem); // 장바구니에서 해당 상품 삭제
        }
    }
}
