package org.sbproject03.service;

import org.sbproject03.domain.CartItems;
import org.sbproject03.domain.Product;
import org.sbproject03.domain.ProductOrder;
import org.sbproject03.domain.OrderStatus;
import org.sbproject03.repository.ProductOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductOrderService {

  @Autowired
  private ProductOrderRepository productOrderRepository;

  @Autowired
  private CartItemService cartItemService;

  @Autowired
  private ProductService productService;

  // 주문 저장 메서드 (주문 상태 포함)
  public void save(ProductOrder productOrder) {
    // 기본 주문 상태 설정
    productOrder.setStatus(OrderStatus.ORDERED);
    productOrderRepository.save(productOrder);
  }

  // 개별 상품 주문 처리 (cartId는 Long, productId는 String)
  public void orderSingleProduct(Long cartId, String productId) {
    // 장바구니에서 상품 가져오기
    CartItems cartItem = cartItemService.findByCartIdAndProductId(cartId, productId);
    if (cartItem == null) {
      throw new IllegalArgumentException("해당 상품이 장바구니에 없습니다.");
    }

    Product product = cartItem.getProduct();

    // 주문 객체 생성
    ProductOrder productOrder = new ProductOrder();
    productOrder.setProduct(product);
    productOrder.setQuantity(cartItem.getQuantity());
    productOrder.setTotalPrice(product.getProductPrice() * cartItem.getQuantity());
    productOrder.setMember(cartItem.getCart().getMember()); // Member 설정

    save(productOrder); // 주문 저장

    // 장바구니에서 해당 상품 제거
    cartItemService.deleteByCartIdAndProductId(cartId, productId);
  }
}
