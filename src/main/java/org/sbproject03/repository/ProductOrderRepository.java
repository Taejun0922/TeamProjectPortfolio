package org.sbproject03.repository;

import org.sbproject03.domain.OrderStatus;
import org.sbproject03.domain.ProductOrder;
import org.sbproject03.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder, Long> {

    // 특정 회원이 주문한 모든 주문을 조회하는 메서드
    List<ProductOrder> findAllByMember(Member member);

    // 주문 상태를 기준으로 주문을 조회하는 메서드
    List<ProductOrder> findAllByStatus(OrderStatus status);

    // 특정 회원이 주문한 특정 주문 상태의 주문을 조회하는 메서드
    List<ProductOrder> findAllByMemberAndStatus(Member member, OrderStatus status);

    // 특정 cartId로 주문을 조회하는 메서드
    List<ProductOrder> findAllByCartId(Long cartId);

    // 회원별 주문 목록 조회 (최신 순)
    List<ProductOrder> findByMemberOrderByOrderDateDesc(Member member);

    // 페이징처리
    Page<ProductOrder> findAll(Pageable pageable);
    Page<ProductOrder> findByMember_MemberIdContaining(String memberId, Pageable pageable);

}
