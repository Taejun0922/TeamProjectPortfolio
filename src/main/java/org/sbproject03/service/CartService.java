package org.sbproject03.service;

import org.sbproject03.domain.Cart;
import org.sbproject03.domain.Member;
import org.sbproject03.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

  @Autowired
  private CartRepository cartRepository;

  // 카트 저장
  public void save(Cart cart) {
    cartRepository.save(cart);
  }

  // 카트 읽기 - cartId는 Long 타입이므로 Long으로 조회
  public Cart read(Long cartId) {
    return cartRepository.findById(cartId).orElse(null); // Cart는 Optional을 사용하여 조회합니다.
  }

  // 카트 삭제
  public void delete(Cart cart) {
    cartRepository.delete(cart);
  }

  // memberId로 카트 목록 조회
  public List<Cart> getCartsByMemberId(Long memberId) {
    return cartRepository.findByMemberId(memberId);
  }

  // 새로운 카트 생성
  public Cart createCart(Member member) {
    // 카트 생성은 팩토리 메서드를 통해 생성
    return Cart.createCart(member);
  }
}
