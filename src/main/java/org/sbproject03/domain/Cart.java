package org.sbproject03.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"member", "cartItems"})
public class Cart {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long cartId;

  private int totalPrice = 0;  // 총 가격

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;  // 카트에 소속된 회원

  @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CartItems> cartItems = new ArrayList<>();  // 카트 아이템 목록

  // 기본 생성자는 protected로 설정되어 있음.
  // 외부에서 사용하려면 팩토리 메소드 사용

  // 멤버 정보를 받는 생성자 (팩토리 메소드 방식)
  public Cart(Member member) {
    this.member = member;
  }

  // 카트에 상품 추가
  public void addCartItem(CartItems cartItem) {
    this.cartItems.add(cartItem);
    cartItem.setCart(this);  // 카트 아이템에 카트 정보 추가
  }

  // 카트의 총 가격을 실시간으로 계산하는 메소드
  public void updateTotalPrice() {
    this.totalPrice = cartItems.stream()
            .mapToInt(CartItems::getSubtotal)
            .sum();
  }

  // 카트 객체를 생성하는 팩토리 메소드
  public static Cart createCart(Member member) {
    Cart cart = new Cart(member);
    cart.updateTotalPrice();  // 새로 생성된 카트의 총 가격 계산
    return cart;
  }
}
