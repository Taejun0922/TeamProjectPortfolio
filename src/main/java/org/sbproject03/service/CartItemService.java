package org.sbproject03.service;

import org.sbproject03.domain.CartItems;
import org.sbproject03.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartItemService {
    @Autowired
    private CartItemRepository cartItemRepository;
    public List<CartItems> getCartItems(String cartRefId) {
        return cartItemRepository.findAllByCartRefId(cartRefId);
    }
    public void save(CartItems cartItems) {
        cartItemRepository.save(cartItems);
    }
    public void deleteCartItem(String productId, String cartId) {
        cartItemRepository.deleteByProductIdAndCartRefId(productId, cartId);
    }
}
