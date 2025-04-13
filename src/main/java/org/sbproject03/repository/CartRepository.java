package org.sbproject03.repository;

import org.sbproject03.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {  // Long 타입으로 수정
  Cart findByCartId(Long cartId);  // cartId를 Long으로 수정
  List<Cart> findByMemberId(Long memberId);  // memberId로 카트를 찾는 메서드
}
