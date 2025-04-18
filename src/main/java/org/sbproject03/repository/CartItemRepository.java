package org.sbproject03.repository;

import org.sbproject03.domain.CartItems;
import org.sbproject03.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItems, String> {

    // cartRefId로 장바구니 아이템 리스트를 가져오는 메서드
    List<CartItems> findAllByCartRefId(String cartRefId);

    // 특정 상품과 cartRefId로 장바구니 아이템을 찾는 메서드
    CartItems findByProductAndCartRefId(Product product, String cartRefId);

    // 특정 상품과 cartRefId로 장바구니에서 아이템 삭제
    @Transactional
    void deleteByProductAndCartRefId(Product product, String cartRefId);

    // cartRefId와 상품으로 장바구니 아이템을 찾는 메서드
    CartItems findByCartRefIdAndProduct(String cartRefId, Product product);

    // cartRefId와 상품으로 장바구니 아이템 삭제하는 메서드
    @Transactional
    void deleteByCartRefIdAndProduct(String cartRefId, Product product);

    // ✅ cartRefId와 productId(String 타입)로 장바구니 아이템 찾기
    CartItems findByCartRefIdAndProductId(String cartRefId, String productId);
}
