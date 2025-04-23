package org.sbproject03.repository;

import org.sbproject03.domain.Cart;
import org.sbproject03.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {  // Long 타입으로 수정

  // CartId로 카트를 조회하는 메소드 (이미 JpaRepository의 기본 메소드로 존재)
  Optional<Cart> findById(Long cartId);  // JpaRepository의 기본 findById 메소드를 사용

  // memberId로 카트를 조회하는 메소드
  List<Cart> findByMemberId(Long memberId);

  // 특정 회원에 대해 가장 최근에 생성된 카트 조회
  Optional<Cart> findTopByMemberOrderByCartIdDesc(Member member);
}
