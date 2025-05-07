package org.sbproject03.repository;

import org.sbproject03.domain.Cart;
import org.sbproject03.domain.CartItems;
import org.sbproject03.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItems, Long> {  // CartItems 엔티티의 ID 타입이 Long으로 변경

    // cartId로 장바구니 아이템 리스트를 가져오는 메서드
    List<CartItems> findAllByCart_CartId(Long cartId);  // cartId를 Long으로 수정

    // cartId와 상품(productId)로 장바구니 아이템을 찾는 메서드
    CartItems findByCart_CartIdAndProduct_ProductId(Long cartId, String productId);  // cartId를 Long으로 수정

    // cartId 없이 특정 상품을 기준으로 장바구니 아이템을 찾는 메서드 (카트 사용하지 않고)
    CartItems findByProduct_ProductId(String productId);  // cartId 없이 상품ID로 장바구니 아이템을 찾기

    // 특정 상품과 cartId로 장바구니에서 아이템 삭제
    @Transactional
    void deleteByProductAndCart_CartId(Product product, Long cartId);  // cartId를 Long으로 수정

    // cartId와 상품(productId)로 장바구니 아이템 삭제하는 메서드
    @Transactional
    void deleteByCart_CartIdAndProduct_ProductId(Long cartId, String productId);  // cartId를 Long으로 수정

    // cartId 없이 상품ID로 장바구니에서 해당 상품 삭제하는 메서드
    @Transactional
    void deleteByProduct_ProductId(String productId);  // cartId 없이 상품ID로 삭제

    // cartId로 모든 장바구니 아이템을 삭제하는 메서드
    @Transactional
    void deleteByCart_CartId(Long cartId);  // cartId로 장바구니에 있는 모든 아이템을 삭제
}
