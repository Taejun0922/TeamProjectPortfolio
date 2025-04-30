package org.sbproject03.service;

import org.sbproject03.domain.*;
import org.sbproject03.repository.ProductOrderRepository;
import org.sbproject03.repository.ProductOrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductOrderService {

  @Autowired
  private ProductOrderRepository productOrderRepository;

  @Autowired
  private ProductOrderItemRepository productOrderItemRepository;

  @Autowired
  private CartItemService cartItemService;

  /**
   * 주문 저장 (기본 상태 포함)
   */
  public void save(ProductOrder productOrder) {
    productOrder.setStatus(OrderStatus.ORDERED);
    productOrderRepository.save(productOrder);
  }

  /**
   * 주문 항목 저장
   */
  public void saveOrderItem(ProductOrderItem orderItem) {
    productOrderItemRepository.save(orderItem);
  }

  /**
   * 전체 장바구니 주문 처리
   */
  public void placeOrder(Long cartId, Member member) {
    List<CartItems> cartItems = cartItemService.getCartItems(cartId);
    if (cartItems == null || cartItems.isEmpty()) {
      throw new IllegalStateException("장바구니에 상품이 없습니다.");
    }

    // 주문 생성 및 저장
    ProductOrder order = new ProductOrder();
    order.setMember(member);
    order.setCartId(cartId);
    save(order); // ID가 생성됨

    // 주문 항목 저장
    for (CartItems item : cartItems) {
      ProductOrderItem orderItem = new ProductOrderItem();
      orderItem.setOrder(order);
      orderItem.setProduct(item.getProduct());
      orderItem.setQuantity(item.getQuantity());
      orderItem.setPrice(item.getProduct().getProductPrice() * item.getQuantity());

      saveOrderItem(orderItem);
    }

    // 장바구니 비우기
    cartItemService.deleteAllByCartId(cartId);
  }
}
