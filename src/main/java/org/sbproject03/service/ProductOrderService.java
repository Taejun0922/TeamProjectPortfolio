package org.sbproject03.service;

import jakarta.servlet.http.HttpSession;
import org.sbproject03.domain.*;
import org.sbproject03.repository.CartRepository;
import org.sbproject03.repository.MemberRepository;
import org.sbproject03.repository.ProductOrderRepository;
import org.sbproject03.repository.ProductOrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductOrderService {

  @Autowired
  private HttpSession session;

  @Autowired
  private ProductOrderRepository productOrderRepository;

  @Autowired
  private ProductOrderItemRepository productOrderItemRepository;

  @Autowired
  private CartItemService cartItemService;

  @Autowired
  private CartRepository cartRepository;

  @Autowired
  private MemberRepository memberRepository;

  // 주문 저장
  public void save(ProductOrder productOrder) {
    productOrder.setStatus(OrderStatus.ORDERED);
    productOrderRepository.save(productOrder);
  }

  // 주문 항목 저장
  public void saveOrderItem(ProductOrderItem orderItem) {
    productOrderItemRepository.save(orderItem);
  }

  // ✅ 전체 장바구니 주문처리 (트랜잭션 보장)
  @Transactional
  public void placeOrder(Long cartId, Member memberParam) {
    // ✅ 영속 상태의 member로 다시 조회
    Member member = memberRepository.findById(memberParam.getId())
            .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

    List<CartItems> cartItems = cartItemService.getCartItems(cartId);
    if (cartItems == null || cartItems.isEmpty()) {
      throw new IllegalStateException("장바구니에 상품이 없습니다.");
    }

    // 주문 생성 및 저장
    ProductOrder order = new ProductOrder();
    order.setMember(member);
    order.setCartId(cartId);
    save(order);

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

    // 카트 삭제
    Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new IllegalArgumentException("카트를 찾을 수 없습니다."));
    member.getCarts().remove(cart);
    cartRepository.delete(cart);
  }

  // ✅ 단일 상품 주문 처리
  @Transactional
  public void placeSingleOrder(Member memberParam, Product product, int quantity) {
    // 회원 정보 확인
    Member member = memberRepository.findById(memberParam.getId())
            .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

    // 주문 생성
    ProductOrder order = new ProductOrder();
    order.setMember(member);
    order.setStatus(OrderStatus.ORDERED);

    // 주문 항목 생성
    ProductOrderItem orderItem = new ProductOrderItem();
    orderItem.setProduct(product);
    orderItem.setQuantity(quantity);
    orderItem.setPrice(product.getProductPrice() * quantity);

    // 주문 항목 추가
    order.addOrderItem(orderItem);

    // 주문 저장
    productOrderRepository.save(order);

    // 카트에서 해당 상품만 삭제
    Object cartIdObj = session.getAttribute("cartId");
    if (cartIdObj != null) {
      Long cartId = Long.parseLong(cartIdObj.toString());
      cartItemService.deleteByCartIdAndProductId(cartId, product.getProductId()); // 선택한 상품만 삭제
    }
  }

  // 특정 회원의 주문 내역 조회
  public List<ProductOrder> getOrdersByMember(Member member) {
    return productOrderRepository.findByMemberOrderByOrderDateDesc(member);
  }

  // 전체 주문 조회
  public List<ProductOrder> findAll() {
    return productOrderRepository.findAll();
  }

  // memberId로 주문 내역 찾기
  public List<ProductOrder> findByMemberId(String memberId) {
    Member member = memberRepository.findByMemberId(memberId)
            .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다. ID: " + memberId));

    return productOrderRepository.findByMemberOrderByOrderDateDesc(member);
  }

  // id로 주문 내역 찾기
  public List<ProductOrder> findById(Long id) {
    Member member = memberRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다. ID: " + id));

    return productOrderRepository.findByMemberOrderByOrderDateDesc(member);
  }


}
